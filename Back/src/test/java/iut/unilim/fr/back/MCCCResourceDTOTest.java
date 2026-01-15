package iut.unilim.fr.back;


import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MCCCResourceDTOTest {

    @Test
    void testMCCCResourceDTO_GettersAndSetters() {
        MCCCResourceDTO dto = new MCCCResourceDTO();

        Long resourceId = 1L;
        String label = "R1.01";
        String name = "Initiation to Dev";
        String apogeeCode = "R101";
        Integer semester = 1;
        Long institutionId = 100L;

        Integer pnCm = 10, pnTd = 20, pnTp = 30, pnTotal = 60;

        Integer initCm = 12, initTd = 22, initTp = 32, initProj = 5, initTotal = 71;

        Integer altCm = 5, altTd = 15, altTp = 25, altProj = 10, altTotal = 55;

        String mainTeacher = "Prof. Turing";
        MCCCResourceDTO.TeacherInfoDTO teacher1 = new MCCCResourceDTO.TeacherInfoDTO(1L, "Turing");
        List<MCCCResourceDTO.TeacherInfoDTO> teachers = List.of(teacher1);

        MCCCResourceDTO.UECoefficientDTO ueCoef = new MCCCResourceDTO.UECoefficientDTO("UE1", "Base", 3);
        List<MCCCResourceDTO.UECoefficientDTO> ueCoefficients = List.of(ueCoef);

        List<String> linkedSaes = Arrays.asList("S1.01", "S1.02");

        dto.setResourceId(resourceId);
        dto.setLabel(label);
        dto.setName(name);
        dto.setApogeeCode(apogeeCode);
        dto.setSemester(semester);
        dto.setInstitutionId(institutionId);

        dto.setPnCm(pnCm);
        dto.setPnTd(pnTd);
        dto.setPnTp(pnTp);
        dto.setPnTotal(pnTotal);

        dto.setInitialCm(initCm);
        dto.setInitialTd(initTd);
        dto.setInitialTp(initTp);
        dto.setInitialProject(initProj);
        dto.setInitialTotal(initTotal);

        dto.setAlternanceCm(altCm);
        dto.setAlternanceTd(altTd);
        dto.setAlternanceTp(altTp);
        dto.setAlternanceProject(altProj);
        dto.setAlternanceTotal(altTotal);

        dto.setMainTeacherName(mainTeacher);
        dto.setTeachers(teachers);
        dto.setUeCoefficients(ueCoefficients);
        dto.setLinkedSaes(linkedSaes);

        assertEquals(resourceId, dto.getResourceId());
        assertEquals(label, dto.getLabel());
        assertEquals(name, dto.getName());
        assertEquals(apogeeCode, dto.getApogeeCode());
        assertEquals(semester, dto.getSemester());
        assertEquals(institutionId, dto.getInstitutionId());

        assertEquals(pnCm, dto.getPnCm());
        assertEquals(pnTd, dto.getPnTd());
        assertEquals(pnTp, dto.getPnTp());
        assertEquals(pnTotal, dto.getPnTotal());

        assertEquals(initCm, dto.getInitialCm());
        assertEquals(initTd, dto.getInitialTd());
        assertEquals(initTp, dto.getInitialTp());
        assertEquals(initProj, dto.getInitialProject());
        assertEquals(initTotal, dto.getInitialTotal());

        assertEquals(altCm, dto.getAlternanceCm());
        assertEquals(altTd, dto.getAlternanceTd());
        assertEquals(altTp, dto.getAlternanceTp());
        assertEquals(altProj, dto.getAlternanceProject());
        assertEquals(altTotal, dto.getAlternanceTotal());

        assertEquals(mainTeacher, dto.getMainTeacherName());
        assertEquals(teachers, dto.getTeachers());
        assertEquals(ueCoefficients, dto.getUeCoefficients());
        assertEquals(linkedSaes, dto.getLinkedSaes());
    }

    @Test
    void testTeacherInfoDTO_InnerClass() {
        Long id = 50L;
        String name = "Teacher Name";

        MCCCResourceDTO.TeacherInfoDTO teacher = new MCCCResourceDTO.TeacherInfoDTO(id, name);

        assertEquals(id, teacher.getTeacherId());
        assertEquals(name, teacher.getTeacherName());

        teacher.setTeacherId(60L);
        teacher.setTeacherName("New Name");

        assertEquals(60L, teacher.getTeacherId());
        assertEquals("New Name", teacher.getTeacherName());
    }

    @Test
    void testUECoefficientDTO_InnerClass() {
        String label = "UE1.1";
        String name = "Dev Base";
        Integer coef = 4;

        MCCCResourceDTO.UECoefficientDTO ue = new MCCCResourceDTO.UECoefficientDTO(label, name, coef);


        assertEquals(label, ue.getUeLabel());
        assertEquals(name, ue.getUeName());
        assertEquals(coef, ue.getCoefficient());

        ue.setUeLabel("UE2.2");
        ue.setUeName("Advanced");
        ue.setCoefficient(5);

        assertEquals("UE2.2", ue.getUeLabel());
        assertEquals("Advanced", ue.getUeName());
        assertEquals(5, ue.getCoefficient());
    }
}