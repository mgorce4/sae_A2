package iut.unilim.fr.back;

import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.mapper.MCCCMapper;
import iut.unilim.fr.back.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MCCCMapperTest {

    @Mock
    private HoursPerStudentRepository hoursPerStudentRepository;

    @Mock
    private TeacherHoursRepository teacherHoursRepository;

    @Mock
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    @Mock
    private TeachersForResourceRepository teachersForResourceRepository;

    @Mock
    private UeCoefficientRepository ueCoefficientRepository;

    @Mock
    private SAELinkResourceRepository saeLinkResourceRepository;

    @InjectMocks
    private MCCCMapper mcccMapper;

    @Test
    void testToDTO_FullMapping() {
        Long resId = 100L;
        Long institutionId = 55L;

        Ressource resource = new Ressource();
        resource.setIdResource(resId);
        resource.setLabel("R1.01");
        resource.setName("Init Dev");
        resource.setApogeeCode("R101");
        resource.setSemester(1);

        Institution institution = new Institution();
        institution.setIdInstitution(institutionId);

        UserSyncadia mainUser = new UserSyncadia();
        mainUser.setIdUser(10L);
        mainUser.setFirstname("Alan");
        mainUser.setLastname("Turing");
        mainUser.setInstitution(institution);

        MainTeacherForResource mainTeacherRel = new MainTeacherForResource();
        mainTeacherRel.setUser(mainUser);

        when(mainTeacherForResourceRepository.findByIdResource(resId))
                .thenReturn(Collections.singletonList(mainTeacherRel));

        UserSyncadia teacherUser = new UserSyncadia();
        teacherUser.setIdUser(20L);
        teacherUser.setFirstname("Grace");
        teacherUser.setLastname("Hopper");

        TeachersForResource teacherRel = new TeachersForResource();
        teacherRel.setUser(teacherUser);

        when(teachersForResourceRepository.findByIdResource(resId))
                .thenReturn(Collections.singletonList(teacherRel));

        HoursPerStudent pnHours = new HoursPerStudent();
        pnHours.setCm(10.0);
        pnHours.setTd(20.0);
        pnHours.setTp(30.0);

        when(hoursPerStudentRepository.findByResource_IdResource(resId))
                .thenReturn(Collections.singletonList(pnHours));

        RessourceSheet sheet = new RessourceSheet();
        sheet.setResource(resource);

        TeacherHours initialHours = new TeacherHours();
        initialHours.setResourceSheet(sheet);
        initialHours.setIsAlternance(false);
        initialHours.setCm("5");
        initialHours.setTd("5");
        initialHours.setTp("5");

        TeacherHours alternanceHours = new TeacherHours();
        alternanceHours.setResourceSheet(sheet);
        alternanceHours.setIsAlternance(true);
        alternanceHours.setCm("2");
        alternanceHours.setTd("2");
        alternanceHours.setTp("2");

        when(teacherHoursRepository.findAll())
                .thenReturn(Arrays.asList(initialHours, alternanceHours));

        UE ue = new UE();
        ue.setLabel("UE1.1");
        ue.setName("Base");

        UeCoefficient coef = new UeCoefficient();
        coef.setUe(ue);
        coef.setCoefficient(3.0);

        when(ueCoefficientRepository.findByResource_IdResource(resId))
                .thenReturn(Collections.singletonList(coef));

        SAE sae = new SAE();
        sae.setLabel("S1.01");

        SAELinkResource link = new SAELinkResource();
        link.setSae(sae);

        when(saeLinkResourceRepository.findByIdResource(resId))
                .thenReturn(Collections.singletonList(link));

        MCCCResourceDTO result = mcccMapper.toDTO(resource);

        assertNotNull(result);
        assertEquals(resId, result.getResourceId());
        assertEquals("R1.01", result.getLabel());
        assertEquals("Init Dev", result.getName());
        assertEquals("R101", result.getApogeeCode());
        assertEquals(1, result.getSemester());
        assertEquals(institutionId, result.getInstitutionId());

        assertEquals(10, result.getPnCm());
        assertEquals(20, result.getPnTd());
        assertEquals(30, result.getPnTp());
        assertEquals(60, result.getPnTotal());

        assertEquals(5, result.getInitialCm());
        assertEquals(5, result.getInitialTd());
        assertEquals(5, result.getInitialTp());
        assertEquals(0, result.getInitialProject());
        assertEquals(15, result.getInitialTotal());

        assertEquals(2, result.getAlternanceCm());
        assertEquals(2, result.getAlternanceTd());
        assertEquals(2, result.getAlternanceTp());
        assertEquals(6, result.getAlternanceTotal());

        assertEquals("Alan Turing", result.getMainTeacherName());

        assertEquals(1, result.getTeachers().size());
        assertEquals("Grace Hopper", result.getTeachers().getFirst().getTeacherName());
        assertEquals(20L, result.getTeachers().getFirst().getTeacherId());

        assertEquals(1, result.getUeCoefficients().size());
        assertEquals("UE1.1", result.getUeCoefficients().getFirst().getUeLabel());
        assertEquals(3, result.getUeCoefficients().getFirst().getCoefficient());

        assertEquals(1, result.getLinkedSaes().size());
        assertEquals("S1.01", result.getLinkedSaes().getFirst());
    }

    @Test
    void testToDTO_EmptyValues() {
        Long resId = 200L;
        Ressource resource = new Ressource();
        resource.setIdResource(resId);

        when(hoursPerStudentRepository.findByResource_IdResource(resId)).thenReturn(Collections.emptyList());
        when(teacherHoursRepository.findAll()).thenReturn(Collections.emptyList());
        when(mainTeacherForResourceRepository.findByIdResource(resId)).thenReturn(Collections.emptyList());
        when(teachersForResourceRepository.findByIdResource(resId)).thenReturn(Collections.emptyList());
        when(ueCoefficientRepository.findByResource_IdResource(resId)).thenReturn(Collections.emptyList());
        when(saeLinkResourceRepository.findByIdResource(resId)).thenReturn(Collections.emptyList());

        MCCCResourceDTO result = mcccMapper.toDTO(resource);

        assertNotNull(result);
        assertEquals(0, result.getPnTotal());
        assertEquals(0, result.getInitialTotal());
        assertEquals("Non assigné", result.getMainTeacherName());
        assertTrue(result.getTeachers().isEmpty());
    }

    @Test
    void testToDTOList() {
        Ressource r1 = new Ressource(); r1.setIdResource(1L);
        Ressource r2 = new Ressource(); r2.setIdResource(2L);
        List<Ressource> resources = Arrays.asList(r1, r2);


        when(hoursPerStudentRepository.findByResource_IdResource(anyLong())).thenReturn(Collections.emptyList());

        List<MCCCResourceDTO> dtos = mcccMapper.toDTOList(resources);

        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getResourceId());
        assertEquals(2L, dtos.get(1).getResourceId());
    }
}
