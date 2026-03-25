package iut.unilim.fr.back.service;

import iut.unilim.fr.back.dto.ResourceDTO;
import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.repository.InstitutionRepository;
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.repository.ResourceRepository;
import iut.unilim.fr.back.security.UserDetailsImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static iut.unilim.fr.back.controllerBack.LogController.debugCsvLogs;
import static iut.unilim.fr.back.controllerBack.LogController.writeInCsvLogs;
import static iut.unilim.fr.back.security.UserDetailsImpl.getCurrentUser;

@Service
public class ExcelResourceImportService {

    private final Integer resourceDebugId = 1;
    private final Integer pathDebugId = 2;

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private InstitutionRepository institutionRepository;

    @Transactional
    public void importResourcesFromExcel(MultipartFile file, Long institutionId) throws Exception {
        List<ResourceDTO> dtos = new ArrayList<>();
        StringBuilder logContent = new StringBuilder();
        UserDetailsImpl currentUser = getCurrentUser();
        String userName = currentUser.getUsername();
        Long userId = currentUser.getId();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                Long resolvedPathId = extractAndResolvePath(sheet, institutionId);
                if (resolvedPathId == null) {
                    continue;
                }

                int colLabel = -1;
                int colApogee = -1;
                int colFI = -1;
                int colAlt = -1;
                Map<Integer, String> colUeCoefs = new HashMap<>();

                Integer currentSemester = null;

                for (Row row : sheet) {
                    if (row == null) continue;

                    String firstCellVal = getStringValue(row.getCell(0)).trim().toUpperCase();

                    if (firstCellVal.contains("SEMESTRE")) {
                        Matcher m = Pattern.compile("SEMESTRE\\s+(\\d+)").matcher(firstCellVal);
                        if (m.find()) {
                            currentSemester = Integer.parseInt(m.group(1));
                            colLabel = -1; colApogee = -1; colFI = -1; colAlt = -1;
                            colUeCoefs.clear();
                        }
                    }

                    if (colLabel == -1) {
                        for (Cell cell : row) {
                            if (sheet.isColumnHidden(cell.getColumnIndex())) continue;

                            String val = getStringValue(cell).trim().toLowerCase();
                            int colIndex = cell.getColumnIndex();

                            if (val.contains("intitulé des ressources")) colLabel = colIndex;
                            else if (val.contains("code apogee") || val.contains("code apogée")) colApogee = colIndex;
                            else if (val.equals("formation initiale")) colFI = colIndex;
                            else if (val.equals("alternance")) colAlt = colIndex;
                            else if (val.startsWith("coefficients\nue") || val.startsWith("coefficients ue")) {
                                String ueLabel = val.replace("coefficients", "").replace("\n", "").trim().toUpperCase();
                                colUeCoefs.put(colIndex, ueLabel);
                            }
                        }
                    }

                    if (colLabel != -1 && colFI != -1) {
                        String labelCell = getStringValue(row.getCell(colLabel)).trim();

                        if (labelCell.matches("^R\\d\\.[a-zA-Z0-9.]+.*")) {
                            ResourceDTO resourceDTO = new ResourceDTO();

                            Matcher m = Pattern.compile("^(R\\d\\.[a-zA-Z0-9.]+)\\s*[|\\-]?\\s*(.*)$").matcher(labelCell);
                            if (m.find()) {
                                resourceDTO.setLabel(m.group(1).trim());
                                resourceDTO.setName(m.group(2).trim());
                            } else {
                                resourceDTO.setLabel(labelCell);
                                resourceDTO.setName(labelCell);
                            }

                            String apogeeCode = colApogee != -1 ? getStringValue(row.getCell(colApogee), "") : "";
                            resourceDTO.setApogeeCode(apogeeCode);
                            int semester = currentSemester != null ? currentSemester : 1;
                            resourceDTO.setSemester(semester);
                            resourceDTO.setInstitutionId(institutionId);
                            resourceDTO.setPathId(resolvedPathId);
                            resourceDTO.setTermsCode(" ");

                            Double initialCm = getNumericValue(row.getCell(colFI), 0.0);
                            resourceDTO.setInitialCm(initialCm);
                            Double initialTd = getNumericValue(row.getCell(colFI + 1), 0.0);
                            resourceDTO.setInitialTd(initialTd);
                            Double initialTp = getNumericValue(row.getCell(colFI + 2), 0.0);
                            resourceDTO.setInitialTp(initialTp);

                            if (colAlt != -1) {
                                Double alternanceCm = getNumericValue(row.getCell(colAlt), 0.0);
                                resourceDTO.setAlternanceCm(alternanceCm);
                                Double alternanceTd = getNumericValue(row.getCell(colAlt + 1), 0.0);
                                resourceDTO.setAlternanceTd(alternanceTd);
                                Double alternanceTp = getNumericValue(row.getCell(colAlt + 2), 0.0);
                                resourceDTO.setAlternanceTp(alternanceTp);
                            } else {
                                resourceDTO.setAlternanceCm(0.0); resourceDTO.setAlternanceTd(0.0); resourceDTO.setAlternanceTp(0.0);
                            }

                            resourceDTO.setMainTeachers(new ArrayList<>());
                            resourceDTO.setTeachers(new ArrayList<>());
                            resourceDTO.setLinkedSaesIds(new ArrayList<>());
                            resourceDTO.setLinkedSaes(new ArrayList<>());

                            List<ResourceDTO.UeCoefficientDTO> ueCoefficients = new ArrayList<>();
                            List<String> addedUes = new ArrayList<>();

                            for (Map.Entry<Integer, String> entry : colUeCoefs.entrySet()) {
                                String ueLabel = entry.getValue();

                                if (currentSemester != null && !ueLabel.matches(".*UE\\s*" + currentSemester + "\\..*")) {
                                    continue;
                                }

                                Double coef = getNumericValue(row.getCell(entry.getKey()), 0.0);
                                debugCsvLogs(resourceDebugId, "ACTUAL_RESOURCE", resourceDTO.getLabel());
                                debugCsvLogs(resourceDebugId, "IF_STATEMENT", String.valueOf(coef > 0 && !addedUes.contains(ueLabel)));
                                if (coef > 0 && !addedUes.contains(ueLabel)) {
                                    ResourceDTO.UeCoefficientDTO ueDto = new ResourceDTO.UeCoefficientDTO();
                                    ueDto.setUeLabel(ueLabel);
                                    ueDto.setCoefficient(coef);
                                    ueCoefficients.add(ueDto);

                                    StringBuilder fgyeul = new StringBuilder();
                                    for (String u : addedUes) {
                                        fgyeul.append(u).append(", ");
                                    }
                                    addedUes.add(ueLabel);
                                    debugCsvLogs(resourceDebugId , "ADDED", ueLabel);
                                    debugCsvLogs(resourceDebugId, "COEF", String.valueOf(coef));
                                    debugCsvLogs(resourceDebugId,"LIST_ADDED_UE", fgyeul.toString() + "\n");
                                }
                            }

                            resourceDTO.setUeCoefficients(ueCoefficients);
                            dtos.add(resourceDTO);
                        }
                    }
                }
            }
        }

        List<Resource> entitiesToSave = new ArrayList<>();
        int skippedCount = 0;
        for (ResourceDTO dto : dtos) {
            if (resourceRepository.existsByLabel(dto.getLabel().trim())) {
                skippedCount++;
                writeInCsvLogs("\nSkipped: " + dto.getLabel());
            } else {
                entitiesToSave.add(mapDtoToEntity(dto));
                StringBuilder logContentAlt = new  StringBuilder();

                String name = dto.getName();
                String label = dto.getLabel();
                String apogeeCode = dto.getApogeeCode();
                Integer semester = dto.getSemester();
                Long institutionID = dto.getInstitutionId();
                Long pathIdDto = dto.getPathId();
                String terms = dto.getTermsCode();
                Double CMHours = dto.getInitialCm();
                Double TDHours = dto.getInitialTd();
                Double TPHours = dto.getInitialTp();
                Double altCM = dto.getAlternanceCm();
                Double altTD = dto.getAlternanceTd();
                Double altTP = dto.getAlternanceTp();
                if (altCM != null || altTD != null || altTP != null) {
                    logContentAlt.append("Internship CM Hours : ").append(altCM).append("\n");
                    logContentAlt.append("Internship TD Hours : ").append(altTD).append("\n");
                    logContentAlt.append("Internship TP Hours : ").append(altTP).append("\n");
                }
                List<Long> mainTeacherId = dto.getMainTeachers();
                List<Long> teacherId = dto.getTeachers();
                List<Long> linkedSaesId = dto.getLinkedSaesIds();
                List<ResourceDTO.UeCoefficientDTO> ueCoefficients = dto.getUeCoefficients();
                for (ResourceDTO.UeCoefficientDTO ueCoef : ueCoefficients) {
                    logContent.append("     -UE : ").append(ueCoef.getUeLabel()).append("\n");
                    logContent.append("     -Coef : ").append(ueCoef.getCoefficient()).append("\n");
                }

                writeInCsvLogs(userName + " (" + userId + ") import a resource from a xlsx with values : \n" +
                        "Name : " + name + "\n" +
                        "Label : " + label + "\n" +
                        "Apogee Code " + apogeeCode + "\n" +
                        "Semester : " + semester + "\n" +
                        "Institution ID : " + institutionID + "\n" +
                        "Path ID : " + pathIdDto + "\n" +
                        "Terms : " + terms + "\n" +
                        "Hours CM : " + CMHours + "\n" +
                        "Hours TD : " + TDHours + "\n" +
                        "Hours TP : " + TPHours + "\n" +
                        "Coefficients :" + "\n" +logContentAlt + "\n" +
                        "Main Teacher ID : " + mainTeacherId + "\n" +
                        "Teachers IDs : " + teacherId.toString() + "\n" +
                        "Linked SAEs IDs : " + linkedSaesId + "\n" +
                        logContent + "\n");
                logContent = new StringBuilder();

            }
        }

        if (!entitiesToSave.isEmpty()) {
            // resourceRepository.saveAll(entitiesToSave);
            writeInCsvLogs("\nNormalement, enregistrement DB" + "\n" +
                    skippedCount + " enties skipped");
        } else {
            writeInCsvLogs(userName + " (" + userId + ") : No new resource to save from xslx file.");
        }
    }

    private Long extractAndResolvePath(Sheet sheet, Long institutionId) {
        String pathRawString = null;

        for (int r = 0; r < 10; r++) {
            Row row = sheet.getRow(r);
            if (row != null && row.getCell(0) != null) {
                String val = getStringValue(row.getCell(0)).trim().replaceAll("^\"|\"$", "");
                if (val.toLowerCase().contains("parcours")) {
                    pathRawString = val;
                    break;
                }
            }
        }

        if (pathRawString == null) return null;

        int pathNumber;
        String pathName;

        if (pathRawString.toLowerCase().contains("parcours commun")) {
            pathNumber = 0;
            pathName = "Parcours commun";
        } else {
            Matcher m = Pattern.compile("(?i)Parcours\\s+(\\d+)\\s*:\\s*(.*)").matcher(pathRawString);
            if (m.find()) {
                pathNumber = Integer.parseInt(m.group(1));
                pathName = m.group(2).trim();
            } else {
                pathNumber = 99;
                pathName = pathRawString;
            }
        }

        Path finalPath = pathRepository.findByInstitution_IdInstitutionAndNumber(institutionId, pathNumber)
                .orElseGet(() -> {
                    Path newPath = new Path();
                    newPath.setNumber(pathNumber);
                    newPath.setName(pathName);

                    Institution inst = institutionRepository.findById(institutionId)
                            .orElseThrow(() -> new RuntimeException("Institution ID not found for ID : " + institutionId));
                    newPath.setInstitution(inst);

                    writeInCsvLogs("A new path has been created while importing xlsx file : " + pathName + " with number : " + pathNumber + ".");
                    return pathRepository.save(newPath);
                });
        debugCsvLogs(pathDebugId, "PATH_NUMBER", finalPath.getNumber().toString());

        return finalPath.getIdPath();
    }

    private Resource mapDtoToEntity(ResourceDTO dto) {
        Resource ressource = new Resource();
        ressource.setLabel(dto.getLabel());
        ressource.setName(dto.getName());
        ressource.setApogeeCode(dto.getApogeeCode());
        ressource.setSemester(dto.getSemester());
        ressource.setDiffMultiCompetences(false);
        return ressource;
    }

    private String getStringValue(Cell cell) {
        return getStringValue(cell, "");
    }

    private String getStringValue(Cell cell, String defaultValue) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return defaultValue;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((int) cell.getNumericCellValue());
        return defaultValue;
    }

    private Double getNumericValue(Cell cell, Double defaultValue) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return defaultValue;
        if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
            try { return cell.getNumericCellValue(); } catch (Exception e) { return defaultValue; }
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().replace(",", ".").replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}