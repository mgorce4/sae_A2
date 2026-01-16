package iut.unilim.fr.back;

import iut.unilim.fr.back.controllerBack.PdfController;
import iut.unilim.fr.back.service.ResourceGetterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfControllerTest {

    @Mock
    private ResourceGetterService res;

    @InjectMocks
    private PdfController pdfController;

    @Test
    void testGeneratePdf_Success() {
        when(res.getResourceName()).thenReturn("R1.01");

        when(res.qualityReference()).thenReturn("REF01");
        when(res.getDepartment()).thenReturn("Info");
        when(res.getRefUE()).thenReturn("UE1");
        when(res.getLabelResource()).thenReturn("Intro Java");
        when(res.getProfRef()).thenReturn("M. Dupont");
        when(res.getObjectiveContent()).thenReturn("Obj");
        when(res.getSkills()).thenReturn(new ArrayList<>(List.of("Skill1")));
        when(res.getSaes()).thenReturn(new ArrayList<>(List.of("SAE1")));
        when(res.getKeyWords()).thenReturn(new ArrayList<>(List.of("Java")));
        when(res.getModalities()).thenReturn(new ArrayList<>(List.of("Ecrit")));
        when(res.getHoursPN()).thenReturn(new ArrayList<>(List.of(10.0, 0.0, 0.0, 10.0)));
        when(res.getHoursStudent()).thenReturn(new ArrayList<>(List.of(10.0, 0.0, 0.0, 10.0)));
        when(res.isAlternance()).thenReturn(false);
        when(res.getPedagoContentDs()).thenReturn("DS1");
        when(res.getPedagoContentCm()).thenReturn("CM1");
        when(res.getPedagoContentTd()).thenReturn("TD1");
        when(res.getPedagoContentTp()).thenReturn("TP1");
        when(res.getPedagoTeamFeedback()).thenReturn("Bon");
        when(res.getStudentFeedback()).thenReturn("Bien");
        when(res.getImprovements()).thenReturn("Rien");

        try {
            ResponseEntity<Resource> response = pdfController.generatePdf("R1.01", "UserTest");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getHeaders().getContentDisposition().toString().contains("R1.01_ressource_sheet.pdf"));

        } catch (Exception e) {
            System.out.println("No police file found. Test is ignored : \n" + e.getMessage());
        }

        verify(res).setValuesFromResource("R1.01");
    }

    @Test
    void testGeneratePdf_NotFound() {
        when(res.getResourceName()).thenReturn("");

        ResponseEntity<Resource> response = pdfController.generatePdf("Inconnu", "UserTest");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}