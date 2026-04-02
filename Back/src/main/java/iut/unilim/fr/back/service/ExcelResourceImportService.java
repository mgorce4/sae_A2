package iut.unilim.fr.back.service;

import iut.unilim.fr.back.dto.ResourceDTO;
import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.repository.InstitutionRepository;
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.repository.ResourceRepository;
import iut.unilim.fr.back.repository.UERepository;
import iut.unilim.fr.back.security.UserDetailsImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static iut.unilim.fr.back.controllerBack.LogController.debugCsvLogs;
import static iut.unilim.fr.back.controllerBack.LogController.writeInCsvLogs;
import static iut.unilim.fr.back.security.UserDetailsImpl.getCurrentUser;

@Service
public class ExcelResourceImportService {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private PathRepository pathRepository;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private UERepository ueRepository;

    int first = 1;
    int second = 2;
    int notExistingIndex = -first;
    int firstOccurence = 0;

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

                int colLabel = notExistingIndex;
                int colApogee = notExistingIndex;
                int colFI = notExistingIndex;
                int colAlt = notExistingIndex;
                double defaultValue = 0.0;
                int indexFirstRow = 0;
                Map<Integer, String> colUeCoefs = new HashMap<>();

                Integer currentSemester = null;

                Map<String, UE> parsedUesInfo = new HashMap<>();

                for (Row row : sheet) {
                    if (row == null) continue;

                    for (int c = 0; c < row.getLastCellNum(); c++) {
                        Cell cell = row.getCell(c);
                        if (cell == null) continue;
                        String val = getStringValue(cell).trim().toUpperCase();

                        if (val.contains("CODE APOGEE DE L'UE") || val.contains("CODE APOGÉE DE L'UE")) {
                            Row labelRow = sheet.getRow(row.getRowNum() - first);
                            Row nameRow = sheet.getRow(row.getRowNum() + first);
                            Row levelRow = sheet.getRow(row.getRowNum() + second);

                            if (labelRow != null && nameRow != null && levelRow != null) {
                                for (int col = c + first; col < row.getLastCellNum(); col++) {
                                    String extractedUeLabel = getStringValue(labelRow.getCell(col)).trim().toUpperCase();
                                    if (extractedUeLabel.matches("UE\\s*\\d+\\.\\d+")) {
                                        UE parsedUe = new UE();
                                        parsedUe.setLabel(extractedUeLabel);
                                        parsedUe.setEuApogeeCode(getStringValue(row.getCell(col)).trim());
                                        parsedUe.setName(getStringValue(nameRow.getCell(col)).trim());

                                        String levelStr = getStringValue(levelRow.getCell(col)).trim();
                                        int level = first;
                                        Matcher mLevel = Pattern.compile("\\d+").matcher(levelStr);
                                        if (mLevel.find()) {
                                            level = Integer.parseInt(mLevel.group());
                                        }
                                        parsedUe.setCompetenceLevel(level);

                                        parsedUesInfo.put(extractedUeLabel.replace(" ", ""), parsedUe);
                                    }
                                }
                            }
                            break;
                        }
                    }

                    String firstCellVal = getStringValue(row.getCell(firstOccurence)).trim().toUpperCase();

                    if (firstCellVal.contains("SEMESTRE")) {
                        Matcher m = Pattern.compile("SEMESTRE\\s+(\\d+)").matcher(firstCellVal);
                        if (m.find()) {
                            currentSemester = Integer.parseInt(m.group(first));
                            colLabel = notExistingIndex; colApogee = notExistingIndex; colFI = notExistingIndex; colAlt = notExistingIndex;
                            colUeCoefs.clear();
                        }
                    }

                    if (colLabel == notExistingIndex) {
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

                    if (colLabel != notExistingIndex && colFI != notExistingIndex) {
                        String labelCell = getStringValue(row.getCell(colLabel)).trim();

                        if (labelCell.matches("^R\\d\\.[a-zA-Z0-9.]+.*")) {
                            ResourceDTO resourceDTO = new ResourceDTO();

                            Matcher m = Pattern.compile("^(R\\d\\.[a-zA-Z0-9.]+)\\s*[|\\-]?\\s*(.*)$").matcher(labelCell);
                            if (m.find()) {
                                resourceDTO.setLabel(m.group(first).trim());
                                resourceDTO.setName(m.group(second).trim());
                            } else {
                                resourceDTO.setLabel(labelCell);
                                resourceDTO.setName(labelCell);
                            }

                            String apogeeCode = colApogee != notExistingIndex ? getStringValue(row.getCell(colApogee), "") : "";
                            resourceDTO.setApogeeCode(apogeeCode);
                            int semester = currentSemester != null ? currentSemester : first;
                            resourceDTO.setSemester(semester);
                            resourceDTO.setInstitutionId(institutionId);
                            resourceDTO.setPathId(resolvedPathId);
                            resourceDTO.setTermsCode(" ");

                            Double initialCm = getNumericValue(row.getCell(colFI), defaultValue);
                            resourceDTO.setInitialCm(initialCm);
                            Double initialTd = getNumericValue(row.getCell(colFI + first), defaultValue);
                            resourceDTO.setInitialTd(initialTd);
                            Double initialTp = getNumericValue(row.getCell(colFI + second), defaultValue);
                            resourceDTO.setInitialTp(initialTp);

                            if (colAlt != notExistingIndex) {
                                Double alternanceCm = getNumericValue(row.getCell(colAlt), defaultValue);
                                resourceDTO.setAlternanceCm(alternanceCm);
                                Double alternanceTd = getNumericValue(row.getCell(colAlt + first), defaultValue);
                                resourceDTO.setAlternanceTd(alternanceTd);
                                Double alternanceTp = getNumericValue(row.getCell(colAlt + second), defaultValue);
                                resourceDTO.setAlternanceTp(alternanceTp);
                            } else {
                                resourceDTO.setAlternanceCm(defaultValue); resourceDTO.setAlternanceTd(defaultValue); resourceDTO.setAlternanceTp(defaultValue);
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

                                Double coef = getNumericValue(row.getCell(entry.getKey()), defaultValue);
                                if (coef > indexFirstRow && !addedUes.contains(ueLabel)) {
                                    ResourceDTO.UeCoefficientDTO ueDto = new ResourceDTO.UeCoefficientDTO();
                                    ueDto.setUeLabel(ueLabel);
                                    ueDto.setCoefficient(coef);

                                    String standardUeLabel = ueLabel.replace(" ", "");
                                    Integer finalCurrentSemester = currentSemester;
                                    UE ueEntity = ueRepository.findByLabelAndPath_IdPath(ueLabel, resolvedPathId)
                                            .orElseGet(() -> {
                                                UE newUe = new UE();
                                                newUe.setLabel(ueLabel);
                                                newUe.setSemester(finalCurrentSemester != null ? finalCurrentSemester : first);

                                                UE parsedInfo = parsedUesInfo.get(standardUeLabel);
                                                if (parsedInfo != null) {
                                                    newUe.setEuApogeeCode(parsedInfo.getEuApogeeCode());
                                                    newUe.setName(parsedInfo.getName());
                                                    newUe.setCompetenceLevel(parsedInfo.getCompetenceLevel());
                                                } else {
                                                    newUe.setEuApogeeCode("INCONNU");
                                                    newUe.setName(ueLabel);
                                                    newUe.setCompetenceLevel(first);
                                                }

                                                Path path = pathRepository.findById(resolvedPathId).orElse(null);

                                                debugCsvLogs(first, "UE", newUe.getName());
                                                if  (path != null) {
                                                    debugCsvLogs(first,"UE_PATH_ID", path.getIdPath().toString());
                                                } else {
                                                    debugCsvLogs(first, "UE+PATH_ID", "none");
                                                }
                                                newUe.setPath(path);

                                                return ueRepository.save(newUe);
                                            });

                                    ueDto.setUeId(ueEntity.getUeNumber());

                                    ueCoefficients.add(ueDto);
                                    addedUes.add(ueLabel);
                                }
                            }

                            resourceDTO.setUeCoefficients(ueCoefficients);
                            debugCsvLogs(first, "RESOURCE_LABEL", resourceDTO.getLabel());
                            debugCsvLogs(first,"RESOURCE_NAME",  resourceDTO.getName());
                            debugCsvLogs(first, "RESOURCE_PATH", resourceDTO.getPathId().toString() + "\n");
                            dtos.add(resourceDTO);
                        }
                    }
                }
            }
        }

        List<Resource> entitiesToSave = new ArrayList<>();
        int skippedCount = 0;
        for (ResourceDTO dto : dtos) {
            Optional<Resource> dtoResource = resourceRepository.findFirstByLabelAndPath_IdPath(dto.getLabel(), dto.getPathId());
            if (dtoResource.isPresent()) {
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
                    logContent.append("     -UeId généré en BDD : ").append(ueCoef.getUeId()).append("\n");
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
            resourceRepository.saveAll(entitiesToSave);
            writeInCsvLogs(userName + " (" + userId + ") has saved " +entitiesToSave.size() + "entities in the DB " + "\n" +
                    skippedCount + " entities skipped");
        } else {
            writeInCsvLogs(userName + " (" + userId + ") : No new resource to save from xslx file. Skipped " + skippedCount + " entities.");
        }
    }

    private Long extractAndResolvePath(Sheet sheet, Long institutionId) {
        String pathRawString = null;
        int nbCol = 10;

        for (int r = 0; r < nbCol; r++) {
            Row row = sheet.getRow(r);
            if (row != null && row.getCell(firstOccurence) != null) {
                String val = getStringValue(row.getCell(firstOccurence)).trim().replaceAll("^\"|\"$", "");
                if (val.toLowerCase().contains("parcours")) {
                    pathRawString = val;
                    break;
                }
            }
        }

        if (pathRawString == null) return null;

        int pathNumber;
        int nonExistantPathIndex = 99;
        String pathName;

        if (pathRawString.toLowerCase().contains("parcours commun")) {
            pathNumber = 0;
            pathName = "Parcours commun";
        } else {
            Matcher m = Pattern.compile("(?i)Parcours\\s+(\\d+)\\s*:\\s*(.*)").matcher(pathRawString);
            if (m.find()) {
                pathNumber = Integer.parseInt(m.group(first));
                pathName = m.group(second).trim();
            } else {
                pathNumber = nonExistantPathIndex;
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

                    return pathRepository.saveAndFlush(newPath);
                });


        return finalPath.getIdPath();
    }

    private Resource mapDtoToEntity(ResourceDTO dto) {
        Resource ressource = new Resource();
        ressource.setLabel(dto.getLabel());
        ressource.setName(dto.getName());
        ressource.setApogeeCode(dto.getApogeeCode());
        ressource.setSemester(dto.getSemester());
        ressource.setDiffMultiCompetences(false);

        if (dto.getPathId() != null) {
            Path path = pathRepository.findById(dto.getPathId()).orElse(null);
            ressource.setPath(path);
        }

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