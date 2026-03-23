package iut.unilim.fr.back.controllerBack;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.ResourceRepository;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import iut.unilim.fr.back.security.UserDetailsImpl;
import iut.unilim.fr.back.service.ExcelResourceImportService;
import iut.unilim.fr.back.service.TeacherImportCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static iut.unilim.fr.back.controllerBack.LogController.writeInCsvLogs;
import static iut.unilim.fr.back.security.UserDetailsImpl.getCurrentUser;
import static iut.unilim.fr.back.service.ResourceGetterService.*;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class CsvTransfertController {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private TeacherImportCsvService teacherImportCsvService;
    @Autowired
    private UserSyncadiaRepository userSyncadiaRepository;

    @Autowired
    private ExcelResourceImportService excelResourceImportService;

    @Autowired
    private ResourceSheetDTOController rsDTOController;

    @GetMapping("/generate")
    public ResponseEntity<ByteArrayResource> generateCsv(@RequestParam String resourceName) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getId();
        String userName = currentUser.getUsername();

        Optional<UserSyncadia> user = userSyncadiaRepository.findById(userId);
        if (user.isPresent()) {

            Institution institution = user.get().getInstitution();
            String userDepartment = institution.getName();

            Optional<Resource> resultResource = resourceRepository.findFirstByLabelStartingWith(resourceName);
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
                List<ResourceSheetDTO> allResourceSheets = rsDTOController.getAllResourceSheets();
                System.out.println(allResourceSheets.size());
                List<ResourceSheetDTO> departmentResourceSheets = new ArrayList<>();

                for (ResourceSheetDTO res : allResourceSheets) {
                    if (Objects.equals(res.getDepartment(), userDepartment)) {
                        departmentResourceSheets.add(res);
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

            writeInCsvLogs(logMessage + " in file " + fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvBytes.length)
                    .body(resource);
        }
        byte[] s= HexFormat.of().formatHex("-1".getBytes()).getBytes();
        return new ResponseEntity<>(new ByteArrayResource(s), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/importTeacher")
    public ResponseEntity<?> importTeachers(
            @RequestParam("file") MultipartFile file
    ) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getId();
        String userName = currentUser.getUsername();

        try {
            if (userName.isEmpty()) {
                writeInCsvLogs(userName + "(" + userId + ") attempt to import a CSV file, but an error as occurred because he was not found.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error as occurred: User not found");
            } else {
                Institution inst = userSyncadiaRepository.findById(userId).get().getInstitution();
                Long institutionId = inst.getIdInstitution();
                teacherImportCsvService.importTeachers(file, institutionId, userName);
                writeInCsvLogs(userName + "(" + userId + ") imported from CSV file successfully");
                return ResponseEntity.ok("Import successfully");
            }


        } catch (Exception e) {
            writeInCsvLogs(currentUser + " got an error while importing professor in CSV : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error : " + e.getMessage());
        }
    }

    @PostMapping("/import-excel-resources")
    public ResponseEntity<?> importResourcesFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("semester") Long semester,
            @RequestParam("pathId") Long pathId
    ) {
        UserDetailsImpl currentUser = getCurrentUser();
        Long userId = currentUser.getId();
        String userName = currentUser.getUsername();

        try {
            if (userName.isEmpty()) {
                writeInCsvLogs(userName + "(" + userId + ") attempt to import an Excel file, but an error occurred: User not found.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred: User not found");
            }

            writeInCsvLogs(userName + " try to import an excel file, but he has no institution");
            Optional<UserSyncadia> userOpt = userSyncadiaRepository.findById(userId);
            if (userOpt.isEmpty() || userOpt.get().getInstitution() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("L'utilisateur n'est rattaché à aucune institution.");
            }

            Long institutionId = userOpt.get().getInstitution().getIdInstitution();

            excelResourceImportService.importResourcesFromExcel(file, institutionId, pathId);

            writeInCsvLogs(userName + "(" + userId + ") imported Resources from Excel");
            return ResponseEntity.ok("Import Excel des ressources réussi avec succès.");

        } catch (Exception e) {
            e.printStackTrace(); // TODO : Supp dans ver final
            writeInCsvLogs(currentUser.getUsername() + " got an error while importing Excel : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'import Excel : " + e.getMessage());
        }
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

        List<Double> hoursStudent = new ArrayList<>();
        List<Double> hoursStudentInternship = new ArrayList<>();
        boolean isInternship = false;

        if (hoursDTOTeacherInternship != null && hoursDTOTeacherInternship.getHasAlternance()) {
            HoursDTO hoursDTOInternship = res.getHoursTeacherAlternance();
            setHoursDTO(hoursDTOInternship, hoursStudent);
            HoursDTO hoursDTOPNInternship = res.getHoursPNAlternance();
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
        for (Double hour : csvContent.hoursStudent()) {
            hours.append(hour.toString()).append(";");
        }
        for (Double hourInternship : csvContent.hoursStudentInternship()) {
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

    public static String cleanCsv(String content) {
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
