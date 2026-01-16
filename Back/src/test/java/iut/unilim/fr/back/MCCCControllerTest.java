package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.MCCCController;
import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.dto.admin.MCCCSaeDTO;
import iut.unilim.fr.back.dto.admin.MCCCUEDTO;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.mapper.MCCCMapper;
import iut.unilim.fr.back.mapper.MCCCSaeMapper;
import iut.unilim.fr.back.mapper.MCCCUEMapper;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.SAERepository;
import iut.unilim.fr.back.repository.UERepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MCCCControllerTest {

    @Mock
    private RessourceRepository ressourceRepository;
    @Mock
    private SAERepository saeRepository;
    @Mock
    private UERepository ueRepository;

    @Mock
    private MCCCMapper mcccMapper;
    @Mock
    private MCCCSaeMapper mcccSaeMapper;
    @Mock
    private MCCCUEMapper mcccUEMapper;

    @InjectMocks
    private MCCCController controller;

    @Test
    void testGetAllMCCCResources_Success() {
        List<Ressource> resources = Collections.singletonList(new Ressource());
        List<MCCCResourceDTO> dtos = Collections.singletonList(new MCCCResourceDTO());

        when(ressourceRepository.findAll()).thenReturn(resources);
        when(mcccMapper.toDTOList(resources)).thenReturn(dtos);

        ResponseEntity<List<MCCCResourceDTO>> response = controller.getAllMCCCResources();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }

    @Test
    void testGetMCCCResourcesBySemester_Success() {
        Integer semester = 1;
        List<Ressource> resources = Collections.singletonList(new Ressource());
        List<MCCCResourceDTO> dtos = Collections.singletonList(new MCCCResourceDTO());

        when(ressourceRepository.findBySemester(semester)).thenReturn(resources);
        when(mcccMapper.toDTOList(resources)).thenReturn(dtos);

        ResponseEntity<List<MCCCResourceDTO>> response = controller.getMCCCResourcesBySemester(semester);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }

    @Test
    void testGetMCCCResourceById_Found() {
        Long id = 1L;
        Ressource resource = new Ressource();
        MCCCResourceDTO dto = new MCCCResourceDTO();

        when(ressourceRepository.findById(id)).thenReturn(Optional.of(resource));
        when(mcccMapper.toDTO(resource)).thenReturn(dto);

        ResponseEntity<MCCCResourceDTO> response = controller.getMCCCResourceById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetMCCCResourceById_NotFound() {
        when(ressourceRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<MCCCResourceDTO> response = controller.getMCCCResourceById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllMCCCResources_Exception() {
        when(ressourceRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<MCCCResourceDTO>> response = controller.getAllMCCCResources();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void testGetAllMCCCSaes_Success() {
        List<SAE> saes = Collections.singletonList(new SAE());
        List<MCCCSaeDTO> dtos = Collections.singletonList(new MCCCSaeDTO());

        when(saeRepository.findAll()).thenReturn(saes);
        when(mcccSaeMapper.toDTOList(saes)).thenReturn(dtos);

        ResponseEntity<List<MCCCSaeDTO>> response = controller.getAllMCCCSaes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }

    @Test
    void testGetMCCCSaeById_Found() {
        Long id = 1L;
        SAE sae = new SAE();
        MCCCSaeDTO dto = new MCCCSaeDTO();

        when(saeRepository.findById(id)).thenReturn(Optional.of(sae));
        when(mcccSaeMapper.toDTO(sae)).thenReturn(dto);

        ResponseEntity<MCCCSaeDTO> response = controller.getMCCCSaeById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetMCCCSaesByInstitution_Success() {
        Long targetInstitutionId = 100L;
        Long otherInstitutionId = 200L;

        List<SAE> saes = Arrays.asList(new SAE(), new SAE());
        when(saeRepository.findAll()).thenReturn(saes);

        MCCCSaeDTO dto1 = mock(MCCCSaeDTO.class);
        when(dto1.getInstitutionId()).thenReturn(targetInstitutionId);

        MCCCSaeDTO dto2 = mock(MCCCSaeDTO.class);
        when(dto2.getInstitutionId()).thenReturn(otherInstitutionId);

        MCCCSaeDTO dto3 = mock(MCCCSaeDTO.class);
        when(dto3.getInstitutionId()).thenReturn(null);

        List<MCCCSaeDTO> mappedDtos = Arrays.asList(dto1, dto2, dto3);
        when(mcccSaeMapper.toDTOList(saes)).thenReturn(mappedDtos);


        ResponseEntity<List<MCCCSaeDTO>> response = controller.getMCCCSaesByInstitution(targetInstitutionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(targetInstitutionId, response.getBody().getFirst().getInstitutionId());
    }


    @Test
    void testGetAllMCCCUEs_Success() {

        List<UE> ues = Collections.singletonList(new UE());
        List<MCCCUEDTO> dtos = Collections.singletonList(new MCCCUEDTO());

        when(ueRepository.findAll()).thenReturn(ues);
        when(mcccUEMapper.toDTOList(ues)).thenReturn(dtos);

        ResponseEntity<List<MCCCUEDTO>> response = controller.getAllMCCCUEs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }

    @Test
    void testGetMCCCUEById_Found() {
        Long id = 1L;
        UE ue = new UE();
        MCCCUEDTO dto = new MCCCUEDTO();

        when(ueRepository.findById(id)).thenReturn(Optional.of(ue));
        when(mcccUEMapper.toDTO(ue)).thenReturn(dto);


        ResponseEntity<MCCCUEDTO> response = controller.getMCCCUEById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetMCCCUEsByLevel_Success() {

        Integer level = 2;

        List<UE> ues = Collections.singletonList(new UE());
        List<MCCCUEDTO> dtos = Collections.singletonList(new MCCCUEDTO());

        when(ueRepository.findByCompetenceLevel(level)).thenReturn(ues);
        when(mcccUEMapper.toDTOList(ues)).thenReturn(dtos);

        ResponseEntity<List<MCCCUEDTO>> response = controller.getMCCCUEsByLevel(level);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
    }

    @Test
    void testGetMCCCUEsByInstitution_Success() {
        Long targetInstitutionId = 50L;

        List<UE> ues = Collections.singletonList(new UE());
        when(ueRepository.findAll()).thenReturn(ues);

        MCCCUEDTO dtoMatch = mock(MCCCUEDTO.class);
        when(dtoMatch.getInstitutionId()).thenReturn(targetInstitutionId);

        MCCCUEDTO dtoMismatch = mock(MCCCUEDTO.class);
        when(dtoMismatch.getInstitutionId()).thenReturn(99L);

        List<MCCCUEDTO> mappedDtos = Arrays.asList(dtoMatch, dtoMismatch);
        when(mcccUEMapper.toDTOList(ues)).thenReturn(mappedDtos);

        ResponseEntity<List<MCCCUEDTO>> response = controller.getMCCCUEsByInstitution(targetInstitutionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(targetInstitutionId, response.getBody().getFirst().getInstitutionId());
    }
}
