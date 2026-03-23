package iut.unilim.fr.back.service;

import iut.unilim.fr.back.dto.ResourceDTO;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.repository.ResourceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

@Service
public class ExcelResourceImportService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Transactional
    public void importResourcesFromExcel(MultipartFile file, Long institutionId, Long pathId) throws Exception {
        List<ResourceDTO> dtos = new ArrayList<>();

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

                            dto.setApogeeCode(colApogee != -1 ? getStringValue(row.getCell(colApogee), "") : "");
                            dto.setSemester(currentSemester != null ? currentSemester : 1);
                            dto.setInstitutionId(institutionId);
                            dto.setPathId(pathId);
                            dto.setTermsCode("");

                            dto.setInitialCm(getNumericValue(row.getCell(colFI), 0.0));
                            dto.setInitialTd(getNumericValue(row.getCell(colFI + 1), 0.0));
                            dto.setInitialTp(getNumericValue(row.getCell(colFI + 2), 0.0));

                            // TODO : Verif que absence val = -1
                            if (colAlt != -1) {
                                dto.setAlternanceCm(getNumericValue(row.getCell(colAlt), 0.0));
                                dto.setAlternanceTd(getNumericValue(row.getCell(colAlt + 1), 0.0));
                                dto.setAlternanceTp(getNumericValue(row.getCell(colAlt + 2), 0.0));
                            } else {
                                dto.setAlternanceCm(0.0); dto.setAlternanceTd(0.0); dto.setAlternanceTp(0.0);
                            }

                            dto.setMainTeachers(new ArrayList<>());
                            dto.setTeachers(new ArrayList<>());
                            dto.setLinkedSaesIds(new ArrayList<>());
                            dto.setLinkedSaes(new ArrayList<>());

                            List<ResourceDTO.UeCoefficientDTO> ueCoefficients = new ArrayList<>();
                            for (Map.Entry<Integer, String> entry : colUeCoefs.entrySet()) {
                                Double coef = getNumericValue(row.getCell(entry.getKey()), 0.0);
                                if (coef > 0) {
                                    ResourceDTO.UeCoefficientDTO ueDto = new ResourceDTO.UeCoefficientDTO();
                                    ueDto.setUeLabel(entry.getValue());
                                    ueDto.setCoefficient(coef);
                                    ueCoefficients.add(ueDto);
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
        for (ResourceDTO dto : dtos) {
            entitiesToSave.add(mapDtoToEntity(dto));
        }

        if (!entitiesToSave.isEmpty()) {
            resourceRepository.saveAll(entitiesToSave);
        }
    }

    private Resource mapDtoToEntity(ResourceDTO dto) {
        Resource ressource = new Resource();
        ressource.setLabel(dto.getLabel());
        ressource.setName(dto.getName());
        ressource.setApogeeCode(dto.getApogeeCode());
        ressource.setSemester(dto.getSemester());
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