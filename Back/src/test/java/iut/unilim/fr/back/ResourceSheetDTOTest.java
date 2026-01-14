package iut.unilim.fr.back;

import iut.unilim.fr.back.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceSheetDTOTest {
    // ResourceSheetDto is a POJO, here we are only testing setters and getters
    @Test
    void testGettersAndSetters() {
        ResourceSheetDTO dto = new ResourceSheetDTO();

        Long id = 1L;
        LocalDate year = LocalDate.of(2023, 9, 1);

        Long resourceId = 101L;
        String resourceName = "Java Programming";
        String resourceLabel = "R2.01";
        String resourceApogeeCode = "R201";
        String qualityReference = "QUAL-2023";
        Integer semester = 3;
        Boolean diffMultiCompetences = true;

        String department = "Computer Science";
        Long institutionId = 500L;

        String mainTeacher = "Dr. Smith";
        List<String> teachers = Arrays.asList("Dr. Smith", "Prof. Doe");

        UeInfoDTO ueInfo = new UeInfoDTO();
        List<UeInfoDTO> ueReferences = List.of(ueInfo);

        SkillDTO skill = new SkillDTO();
        List<SkillDTO> skills = List.of(skill);

        SaeInfoDTO saeInfo = new SaeInfoDTO();
        List<SaeInfoDTO> linkedSaes = List.of(saeInfo);

        String objective = "To master OOP";
        List<String> keywords = Arrays.asList("Java", "OOP", "Spring");
        List<String> modalities = Arrays.asList("Project", "Exam");

        HoursDTO hoursPN = new HoursDTO();
        HoursDTO hoursPNAlternance = new HoursDTO();
        HoursDTO hoursTeacher = new HoursDTO();
        HoursDTO hoursTeacherAlternance = new HoursDTO();

        PedagogicalContentDTO pedagogicalContent = new PedagogicalContentDTO();
        ResourceTrackingDTO tracking = new ResourceTrackingDTO();

        dto.setId(id);
        dto.setYear(year);
        dto.setResourceId(resourceId);
        dto.setResourceName(resourceName);
        dto.setResourceLabel(resourceLabel);
        dto.setResourceApogeeCode(resourceApogeeCode);
        dto.setQualityReference(qualityReference);
        dto.setSemester(semester);
        dto.setDiffMultiCompetences(diffMultiCompetences);
        dto.setDepartment(department);
        dto.setInstitutionId(institutionId);
        dto.setMainTeacher(mainTeacher);
        dto.setTeachers(teachers);
        dto.setUeReferences(ueReferences);
        dto.setObjective(objective);
        dto.setSkills(skills);
        dto.setLinkedSaes(linkedSaes);
        dto.setKeywords(keywords);
        dto.setModalities(modalities);
        dto.setHoursPN(hoursPN);
        dto.setHoursPNAlternance(hoursPNAlternance);
        dto.setHoursTeacher(hoursTeacher);
        dto.setHoursTeacherAlternance(hoursTeacherAlternance);
        dto.setPedagogicalContent(pedagogicalContent);
        dto.setTracking(tracking);

        assertEquals(id, dto.getId());
        assertEquals(year, dto.getYear());

        assertEquals(resourceId, dto.getResourceId());
        assertEquals(resourceName, dto.getResourceName());
        assertEquals(resourceLabel, dto.getResourceLabel());
        assertEquals(resourceApogeeCode, dto.getResourceApogeeCode());
        assertEquals(qualityReference, dto.getQualityReference());
        assertEquals(semester, dto.getSemester());
        assertEquals(diffMultiCompetences, dto.getDiffMultiCompetences());

        assertEquals(department, dto.getDepartment());
        assertEquals(institutionId, dto.getInstitutionId());

        assertEquals(mainTeacher, dto.getMainTeacher());
        assertEquals(teachers, dto.getTeachers());
        assertEquals(2, dto.getTeachers().size());

        assertEquals(ueReferences, dto.getUeReferences());
        assertEquals(objective, dto.getObjective());
        assertEquals(skills, dto.getSkills());

        assertEquals(linkedSaes, dto.getLinkedSaes());
        assertEquals(keywords, dto.getKeywords());
        assertEquals(modalities, dto.getModalities());

        assertEquals(hoursPN, dto.getHoursPN());
        assertEquals(hoursPNAlternance, dto.getHoursPNAlternance());
        assertEquals(hoursTeacher, dto.getHoursTeacher());
        assertEquals(hoursTeacherAlternance, dto.getHoursTeacherAlternance());

        assertEquals(pedagogicalContent, dto.getPedagogicalContent());
        assertEquals(tracking, dto.getTracking());
    }
}
