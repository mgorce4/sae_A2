package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.controllerBack.CsvTransfertController;
import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import iut.unilim.fr.back.service.TeacherImportCsvService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvTransfertControllerTest {

    @Mock
    private RessourceRepository ressourceRepository;
    @Mock
    private RessourceSheetRepository ressourceSheetRepository;
    @Mock
    private ResourceSheetDTOController rsDTOController;

    @Mock
    private UserSyncadiaRepository userSyncadiaRepository;
    @Mock
    private TeacherImportCsvService teacherImportCsvService;

    @InjectMocks
    private CsvTransfertController csvController;


    @Test
    void testGenerateCsv_Success() {
        String resourceName = "R1.01";

        Ressource mockRessource = new Ressource();
        mockRessource.setIdResource(1L);
        when(ressourceRepository.findFirstByLabelStartingWith(resourceName))
                .thenReturn(Optional.of(mockRessource));

        ResourceSheetDTO dto = mock(ResourceSheetDTO.class);

        when(dto.getDepartment()).thenReturn("Info");
        when(dto.getMainTeacher()).thenReturn("Prof");

        UeInfoDTO ue = new UeInfoDTO(); ue.setLabel("UE1");
        when(dto.getUeReferences()).thenReturn(List.of(ue));

        SaeInfoDTO sae = new SaeInfoDTO(); sae.setLabel("SAE1");
        when(dto.getLinkedSaes()).thenReturn(List.of(sae));

        PedagogicalContentDTO pedago = new PedagogicalContentDTO();
        pedago.setCm(Collections.emptyList());
        pedago.setTd(Collections.emptyList());
        pedago.setTp(Collections.emptyList());
        pedago.setDs(Collections.emptyList());
        when(dto.getPedagogicalContent()).thenReturn(pedago);

        ResourceTrackingDTO track = new ResourceTrackingDTO();
        track.setStudentFeedback("OK");
        when(dto.getTracking()).thenReturn(track);

        when(rsDTOController.getResourceSheetsByResourceId(1L)).thenReturn(List.of(dto));

        ResponseEntity<ByteArrayResource> response = csvController.generateCsv(resourceName, "", "atest");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        String csvContent = new String(response.getBody().getByteArray(), StandardCharsets.UTF_8);

        assertTrue(csvContent.startsWith("\uFEFF"));

        assertTrue(csvContent.contains("R1.01"));

        assertTrue(csvContent.contains("Info"));
        assertTrue(csvContent.contains("Prof"));
    }

    @Test
    void testGenerateCsv_NotFound() {
        when(ressourceRepository.findFirstByLabelStartingWith("Inconnu"))
                .thenReturn(Optional.empty());

        ResponseEntity<ByteArrayResource> response = csvController.generateCsv("Inconnu", "", "User");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testImportTeachers_Success() throws Exception {
        MultipartFile file = new org.springframework.mock.web.MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        String currentUser = "admin";
        UserSyncadia user = new iut.unilim.fr.back.entity.UserSyncadia();
        Institution inst = new iut.unilim.fr.back.entity.Institution();
        inst.setIdInstitution(1L);
        user.setInstitution(inst);

        when(userSyncadiaRepository.findByUsername(currentUser)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = csvController.importTeachers(file, currentUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertTrue(response.getBody().toString().contains("Import successful"));
        verify(teacherImportCsvService).importTeachers(file, 1L, currentUser);
    }

    @Test
    void testImportTeachers_UserNotFound() throws Exception {
        MultipartFile file = new org.springframework.mock.web.MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        String currentUser = "unknown";

        when(userSyncadiaRepository.findByUsername(currentUser)).thenReturn(Optional.empty());

        ResponseEntity<?> response = csvController.importTeachers(file, currentUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        assertTrue(response.getBody().toString().contains("User not found"));
    }

    @Test
    void testCleanCsv() {
        assertEquals("normal; ", CsvTransfertController.cleanCsv("normal"));
        assertEquals("with a coma, there is an other data; ", CsvTransfertController.cleanCsv("with a coma; there is an other data"));
        assertEquals("; ", CsvTransfertController.cleanCsv(null));
    }

    @Test
    void testCreatePedagoContent_Empty() {
        StringBuilder result = CsvTransfertController.createPedagoContent(Collections.emptyList());
        assertEquals("Aucun contenue pour cette catégorie", result.toString());
    }

    @Test
    void testCreatePedagoContent_WithData() {
        PedagogicalContentDTO.ContentItemDTO item = new PedagogicalContentDTO.ContentItemDTO();
        item.setOrder(1);
        item.setContent("Test Content");

        StringBuilder result = CsvTransfertController.createPedagoContent(List.of(item));
        assertEquals("1. Test Content, ", result.toString());
    }
}
