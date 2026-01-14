package iut.unilim.fr.back;

import iut.unilim.fr.back.dto.*;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.mapper.ResourceSheetMapper;
import iut.unilim.fr.back.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceSheetMapperTest {

    @Mock
    private UeCoefficientRepository ueCoefficientRepository;
    @Mock
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;
    @Mock
    private TeachersForResourceRepository teachersForResourceRepository;
    @Mock
    private NationalProgramObjectiveRepository nationalProgramObjectiveRepository;
    @Mock
    private NationalProgramSkillRepository nationalProgramSkillRepository;
    @Mock
    private SAERepository saeRepository;
    @Mock
    private SAELinkResourceRepository saeLinkResourceRepository;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private ModalitiesOfImplementationRepository modalitiesRepository;
    @Mock
    private HoursPerStudentRepository hoursPerStudentRepository;
    @Mock
    private TeacherHoursRepository teacherHoursRepository;
    @Mock
    private PedagogicalContentRepository pedagogicalContentRepository;
    @Mock
    private RessourceTrackingRepository resourceTrackingRepository;

    // Not used in toDTO directly but autowired in class, good practice to mock to avoid context issues if context was loaded
    @Mock
    private RessourceRepository ressourceRepository;

    @InjectMocks
    private ResourceSheetMapper resourceSheetMapper;

    @Test
    void testToDTO_FullMapping() {
        Long sheetId = 100L;
        Long resId = 50L;
        Integer semester = 1;

        Ressource resource = new Ressource();
        resource.setIdResource(resId);
        resource.setName("Development");
        resource.setLabel("R1.01");
        resource.setApogeeCode("R101");
        resource.setSemester(semester);
        resource.setDiffMultiCompetences(true);

        RessourceSheet sheet = new RessourceSheet();
        sheet.setIdResourceSheet(sheetId);
        sheet.setYear(LocalDate.now());
        sheet.setResource(resource);

        Institution institution = new Institution();
        institution.setIdInstitution(99L);
        institution.setName("IUT Limoges");

        UserSyncadia mainUser = new UserSyncadia();
        mainUser.setFirstname("John");
        mainUser.setLastname("Doe");
        mainUser.setInstitution(institution);

        MainTeacherForResource mainTeacherRel = new MainTeacherForResource();
        mainTeacherRel.setUser(mainUser);

        when(mainTeacherForResourceRepository.findByIdResource(resId))
                .thenReturn(Collections.singletonList(mainTeacherRel));

        UserSyncadia teacherUser = new UserSyncadia();
        teacherUser.setFirstname("Jane");
        teacherUser.setLastname("Smith");

        TeachersForResource teacherRel = new TeachersForResource();
        teacherRel.setUser(teacherUser);

        when(teachersForResourceRepository.findByIdResource(resId))
                .thenReturn(Collections.singletonList(teacherRel));

        UE ue = new UE();
        ue.setUeNumber(1L);
        ue.setLabel("Base Informatique");
        ue.setName("Info");
        ue.setCompetenceLevel(1);

        UeCoefficient coef = new UeCoefficient();
        coef.setUe(ue);
        coef.setCoefficient(2);

        when(ueCoefficientRepository.findByResource_IdResource(resId))
                .thenReturn(Collections.singletonList(coef));

        NationalProgramObjective obj1 = new NationalProgramObjective();
        obj1.setContent("Learn Java");
        NationalProgramObjective obj2 = new NationalProgramObjective();
        obj2.setContent("Understand OOP");

        when(nationalProgramObjectiveRepository.findByResourceSheet_IdResourceSheet(sheetId))
                .thenReturn(Arrays.asList(obj1, obj2));

        NationalProgramSkill skill = new NationalProgramSkill();
        skill.setIdSkill(1L);
        skill.setLabel("Dev");
        skill.setDescription("Coding skills");

        when(nationalProgramSkillRepository.findByResourceSheet_IdResourceSheet(sheetId))
                .thenReturn(Collections.singletonList(skill));

        SAELinkResource link = new SAELinkResource();
        link.setIdSAE(1000L);
        when(saeLinkResourceRepository.findByIdResource(resId)).thenReturn(Collections.singletonList(link));

        SAE sae1 = new SAE(); sae1.setIdSAE(1000L); sae1.setLabel("SAE 1"); sae1.setApogeeCode("S1.01");
        SAE sae2 = new SAE(); sae2.setIdSAE(2000L); sae2.setLabel("SAE 2"); sae2.setApogeeCode("S1.02");

        when(saeRepository.findBySemester(semester)).thenReturn(Arrays.asList(sae1, sae2));

        Keyword kw = new Keyword();
        kw.setKeyword("Java");
        when(keywordRepository.findByIdResourceSheet(sheetId)).thenReturn(Collections.singletonList(kw));
        ModalitiesOfImplementation modality = new ModalitiesOfImplementation();
        modality.setModality("Project");
        when(modalitiesRepository.findByResourceSheet_IdResourceSheet(sheetId)).thenReturn(Collections.singletonList(modality));

        HoursPerStudent hpInit = new HoursPerStudent();
        hpInit.setHasAlternance(false); hpInit.setCm(10); hpInit.setTd(20); hpInit.setTp(0);

        HoursPerStudent hpAlt = new HoursPerStudent();
        hpAlt.setHasAlternance(true); hpAlt.setCm(5); hpAlt.setTd(5); hpAlt.setTp(5);

        when(hoursPerStudentRepository.findByResource_IdResource(resId))
                .thenReturn(Arrays.asList(hpInit, hpAlt));

        TeacherHours thInit = new TeacherHours();
        thInit.setIsAlternance(false); thInit.setCm(10); thInit.setTd(20); thInit.setTp(30);

        TeacherHours thAlt = new TeacherHours();
        thAlt.setIsAlternance(true); thAlt.setCm(5); thAlt.setTd(5); thAlt.setTp(5);

        when(teacherHoursRepository.findByResourceSheet_IdResourceSheet(sheetId))
                .thenReturn(Arrays.asList(thInit, thAlt));

        PedagogicalContent content = new PedagogicalContent();
        content.setCm("1 Intro, 2 Loops");
        content.setTd("1 Exercise");
        content.setTp(null);
        content.setDs("");

        when(pedagogicalContentRepository.findByResourceSheet_IdResourceSheet(sheetId))
                .thenReturn(Collections.singletonList(content));

        RessourceTracking tracking = new RessourceTracking();
        tracking.setStudentFeedback("Good");
        tracking.setPedagogicalFeedback("Okay");
        tracking.setImprovementSuggestions("More practice");

        when(resourceTrackingRepository.findByResourceSheet_IdResourceSheet(sheetId))
                .thenReturn(Collections.singletonList(tracking));

        ResourceSheetDTO result = resourceSheetMapper.toDTO(sheet);

        assertNotNull(result);
        assertEquals(sheetId, result.getId());
        assertEquals("R1.01", result.getResourceLabel());
        assertEquals("R101", result.getResourceApogeeCode());
        assertEquals("IU EN FOR 001", result.getQualityReference());
        assertEquals(true, result.getDiffMultiCompetences());

        assertEquals("IUT Limoges", result.getDepartment());
        assertEquals(99L, result.getInstitutionId());

        assertEquals("John Doe", result.getMainTeacher());
        assertEquals(1, result.getTeachers().size());
        assertEquals("Jane Smith", result.getTeachers().getFirst());

        assertEquals(1, result.getUeReferences().size());
        assertEquals(1L, result.getUeReferences().getFirst().getUeNumber());
        assertEquals(2, result.getUeReferences().getFirst().getCoefficient());


        assertTrue(result.getObjective().contains("Learn Java"));
        assertTrue(result.getObjective().contains("Understand OOP"));


        assertEquals(1, result.getSkills().size());
        assertEquals("Dev", result.getSkills().getFirst().getLabel());

        assertEquals(2, result.getLinkedSaes().size());
        SaeInfoDTO dtoSae1 = result.getLinkedSaes().stream().filter(s -> s.getId().equals(1000L)).findFirst().get();
        assertTrue(dtoSae1.getIsLinked());
        SaeInfoDTO dtoSae2 = result.getLinkedSaes().stream().filter(s -> s.getId().equals(2000L)).findFirst().get();
        assertFalse(dtoSae2.getIsLinked());

        assertEquals(List.of("Java"), result.getKeywords());
        assertEquals(List.of("Project"), result.getModalities());

        assertEquals(10, result.getHoursPN().getCm());
        assertFalse(result.getHoursPN().getHasAlternance());
        assertEquals(5, result.getHoursPNAlternance().getCm());
        assertTrue(result.getHoursPNAlternance().getHasAlternance());

        assertEquals(30, result.getHoursTeacher().getTp());
        assertEquals(5, result.getHoursTeacherAlternance().getTp());

        assertNotNull(result.getPedagogicalContent());
        assertEquals(2, result.getPedagogicalContent().getCm().size());
        assertEquals(1, result.getPedagogicalContent().getCm().get(0).getOrder());
        assertEquals("Intro", result.getPedagogicalContent().getCm().get(0).getContent());
        assertEquals(2, result.getPedagogicalContent().getCm().get(1).getOrder());
        assertEquals("Loops", result.getPedagogicalContent().getCm().get(1).getContent());

        assertEquals("Good", result.getTracking().getStudentFeedback());
    }

    @Test
    void testToDTO_NullResource() {
        RessourceSheet sheet = new RessourceSheet();
        sheet.setIdResourceSheet(1L);
        sheet.setResource(null);

        ResourceSheetDTO result = resourceSheetMapper.toDTO(sheet);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getResourceName());
    }
}
