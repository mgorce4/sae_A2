package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.controllerBack.CsvTransfertController;
import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvTransfertControllerTest {

    @Mock
    private RessourceRepository ressourceRepository;
    @Mock
    private RessourceSheetRepository ressourceSheetRepository;
    @Mock
    private ResourceSheetDTOController rsDTOController;

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

        ResponseEntity<ByteArrayResource> response = csvController.generateCsv(resourceName, "", "UserTest");

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
}
