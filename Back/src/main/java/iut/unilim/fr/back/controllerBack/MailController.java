package iut.unilim.fr.back.controllerBack;

import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.security.UserDetailsImpl;
import iut.unilim.fr.back.service.UserSyncadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static iut.unilim.fr.back.controllerBack.LogController.writeInMailLogs;
import static iut.unilim.fr.back.security.UserDetailsImpl.getCurrentUser;

@RestController
public class MailController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserSyncadiaService userSyncadiaService;

    @GetMapping("/api/test-mail")
    public String sendTestMail(@RequestParam(defaultValue = "") String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Test ERP");
        message.setText("Le SMTP fonctionne !");
        message.setFrom("smtp-butinfo02@unilim.fr");
        mailSender.send(message);
        writeInMailLogs("Mail sent from smtp-butinfo02@unilim.fr to " + to);
        return "Mail envoyé à " + to;
    }

    @PostMapping("/api/send-mail-to-user")
    public String sendMailToUser(@RequestParam Long userId, @RequestParam String subject, @RequestParam String message) {
        Optional<UserSyncadia> userOpt = userSyncadiaService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return "Utilisateur non trouvé";
        }
        String mail = userOpt.get().getMail();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("no-reply@syncadia.fr");
        mailSender.send(mailMessage);
        writeInMailLogs("Mail sent from no-reply@syncadia.fr to " + mail);
        return "Mail envoyé à " + mail;
    }

    @PostMapping("/api/send-mail-to-users")
    public String sendMailToUsers(@RequestBody UsersMailRequest request) {
        StringBuilder result = new StringBuilder();
        for (Long userId : request.getUserIds()) {
            Optional<UserSyncadia> userOpt = userSyncadiaService.getUserById(userId);
            if (userOpt.isPresent()) {
                String mail = userOpt.get().getMail();
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(mail);
                mailMessage.setSubject(request.getSubject());
                mailMessage.setText(request.getMessage());
                mailMessage.setFrom("no-reply@syncadia.fr");
                mailSender.send(mailMessage);
                result.append("Mail envoyé à ").append(mail).append("\n");
                writeInMailLogs("Mail sent from no-reply@syncadia.fr to " + mail);
            } else {
                UserDetailsImpl currentUser = getCurrentUser();
                String userName = currentUser.getUsername();
                Long currentUserId = currentUser.getId();
                writeInMailLogs(userName + " ("+ currentUserId + ") failed to sent a mail to a user with id = " + userId + " because it doesn't exist.");
                result.append("Utilisateur non trouvé pour l'id ").append(userId).append("\n");
            }
        }
        return result.toString();
    }

    public static class UsersMailRequest {
        private java.util.List<Long> userIds;
        private String subject;
        private String message;
        public java.util.List<Long> getUserIds() { return userIds; }
        public void setUserIds(java.util.List<Long> userIds) { this.userIds = userIds; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
