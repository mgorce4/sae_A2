package iut.unilim.fr.back.controllerBack;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static iut.unilim.fr.back.controllerBack.LogController.writeInCsvLogs;
import static iut.unilim.fr.back.service.ResourceGetterService.*;

@RestController
@RequestMapping("/api/csv")
public class CsvTransfertController {
    @Autowired
    private RessourceRepository ressourceRepository;
    @Autowired
    private RessourceSheetRepository ressourceSheetRepository;

    @Autowired
    private ResourceSheetDTOController rsDTOController;

    @GetMapping("/generate")
    public ResponseEntity<ByteArrayResource> generateCsv(@RequestParam String resourceName, @RequestParam(required = false, defaultValue = "") String userDepartment, @RequestParam String userName) {
        Optional<Ressource> resultResource = ressourceRepository.findFirstByLabelStartingWith(resourceName);
        List<ExportCsvDTO> csvContents = new ArrayList<>();

        if (resultResource.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        StringBuilder csvBuilder = new StringBuilder();
        StringBuilder logMessage = new StringBuilder(userName + " get from ResourceSheet :\n");
        // En tete
        csvBuilder.append("Département; Référence UE; Référence Ressouce; Professeur référent; SAÉs; Heures; Heures Alternance; DS; CM; TD; TP; Retour de l'équipe pédagogique; Retour étudiant; Amélioration à mettre en oeuvre\n");

        if (userDepartment.isEmpty()) {
            List<ResourceSheetDTO> resourcesSheets = rsDTOController.getResourceSheetsByResourceId(resultResource.get().getIdResource());
            for (ResourceSheetDTO res : resourcesSheets) {
                csvContents.add(getExportCsvDTO(resourceName, res));
            }
        }
        else {
            // TODO : Use API call to get the resource sheets
            List<ResourceSheetDTO> allResourceSheets = rsDTOController.getAllResourceSheets();
            List<ResourceSheetDTO> departmentResourceSheets = new ArrayList<>();

            for (ResourceSheetDTO res : allResourceSheets) {
                System.out.println("tata");
                if (Objects.equals(res.getDepartment(), userDepartment)) {
                    departmentResourceSheets.add(res);
                    System.out.println("toto");
                }
            }
            for (ResourceSheetDTO res : departmentResourceSheets) {
                csvContents.add(getExportCsvDTO(resourceName, res));
            }
        }
        for (ExportCsvDTO csvContent: csvContents) {
            csvBuilder.append(generateCsvFromResource(csvContent));
            logMessage.append(csvContent.getLogs());
        }

        byte[] csvBytes = ("\uFEFF" + csvBuilder.toString()).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        String fileName = resourceName + ".csv";

        writeInCsvLogs(logMessage.toString() + " in file " + fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }

    private ExportCsvDTO getExportCsvDTO(String resourceRef, ResourceSheetDTO res) {
        List<UeInfoDTO> ues = res.getUeReferences();
        List<SaeInfoDTO> linkedSaes = res.getLinkedSaes();
        PedagogicalContentDTO pedagogicalContentDTO = res.getPedagogicalContent();
        HoursDTO hoursDTOTeacherInternship = res.getHoursTeacherAlternance();
        ResourceTrackingDTO resourceTracking = res.getTracking();

        StringBuilder refUE = new StringBuilder();
        for (UeInfoDTO ue : ues) {
            refUE.append(ue.getLabel()).append(" ");
        }

        String profRef = res.getMainTeacher();
        String dep = res.getDepartment();
        List<String> saes = new ArrayList<>();
        for (SaeInfoDTO sae : linkedSaes) {
            saes.add(sae.getLabel());
        }

        List<Integer> hoursStudent = new ArrayList<>();
        List<Integer> hoursStudentInternship = new ArrayList<>();
        boolean isInternship = false;

        if (hoursDTOTeacherInternship != null && hoursDTOTeacherInternship.getHasAlternance()) {
            HoursDTO hoursDTOInternship = res.getHoursTeacherAlternance();
            setHoursDTO(hoursDTOInternship, hoursStudent);
            HoursDTO hoursDTOPNInternship = res.getHoursPNAlternance();
            System.out.println(hoursDTOPNInternship);
            setHoursDTO(hoursDTOPNInternship, hoursStudentInternship);
            isInternship = true;
        }


        String pedagoContentCm = createPedagoContent(pedagogicalContentDTO.getCm()).toString();
        String pedagoContentTd = createPedagoContent(pedagogicalContentDTO.getTd()).toString();
        String pedagoContentTp = createPedagoContent(pedagogicalContentDTO.getTp()).toString();
        String pedagoContentDs = createPedagoContent(pedagogicalContentDTO.getDs()).toString();
        String pedagoTeamFeedback = resourceTracking.getPedagogicalFeedback();
        String studentFeedback = resourceTracking.getStudentFeedback();
        String improvement = resourceTracking.getImprovementSuggestions();

        return new ExportCsvDTO(resourceRef, refUE.toString(), profRef, dep, saes,
                                                    hoursStudent, hoursStudentInternship, isInternship, pedagoContentCm,
                                                    pedagoContentTd, pedagoContentTp, pedagoContentDs, pedagoTeamFeedback, studentFeedback, improvement);
        
    }

    private String generateCsvFromResource(ExportCsvDTO csvContent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder saes = new StringBuilder();
        StringBuilder hours = new StringBuilder();
        StringBuilder hoursInternship = new StringBuilder();

        for (String sae : csvContent.saes()) {
            saes.append(sae).append(";");
        }
        for (Integer hour : csvContent.hoursStudent()) {
            hours.append(hour.toString()).append(";");
        }
        for (Integer hourInternship : csvContent.hoursStudentInternship()) {
            hoursInternship.append(hourInternship.toString()).append(";");
        }


        sb.append(cleanCsv(csvContent.dep()));
        sb.append(cleanCsv(csvContent.refUE()));
        sb.append(cleanCsv(csvContent.resourceName()));
        sb.append(cleanCsv(csvContent.profRef()));
        sb.append(cleanCsv(saes.toString()));
        sb.append(cleanCsv(hours.toString()));
        sb.append(cleanCsv(hoursInternship.toString()));
        sb.append(cleanCsv(csvContent.pedagoContentDs()));
        sb.append(cleanCsv(csvContent.pedagoContentCm()));
        sb.append(cleanCsv(csvContent.pedagoContentTd()));
        sb.append(cleanCsv(csvContent.pedagoContentTp()));
        sb.append(cleanCsv(csvContent.pedagoTeamFeedback()));
        sb.append(cleanCsv(csvContent.studentFeedback()));
        sb.append(cleanCsv(csvContent.improvement()));
        sb.append(cleanCsv("\n"));


        return sb.toString();
    }
    public static StringBuilder createPedagoContent(List<PedagogicalContentDTO.ContentItemDTO> pedagogicalContent) {
        StringBuilder pedagoContentBuilder = new StringBuilder();
        if (!pedagogicalContent.isEmpty()) {
            for (PedagogicalContentDTO.ContentItemDTO contentItemDTO : pedagogicalContent) {
                pedagoContentBuilder.append(contentItemDTO.getOrder()).append(". ").append(contentItemDTO.getContent()).append(", ");
            }
        }
        else {
            pedagoContentBuilder.append(PLACEHOLDER);
        }
        return pedagoContentBuilder;
    }

    private String cleanCsv(String content) {
        String returnValue = content;
        if (content==null) {
            returnValue = "";
        }
        else if (content.contains(";")) {
            returnValue = content.replace(";", ",");
        }

        return returnValue + "; ";
    }
}
