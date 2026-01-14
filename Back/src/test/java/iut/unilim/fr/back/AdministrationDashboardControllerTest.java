package iut.unilim.fr.back;


import iut.unilim.fr.back.controller.AdministrationDashboardController;
import iut.unilim.fr.back.dto.admin.AdministrationDashboardDTO;
import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.mapper.AdministrationDashboardMapper;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministrationDashboardControllerTest {

    @Mock
    private RessourceSheetRepository ressourceSheetRepository;

    @Mock
    private AdministrationDashboardMapper dashboardMapper;

    @InjectMocks
    private AdministrationDashboardController controller;

    @Test
    void testGetAdministrationDashboard_Success() {
        RessourceSheet sheet1 = new RessourceSheet();
        RessourceSheet sheet2 = new RessourceSheet();
        List<RessourceSheet> sheets = Arrays.asList(sheet1, sheet2);

        AdministrationDashboardDTO dto1 = new AdministrationDashboardDTO();
        AdministrationDashboardDTO dto2 = new AdministrationDashboardDTO();
        List<AdministrationDashboardDTO> dtos = Arrays.asList(dto1, dto2);

        when(ressourceSheetRepository.findAll()).thenReturn(sheets);
        when(dashboardMapper.toDTOList(sheets)).thenReturn(dtos);

        ResponseEntity<List<AdministrationDashboardDTO>> response = controller.getAdministrationDashboard();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(ressourceSheetRepository).findAll();
        verify(dashboardMapper).toDTOList(sheets);
    }

    @Test
    void testGetAdministrationDashboard_Exception() {
        when(ressourceSheetRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<AdministrationDashboardDTO>> response = controller.getAdministrationDashboard();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void testGetDashboardByInstitution_Success() {
        Long targetInstitutionId = 100L;
        Long otherInstitutionId = 200L;

        List<RessourceSheet> sheets = Collections.singletonList(new RessourceSheet());
        when(ressourceSheetRepository.findAll()).thenReturn(sheets);

        AdministrationDashboardDTO dtoTarget = mock(AdministrationDashboardDTO.class);
        when(dtoTarget.getInstitutionId()).thenReturn(targetInstitutionId);

        AdministrationDashboardDTO dtoOther = mock(AdministrationDashboardDTO.class);
        when(dtoOther.getInstitutionId()).thenReturn(otherInstitutionId);

        AdministrationDashboardDTO dtoNull = mock(AdministrationDashboardDTO.class);
        when(dtoNull.getInstitutionId()).thenReturn(null);

        List<AdministrationDashboardDTO> allDtos = Arrays.asList(dtoTarget, dtoOther, dtoNull);
        when(dashboardMapper.toDTOList(sheets)).thenReturn(allDtos);

        ResponseEntity<List<AdministrationDashboardDTO>> response = controller.getDashboardByInstitution(targetInstitutionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(targetInstitutionId, response.getBody().getFirst().getInstitutionId());
    }

    @Test
    void testGetDashboardByInstitution_Exception() {
        when(ressourceSheetRepository.findAll()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<AdministrationDashboardDTO>> response = controller.getDashboardByInstitution(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void testGetDashboardBySemester_Success() {
        Integer targetSemester = 3;
        Integer otherSemester = 4;

        List<RessourceSheet> sheets = Collections.singletonList(new RessourceSheet());
        when(ressourceSheetRepository.findAll()).thenReturn(sheets);

        AdministrationDashboardDTO dtoTarget = mock(AdministrationDashboardDTO.class);
        when(dtoTarget.getSemester()).thenReturn(targetSemester);

        AdministrationDashboardDTO dtoOther = mock(AdministrationDashboardDTO.class);
        when(dtoOther.getSemester()).thenReturn(otherSemester);

        AdministrationDashboardDTO dtoNull = mock(AdministrationDashboardDTO.class);
        when(dtoNull.getSemester()).thenReturn(null);

        List<AdministrationDashboardDTO> allDtos = Arrays.asList(dtoTarget, dtoOther, dtoNull);
        when(dashboardMapper.toDTOList(sheets)).thenReturn(allDtos);

        ResponseEntity<List<AdministrationDashboardDTO>> response = controller.getDashboardBySemester(targetSemester);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(targetSemester, response.getBody().getFirst().getSemester());
    }

    @Test
    void testGetDashboardBySemester_Exception() {
        when(ressourceSheetRepository.findAll()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<AdministrationDashboardDTO>> response = controller.getDashboardBySemester(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}