package iut.unilim.fr.back;

import iut.unilim.fr.back.controller.ResourceSheetDTOController;
import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import iut.unilim.fr.back.service.ResourceGetterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceGetterServiceTest {

    @Mock
    private RessourceRepository ressourceRepository;
    @Mock
    private RessourceSheetRepository ressourceSheetRepository;
    @Mock
    private ResourceSheetDTOController rsDTOController;

    @InjectMocks
    private ResourceGetterService service;

    @BeforeEach
    void setUp() {
        // @InjectMock do it.
    }

    @Test
    void testSetValuesFromResource_Success() {
        String resourceName = "R1.01";
        Long resourceId = 1L;

        Ressource mockRessource = new Ressource();
        mockRessource.setIdResource(resourceId);
        mockRessource.setLabel("Init");
        mockRessource.setName("Dev");
        when(ressourceRepository.findFirstByLabelStartingWith(resourceName))
                .thenReturn(Optional.of(mockRessource));

        RessourceSheet mockSheet = new RessourceSheet();
        when(ressourceSheetRepository.findFirstByResource_IdResource(resourceId))
                .thenReturn(Optional.of(mockSheet));

        ResourceSheetDTO mockDto = mock(ResourceSheetDTO.class);
        when(mockDto.getResourceName()).thenReturn("Initiation Dev");
        when(mockDto.getDepartment()).thenReturn("Informatique");
        when(mockDto.getMainTeacher()).thenReturn("Professeur Test");
        when(mockDto.getObjective()).thenReturn("Apprendre Java");

        UeInfoDTO ueInfo = new UeInfoDTO();
        ueInfo.setLabel("UE1");
        when(mockDto.getUeReferences()).thenReturn(List.of(ueInfo));

        SkillDTO skill = new SkillDTO();
        skill.setDescription("Savoir coder");
        when(mockDto.getSkills()).thenReturn(List.of(skill));

        SaeInfoDTO sae = new SaeInfoDTO();
        sae.setLabel("SAE 1");
        sae.setIsLinked(true);
        when(mockDto.getLinkedSaes()).thenReturn(List.of(sae));

        when(mockDto.getKeywords()).thenReturn(List.of("Java", "Spring"));
        when(mockDto.getModalities()).thenReturn(List.of("Ecrit"));

        HoursDTO hours = new HoursDTO();
        hours.setCm(10.0); hours.setTd(10.0); hours.setTp(10.0); hours.setTotal(30.0);
        when(mockDto.getHoursPN()).thenReturn(hours);
        when(mockDto.getHoursTeacher()).thenReturn(hours);

        PedagogicalContentDTO pedago = new PedagogicalContentDTO();
        pedago.setCm(Collections.emptyList());
        pedago.setTd(Collections.emptyList());
        pedago.setTp(Collections.emptyList());
        pedago.setDs(Collections.emptyList());
        when(mockDto.getPedagogicalContent()).thenReturn(pedago);

        ResourceTrackingDTO tracking = new ResourceTrackingDTO();
        tracking.setStudentFeedback("Good");
        tracking.setPedagogicalFeedback("RAS");
        tracking.setImprovementSuggestions("None");
        when(mockDto.getTracking()).thenReturn(tracking);

        when(rsDTOController.getResourceSheetsByResourceId(resourceId))
                .thenReturn(List.of(mockDto));

        service.setValuesFromResource(resourceName);

        assertEquals(resourceName, service.getResourceName());
        assertEquals("Informatique", service.getDepartment());
        assertEquals("Professeur Test", service.getProfRef());
        assertEquals("Init: Dev", service.getLabelResource());
        assertTrue(service.getSkills().contains("Savoir coder"));
        assertTrue(service.getSaes().contains("SAE 1"));
        assertFalse(service.isAlternance());
    }

    @Test
    void testSetValuesFromResource_NotFound() {
        String resourceName = "Inconnu";
        when(ressourceRepository.findFirstByLabelStartingWith(resourceName))
                .thenReturn(Optional.empty());

        service.setValuesFromResource(resourceName);

        assertEquals("", service.getResourceName());
        assertEquals("Aucun", service.getRefUE());
    }
}