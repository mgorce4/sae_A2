package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.dto.ResourceSheetDTO;
import iut.unilim.fr.back.dto.ResourceSheetUpdateDTO;
import iut.unilim.fr.back.entity.ResourceSheet;
import iut.unilim.fr.back.mapper.ResourceSheetMapper;
import iut.unilim.fr.back.repository.ResourceSheetRepository;
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
    private ResourceSheetRepository resourceSheetRepository;

    @Mock
    private ResourceSheetMapper resourceSheetMapper;

    @Mock
    private ResourceSheetUpdateService resourceSheetUpdateService;

    @InjectMocks
    private ResourceSheetDTOController controller;

    @Test
    void testGetAllResourceSheets() {
        ResourceSheet sheet1 = new ResourceSheet();
        ResourceSheet sheet2 = new ResourceSheet();
        when(resourceSheetRepository.findAll()).thenReturn(Arrays.asList(sheet1, sheet2));

        ResourceSheetDTO dto1 = new ResourceSheetDTO();
        ResourceSheetDTO dto2 = new ResourceSheetDTO();
        when(resourceSheetMapper.toDTO(sheet1)).thenReturn(dto1);
        when(resourceSheetMapper.toDTO(sheet2)).thenReturn(dto2);

        List<ResourceSheetDTO> result = controller.getAllResourceSheets();

        assertEquals(2, result.size());
        verify(resourceSheetRepository).findAll();
        verify(resourceSheetMapper, times(2)).toDTO(any(ResourceSheet.class));
    }

    @Test
    void testGetResourceSheetById_Found() {
        Long id = 1L;
        ResourceSheet sheet = new ResourceSheet();
        ResourceSheetDTO dto = new ResourceSheetDTO();

        when(resourceSheetRepository.findById(id)).thenReturn(Optional.of(sheet));
        when(resourceSheetMapper.toDTO(sheet)).thenReturn(dto);

        ResponseEntity<ResourceSheetDTO> response = controller.getResourceSheetById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetResourceSheetById_NotFound() {
        Long id = 1L;
        when(resourceSheetRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<ResourceSheetDTO> response = controller.getResourceSheetById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(resourceSheetMapper, never()).toDTO(any());
    }

    @Test
    void testGetResourceSheetsByResourceId() {
        Long resourceId = 5L;
        ResourceSheet sheet = new ResourceSheet();
        when(resourceSheetRepository.findByResource_IdResource(resourceId)).thenReturn(Collections.singletonList(sheet));
        when(resourceSheetMapper.toDTO(sheet)).thenReturn(new ResourceSheetDTO());

        List<ResourceSheetDTO> result = controller.getResourceSheetsByResourceId(resourceId);

        assertEquals(1, result.size());
        verify(resourceSheetRepository).findByResource_IdResource(resourceId);
    }

    @Test
    void testUpdateResourceSheet_Success() {

        Long id = 1L;
        ResourceSheetUpdateDTO updateDTO = new ResourceSheetUpdateDTO();
        ResourceSheet existingSheet = new ResourceSheet();
        ResourceSheetDTO updatedResultDTO = new ResourceSheetDTO();

        when(resourceSheetRepository.findById(id)).thenReturn(Optional.of(existingSheet));
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
        when(resourceSheetRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.updateResourceSheet(id, updateDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(resourceSheetUpdateService, never()).updateResourceSheet(anyLong(), any());
    }

    @Test
    void testUpdateResourceSheet_Exception() {
        Long id = 1L;
        ResourceSheetUpdateDTO updateDTO = new ResourceSheetUpdateDTO();
        ResourceSheet existingSheet = new ResourceSheet();

        when(resourceSheetRepository.findById(id)).thenReturn(Optional.of(existingSheet));

        doThrow(new RuntimeException("Erreur critique")).when(resourceSheetUpdateService).updateResourceSheet(id, updateDTO);


        ResponseEntity<?> response = controller.updateResourceSheet(id, updateDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Error updating resource sheet"));
    }
}
