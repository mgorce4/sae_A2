package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.dto.ResourceSheetDTO;
import iut.unilim.fr.back.dto.ResourceSheetUpdateDTO;
import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.mapper.ResourceSheetMapper;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import iut.unilim.fr.back.service.ResourceSheetUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceSheetDTOControllerTest {

    @Mock
    private RessourceSheetRepository ressourceSheetRepository;

    @Mock
    private ResourceSheetMapper resourceSheetMapper;

    @Mock
    private ResourceSheetUpdateService resourceSheetUpdateService;

    @InjectMocks
    private ResourceSheetDTOController controller;

    @Test
    void testGetAllResourceSheets() {
        RessourceSheet sheet1 = new RessourceSheet();
        RessourceSheet sheet2 = new RessourceSheet();
        when(ressourceSheetRepository.findAll()).thenReturn(Arrays.asList(sheet1, sheet2));

        ResourceSheetDTO dto1 = new ResourceSheetDTO();
        ResourceSheetDTO dto2 = new ResourceSheetDTO();
        when(resourceSheetMapper.toDTO(sheet1)).thenReturn(dto1);
        when(resourceSheetMapper.toDTO(sheet2)).thenReturn(dto2);

        List<ResourceSheetDTO> result = controller.getAllResourceSheets();

        assertEquals(2, result.size());
        verify(ressourceSheetRepository).findAll();
        verify(resourceSheetMapper, times(2)).toDTO(any(RessourceSheet.class));
    }

    @Test
    void testGetResourceSheetById_Found() {
        Long id = 1L;
        RessourceSheet sheet = new RessourceSheet();
        ResourceSheetDTO dto = new ResourceSheetDTO();

        when(ressourceSheetRepository.findById(id)).thenReturn(Optional.of(sheet));
        when(resourceSheetMapper.toDTO(sheet)).thenReturn(dto);

        ResponseEntity<ResourceSheetDTO> response = controller.getResourceSheetById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetResourceSheetById_NotFound() {
        Long id = 1L;
        when(ressourceSheetRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<ResourceSheetDTO> response = controller.getResourceSheetById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(resourceSheetMapper, never()).toDTO(any());
    }

    @Test
    void testGetResourceSheetsByResourceId() {
        Long resourceId = 5L;
        RessourceSheet sheet = new RessourceSheet();
        when(ressourceSheetRepository.findByResource_IdResource(resourceId)).thenReturn(Collections.singletonList(sheet));
        when(resourceSheetMapper.toDTO(sheet)).thenReturn(new ResourceSheetDTO());

        List<ResourceSheetDTO> result = controller.getResourceSheetsByResourceId(resourceId);

        assertEquals(1, result.size());
        verify(ressourceSheetRepository).findByResource_IdResource(resourceId);
    }

    @Test
    void testUpdateResourceSheet_Success() {

        Long id = 1L;
        ResourceSheetUpdateDTO updateDTO = new ResourceSheetUpdateDTO();
        RessourceSheet existingSheet = new RessourceSheet();
        ResourceSheetDTO updatedResultDTO = new ResourceSheetDTO();

        when(ressourceSheetRepository.findById(id)).thenReturn(Optional.of(existingSheet));
        when(resourceSheetMapper.toDTO(existingSheet)).thenReturn(updatedResultDTO);

        ResponseEntity<?> response = controller.updateResourceSheet(id, updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResultDTO, response.getBody());

        verify(resourceSheetUpdateService).updateResourceSheet(eq(id), eq(updateDTO));
    }

    @Test
    void testUpdateResourceSheet_NotFound() {
        Long id = 1L;
        ResourceSheetUpdateDTO updateDTO = new ResourceSheetUpdateDTO();
        when(ressourceSheetRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.updateResourceSheet(id, updateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(resourceSheetUpdateService, never()).updateResourceSheet(anyLong(), any());
    }

    @Test
    void testUpdateResourceSheet_Exception() {
        Long id = 1L;
        ResourceSheetUpdateDTO updateDTO = new ResourceSheetUpdateDTO();
        RessourceSheet existingSheet = new RessourceSheet();

        when(ressourceSheetRepository.findById(id)).thenReturn(Optional.of(existingSheet));

        doThrow(new RuntimeException("Erreur critique")).when(resourceSheetUpdateService).updateResourceSheet(id, updateDTO);


        ResponseEntity<?> response = controller.updateResourceSheet(id, updateDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Error updating resource sheet"));
    }
}
