package iut.unilim.fr.back;

import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.repository.ResourceRepository;
import iut.unilim.fr.back.service.ExcelResourceImportService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcelResourceImportServiceTest {

    @Mock
    private ResourceRepository ressourceRepository;

    @InjectMocks
    private ExcelResourceImportService excelService;

    @Captor
    private ArgumentCaptor<List<Resource>> ressourceListCaptor;

    @Test
    void testImportResourcesFromExcel_Success() throws Exception {
        Long institutionId = 1L;
        Long pathId = 2L;

        byte[] excelContent = createMockExcelFile();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);

        excelService.importResourcesFromExcel(file, institutionId);

        verify(ressourceRepository, times(1)).saveAll(ressourceListCaptor.capture());

        List<Resource> savedResources = ressourceListCaptor.getValue();

        assertEquals(2, savedResources.size(), "Il devrait y avoir 2 ressources extraites");

        Resource r1 = savedResources.getFirst();
        assertEquals("R1.01", r1.getLabel());
        assertEquals("Initiation au développement", r1.getName());
        assertEquals("INFO111", r1.getApogeeCode());

        Resource r2 = savedResources.get(1);
        assertEquals("R1.02", r2.getLabel());
        assertEquals("Développement interfaces web", r2.getName());
        assertEquals("INFO112", r2.getApogeeCode());
    }

    @Test
    void testImportResourcesFromExcel_EmptyOrInvalidFile() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("Vide");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile("file", "vide.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());

        excelService.importResourcesFromExcel(file, 1L);

        verify(ressourceRepository, never()).saveAll(anyList());
    }

    private byte[] createMockExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("BUT1 INFO");

            Row row0 = sheet.createRow(0);
            row0.createCell(0).setCellValue("SEMESTRE 1");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Intitulé des ressources et des SAÉ");
            row1.createCell(1).setCellValue("Code Apogée");
            row1.createCell(2).setCellValue("Formation initiale");
            row1.createCell(5).setCellValue("Alternance");
            row1.createCell(8).setCellValue("Coefficients\nUE 1.1");
            row1.createCell(9).setCellValue("Coefficients\nUE 1.2");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("R1.01 | Initiation au développement");
            row2.createCell(1).setCellValue("INFO111");
            row2.createCell(2).setCellValue(15.0);
            row2.createCell(4).setCellValue(0.0);
            row2.createCell(3).setCellValue(15.0);
            row2.createCell(5).setCellValue(0.0);
            row2.createCell(8).setCellValue(2.5);

            Row row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("S1.01 | Implémentation d'un besoin");
            row3.createCell(1).setCellValue("INFO11S");

            Row row4 = sheet.createRow(4);
            row4.createCell(0).setCellValue("R1.02 - Développement interfaces web");
            row4.createCell(1).setCellValue("INFO112");
            row4.createCell(2).setCellValue(10.5); 
            row4.createCell(9).setCellValue(1.5);

            workbook.write(out);
            return out.toByteArray();
        }
    }
}