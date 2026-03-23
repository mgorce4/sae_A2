package iut.unilim.fr.back.service;

import iut.unilim.fr.back.dto.ResourceDTO;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.repository.ResourceRepository;
import iut.unilim.fr.back.security.UserDetailsImpl;
import iut.unilim.fr.back.users.Teacher;
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
    // TODO : Exportation d'un parcours ( path ) en meme temps et liaison de ce parcours a la ressource
    // TODO : Verification de la dupplicite a partir du parcours

    private static final Logger log = LoggerFactory.getLogger(ExcelResourceImportService.class);
    @Autowired
    private ResourceRepository resourceRepository;

    @Transactional
    public void importResourcesFromExcel(MultipartFile file, Long institutionId, Long pathId) throws Exception {
        List<ResourceDTO> dtos = new ArrayList<>();
        StringBuilder logContent = new StringBuilder();
        UserDetailsImpl currentUser = getCurrentUser();
        String userName = currentUser.getUsername();
        Long userId = currentUser.getId();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                int colLabel = -1;
                int colApogee = -1;
                int colFI = -1;
                int colAlt = -1;
                Map<Integer, String> colUeCoefs = new HashMap<>();

                Integer currentSemester = null;

                for (Row row : sheet) {
                    if (row == null) continue;

                    String firstCellVal = getStringValue(row.getCell(0)).trim().toUpperCase();

                    // current semester
                    if (firstCellVal.contains("SEMESTRE")) {
                        Matcher m = Pattern.compile("SEMESTRE\\s+(\\d+)").matcher(firstCellVal);
                        if (m.find()) {
                            currentSemester = Integer.parseInt(m.group(1));
                            colLabel = -1; colApogee = -1; colFI = -1; colAlt = -1;
                            colUeCoefs.clear();
                        }
                    }

                    // col mapping
                    if (colLabel == -1) {
                        for (Cell cell : row) {
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
                            ResourceDTO dto = new ResourceDTO();

                            Matcher m = Pattern.compile("^(R\\d\\.[a-zA-Z0-9.]+)\\s*[|\\-]?\\s*(.*)$").matcher(labelCell);
                            if (m.find()) {
                                dto.setLabel(m.group(1).trim());
                                dto.setName(m.group(2).trim());
                            } else {
                                dto.setLabel(labelCell);
                                dto.setName(labelCell);
                            }

                            String apogeeCode = colApogee != -1 ? getStringValue(row.getCell(colApogee), "") : "";
                            dto.setApogeeCode(apogeeCode);
                            int semester = currentSemester != null ? currentSemester : 1;
                            dto.setSemester(semester);
                            dto.setInstitutionId(institutionId);
                            dto.setPathId(pathId);
                            dto.setTermsCode(" ");

                            Double initialCm = getNumericValue(row.getCell(colFI), 0.0);
                            dto.setInitialCm(initialCm);
                            Double initialTd = getNumericValue(row.getCell(colFI + 1), 0.0);
                            dto.setInitialTd(initialTd);
                            Double initialTp = getNumericValue(row.getCell(colFI + 2), 0.0);
                            dto.setInitialTp(initialTp);

                            // TODO : Verif que absence val = -1
                            if (colAlt != -1) {
                                Double alternanceCm = getNumericValue(row.getCell(colAlt), 0.0);
                                dto.setAlternanceCm(alternanceCm);
                                Double alternanceTd = getNumericValue(row.getCell(colAlt + 1), 0.0);
                                dto.setAlternanceTd(alternanceTd);
                                Double alternanceTp = getNumericValue(row.getCell(colAlt + 2), 0.0);
                                dto.setAlternanceTp(alternanceTp);
                            } else {
                                dto.setAlternanceCm(0.0); dto.setAlternanceTd(0.0); dto.setAlternanceTp(0.0);
                            }

                            dto.setMainTeachers(new ArrayList<>());
                            dto.setTeachers(new ArrayList<>());
                            dto.setLinkedSaesIds(new ArrayList<>());
                            dto.setLinkedSaes(new ArrayList<>());

                            List<ResourceDTO.UeCoefficientDTO> ueCoefficients = new ArrayList<>();
                            List<String> addedUes = new ArrayList<>();

                            for (Map.Entry<Integer, String> entry : colUeCoefs.entrySet()) {
                                String ueLabel = entry.getValue();

                                if (currentSemester != null && !ueLabel.matches(".*UE\\s*" + currentSemester + "\\..*")) {
                                    continue;
                                }

                                Double coef = getNumericValue(row.getCell(entry.getKey()), 0.0);
                                debugCsvLogs(1, "ACTUAL_RESOURCE", dto.getLabel());
                                debugCsvLogs(1, "IF_STATEMENT", String.valueOf(coef > 0 && !addedUes.contains(ueLabel)));
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
                                    debugCsvLogs(1 , "ADDED", ueLabel);
                                    debugCsvLogs(1, "COEF", String.valueOf(coef));
                                    debugCsvLogs(1,"LIST_ADDED_UE", fgyeul.toString() + "\n");
                                }
                            }

                            dto.setUeCoefficients(ueCoefficients);
                            dtos.add(dto);
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
            writeInCsvLogs("Aucune nouvelle ressource à enregistrer (toutes étaient déjà présentes).");
        }
    }

    private Resource mapDtoToEntity(ResourceDTO dto) {
        Resource ressource = new Resource();
        ressource.setLabel(dto.getLabel());
        ressource.setName(dto.getName());
        ressource.setApogeeCode(dto.getApogeeCode());
        ressource.setSemester(dto.getSemester());
        ressource.setDiffMultiCompetences(false);
        // TODO : Doit y avoir + de choses a mettre
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