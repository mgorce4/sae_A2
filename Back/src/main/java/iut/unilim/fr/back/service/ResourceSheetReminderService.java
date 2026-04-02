package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static iut.unilim.fr.back.controllerBack.LogController.writeInMailLogs;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceSheetReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceSheetReminderService.class);

    @Autowired
    private FinalDeliveryDatesRepository finalDeliveryDatesRepository;

    @Autowired
    private ResourceSheetRepository resourceSheetRepository;

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    @Autowired
    private TeacherHoursRepository teacherHoursRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Vérifie les fiches ressources non remplies et envoie des rappels par email
     * Exécuté tous les jours à 08:00 (0 0 8 * * *)
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendResourceSheetReminders() {
        logger.info("========== Démarrage de la vérification des fiches ressources ==========");

        try {
            List<FinalDeliveryDates> deliveryDates = finalDeliveryDatesRepository.findAll();
            logger.info("Nombre d'institutions avec dates de rendu: {}", deliveryDates.size());

            if (deliveryDates.isEmpty()) {
                logger.warn("Aucune date de rendu configurée. Vérifiez la table FINAL_DELIVERY_DATES");
                return;
            }

            for (FinalDeliveryDates deliveryDate : deliveryDates) {
                logger.info("Vérification institution ID: {}", deliveryDate.getInstitution().getIdInstitution());
                checkAndSendReminders(deliveryDate);
            }

            logger.info("========== Vérification terminée avec succès ==========");
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification des fiches ressources", e);
        }
    }

    /**
     * Vérifie une institution spécifique et envoie les rappels nécessaires
     */
    private void checkAndSendReminders(FinalDeliveryDates deliveryDate) {
        Long institutionId = deliveryDate.getInstitution().getIdInstitution();
        LocalDate today = LocalDate.now();

        logger.info("--- Institution: {} (ID: {}) ---", deliveryDate.getInstitution().getName(), institutionId);
        logger.info("Date aujourd'hui: {}", today);

        // Vérifier les semestres impairs (fiches ressources S1, S3, S5)
        if (deliveryDate.getFirstDelivery() != null) {
            long daysBeforeDelivery = ChronoUnit.DAYS.between(today, deliveryDate.getFirstDelivery());
            logger.info("FirstDelivery: {} (dans {} jours)", deliveryDate.getFirstDelivery(), daysBeforeDelivery);

            if (isReminderDay(daysBeforeDelivery)) {
                logger.info("C'est un jour de rappel pour semestres impairs (1,3,5)!");
                sendRemindersForSemester(institutionId, Arrays.asList(1, 3, 5),
                    deliveryDate.getFirstDelivery(), getReminderMessage(daysBeforeDelivery));
            } else {
                logger.debug("Pas un jour de rappel pour semestres impairs");
            }
        } else {
            logger.debug("FirstDelivery non configurée");
        }

        // Vérifier les semestres pairs (fiches ressources S2, S4, S6)
        if (deliveryDate.getSecondDelivery() != null) {
            long daysBeforeDelivery = ChronoUnit.DAYS.between(today, deliveryDate.getSecondDelivery());
            logger.info("SecondDelivery: {} (dans {} jours)", deliveryDate.getSecondDelivery(), daysBeforeDelivery);

            if (isReminderDay(daysBeforeDelivery)) {
                logger.info("C'est un jour de rappel pour semestres pairs (2,4,6)!");
                sendRemindersForSemester(institutionId, Arrays.asList(2, 4, 6),
                    deliveryDate.getSecondDelivery(), getReminderMessage(daysBeforeDelivery));
            } else {
                logger.debug("Pas un jour de rappel pour semestres pairs");
            }
        } else {
            logger.debug("SecondDelivery non configurée");
        }
    }

    /**
     * Envoie des rappels aux professeurs référents
     */
    private void sendRemindersForSemester(Long institutionId, List<Integer> semesters,
                                          LocalDate deliveryDate, String reminderMessage) {
        logger.info("Recherche des fiches ressources pour semestres: {}", semesters);

        List<ResourceSheet> resourceSheets = resourceSheetRepository.findAll()
            .stream()
            .filter(rs -> rs.getResource() != null && rs.getResource().getPath() != null)
            .filter(rs -> rs.getResource().getPath().getInstitution().getIdInstitution().equals(institutionId))
            .filter(rs -> semesters.contains(rs.getResource().getSemester()))
            .collect(Collectors.toList());

        logger.info("Nombre de fiches trouvées pour cette institution et ces semestres: {}", resourceSheets.size());

        Map<UserSyncadia, List<ResourceSheet>> teacherToSheets = new HashMap<>();

        for (ResourceSheet sheet : resourceSheets) {
            logger.debug("Vérification fiche: {} (ID: {})", sheet.getResource().getLabel(), sheet.getIdResourceSheet());

            if (isResourceSheetEmpty(sheet)) {
                logger.debug("-> Fiche vide détectée");

                Optional<MainTeacherForResource> mainTeacher = mainTeacherForResourceRepository
                    .findByIdResource(sheet.getResource().getIdResource())
                    .stream()
                    .findFirst();

                if (mainTeacher.isPresent()) {
                    UserSyncadia teacher = mainTeacher.get().getUser();
                    logger.debug("-> Professeur référent: {} {} ({})", teacher.getFirstname(), teacher.getLastname(), teacher.getMail());
                    teacherToSheets.computeIfAbsent(teacher, k -> new ArrayList<>()).add(sheet);
                } else {
                    logger.warn("-> Aucun professeur référent assigné!");
                }
            } else {
                logger.debug("-> Fiche non vide (ignorée)");
            }
        }

        logger.info("Nombre de professeurs avec des fiches à remplir: {}", teacherToSheets.size());

        for (Map.Entry<UserSyncadia, List<ResourceSheet>> entry : teacherToSheets.entrySet()) {
            UserSyncadia teacher = entry.getKey();
            List<ResourceSheet> sheets = entry.getValue();

            String emailBody = buildEmailBody(teacher.getFirstname(), teacher.getLastname(),
                                            reminderMessage, sheets);

            try {
                sendEmail(teacher.getMail(), "Fiche(s) ressource à remplir", emailBody);
                logger.info("✓ Email de rappel envoyé à: {} ({} fiche(s))",
                           teacher.getMail(), sheets.size());
            } catch (Exception e) {
                logger.error("✗ Erreur lors de l'envoi du rappel à {}", teacher.getMail(), e);
            }
        }
    }

    /**
     * Envoie un email via le système SMTP
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("no-reply@syncadia.fr");
            mailSender.send(message);
            writeInMailLogs("Reminder email sent to " + to + " - " + subject);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email à " + to, e);
            writeInMailLogs("ERROR: Failed to send reminder email to " + to);
        }
    }

    /**
     * Vérifie si une fiche ressource est vide
     */
    private boolean isResourceSheetEmpty(ResourceSheet sheet) {
        List<TeacherHours> teacherHours = teacherHoursRepository.findByResourceSheet_IdResourceSheet(sheet.getIdResourceSheet());
        return teacherHours.isEmpty();
    }

    /**
     * Vérifie si c'est un jour de rappel (14, 7, 2 ou 1 jour avant)
     */
    private boolean isReminderDay(long daysBeforeDelivery) {
        return daysBeforeDelivery == 14 || daysBeforeDelivery == 7 ||
               daysBeforeDelivery == 2 || daysBeforeDelivery == 1;
    }

    /**
     * Construit le message de rappel selon le nombre de jours restants
     */
    private String getReminderMessage(long daysBeforeDelivery) {
        if (daysBeforeDelivery == 14) return "2 semaines";
        else if (daysBeforeDelivery == 7) return "1 semaine";
        else if (daysBeforeDelivery == 2) return "2 jours";
        else if (daysBeforeDelivery == 1) return "1 jour";
        return "";
    }

    /**
     * Construit le corps de l'email au format demandé
     */
    private String buildEmailBody(String firstName, String lastName, String timeRemaining,
                                  List<ResourceSheet> sheets) {
        StringBuilder body = new StringBuilder();

        body.append("Bonjour ").append(firstName).append(" ").append(lastName).append(" de la part del'équipe Syncadia!\n\n");
        body.append("Il vous reste ").append(timeRemaining).append(" pour remplir la(es) fiche(s) ressource suivante(s) :\n");

        for (ResourceSheet sheet : sheets) {
            if (sheet.getResource() != null) {
                body.append("• ").append(sheet.getResource().getLabel()).append("\n");
            }
        }

        body.append("\nVotre équipe compte sur vous !\n");
        body.append("Bonne journée !\n");
        body.append("Que le chant des cigales vous accompagne.\n\n");
        body.append("L'équipe Syncadia.");

        return body.toString();
    }
}

