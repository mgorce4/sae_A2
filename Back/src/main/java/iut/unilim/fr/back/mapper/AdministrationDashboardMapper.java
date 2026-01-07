package iut.unilim.fr.back.mapper;

import iut.unilim.fr.back.dto.admin.AdministrationDashboardDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdministrationDashboardMapper {

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    /**
     * Convert a ResourceSheet entity to AdministrationDashboardDTO
     */
    public AdministrationDashboardDTO toDTO(RessourceSheet resourceSheet) {
        AdministrationDashboardDTO dto = new AdministrationDashboardDTO();

        // Resource sheet information
        dto.setResourceSheetId(resourceSheet.getIdResourceSheet());
        dto.setYear(resourceSheet.getYear());

        // Resource information
        if (resourceSheet.getResource() != null) {
            Ressource resource = resourceSheet.getResource();
            dto.setResourceId(resource.getIdResource());
            dto.setResourceLabel(resource.getLabel());
            dto.setResourceName(resource.getName());
            dto.setSemester(resource.getSemester());

            // Main teacher
            setMainTeacher(dto, resource.getIdResource());
        }

        // Sheet status - for now, we'll assume all sheets are submitted
        // This can be extended with a status field in the database
        dto.setIsSubmitted(true);
        dto.setSubmittedDate(resourceSheet.getYear());

        return dto;
    }

    /**
     * Set main teacher information
     */
    private void setMainTeacher(AdministrationDashboardDTO dto, Long resourceId) {
        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        if (!mainTeachers.isEmpty() && mainTeachers.get(0).getUser() != null) {
            UserSyncadia user = mainTeachers.get(0).getUser();
            dto.setMainTeacherId(user.getIdUser());
            dto.setMainTeacherName(user.getFirstname() + " " + user.getLastname());

            // Institution
            if (user.getInstitution() != null) {
                dto.setInstitutionId(user.getInstitution().getIdInstitution());
                dto.setInstitutionName(user.getInstitution().getName());
            }
        } else {
            dto.setMainTeacherId(null);
            dto.setMainTeacherName("Non assigné");
            dto.setInstitutionId(null);
            dto.setInstitutionName("Non assigné");
        }
    }

    /**
     * Convert multiple resource sheets to DTOs
     */
    public List<AdministrationDashboardDTO> toDTOList(List<RessourceSheet> resourceSheets) {
        return resourceSheets.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}

