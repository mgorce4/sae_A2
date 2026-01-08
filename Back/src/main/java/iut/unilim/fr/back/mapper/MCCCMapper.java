package iut.unilim.fr.back.mapper;

import iut.unilim.fr.back.dto.admin.MCCCResourceDTO;
import iut.unilim.fr.back.dto.admin.MCCCResourceDTO.TeacherInfoDTO;
import iut.unilim.fr.back.dto.admin.MCCCResourceDTO.UECoefficientDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MCCCMapper {

    @Autowired
    private HoursPerStudentRepository hoursPerStudentRepository;

    @Autowired
    private TeacherHoursRepository teacherHoursRepository;

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    @Autowired
    private TeachersForResourceRepository teachersForResourceRepository;

    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;

    @Autowired
    private SAELinkResourceRepository saeLinkResourceRepository;

    /**
     * Convert a Resource entity to MCCCResourceDTO
     */
    public MCCCResourceDTO toDTO(Ressource resource) {
        MCCCResourceDTO dto = new MCCCResourceDTO();

        // Basic resource information
        dto.setResourceId(resource.getIdResource());
        dto.setLabel(resource.getLabel());
        dto.setName(resource.getName());
        dto.setApogeeCode(resource.getApogeeCode());
        dto.setSemester(resource.getSemester());

        // Institution ID from main teacher
        dto.setInstitutionId(getInstitutionId(resource.getIdResource()));

        // PN hours (National Program)
        setPNHours(dto, resource.getIdResource());

        // Teacher hours (Initial and Alternance)
        setTeacherHours(dto, resource.getIdResource());

        // Main teacher
        dto.setMainTeacherName(getMainTeacher(resource.getIdResource()));

        // All teachers
        dto.setTeachers(getTeachers(resource.getIdResource()));

        // UE coefficients
        dto.setUeCoefficients(getUECoefficients(resource.getIdResource()));

        // Linked SAEs
        dto.setLinkedSaes(getLinkedSaes(resource.getIdResource()));

        return dto;
    }

    /**
     * Set PN hours from HoursPerStudent
     */
    private void setPNHours(MCCCResourceDTO dto, Long resourceId) {
        List<HoursPerStudent> hoursList = hoursPerStudentRepository.findByResource_IdResource(resourceId);
        if (!hoursList.isEmpty()) {
            HoursPerStudent hours = hoursList.get(0);
            dto.setPnCm(hours.getCm() != null ? hours.getCm() : 0);
            dto.setPnTd(hours.getTd() != null ? hours.getTd() : 0);
            dto.setPnTp(hours.getTp() != null ? hours.getTp() : 0);
            dto.setPnTotal((dto.getPnCm() != null ? dto.getPnCm() : 0) +
                          (dto.getPnTd() != null ? dto.getPnTd() : 0) +
                          (dto.getPnTp() != null ? dto.getPnTp() : 0));
        } else {
            dto.setPnCm(0);
            dto.setPnTd(0);
            dto.setPnTp(0);
            dto.setPnTotal(0);
        }
    }

    /**
     * Set teacher hours (Initial and Alternance)
     * We need to find all resource sheets for this resource
     */
    private void setTeacherHours(MCCCResourceDTO dto, Long resourceId) {
        // Get teacher hours - we'll aggregate from all resource sheets
        List<TeacherHours> allTeacherHours = teacherHoursRepository.findAll().stream()
            .filter(th -> th.getResourceSheet() != null &&
                         th.getResourceSheet().getResource() != null &&
                         th.getResourceSheet().getResource().getIdResource().equals(resourceId))
            .collect(Collectors.toList());

        // Initial training hours (not alternance)
        List<TeacherHours> initialHours = allTeacherHours.stream()
            .filter(th -> !th.getIsAlternance())
            .collect(Collectors.toList());

        if (!initialHours.isEmpty()) {
            TeacherHours hours = initialHours.get(0);
            dto.setInitialCm(hours.getCm() != null ? hours.getCm() : 0);
            dto.setInitialTd(hours.getTd() != null ? hours.getTd() : 0);
            dto.setInitialTp(hours.getTp() != null ? hours.getTp() : 0);
            // Project hours would come from another field if it exists
            dto.setInitialProject(0);
            dto.setInitialTotal((dto.getInitialCm() != null ? dto.getInitialCm() : 0) +
                               (dto.getInitialTd() != null ? dto.getInitialTd() : 0) +
                               (dto.getInitialTp() != null ? dto.getInitialTp() : 0) +
                               (dto.getInitialProject() != null ? dto.getInitialProject() : 0));
        } else {
            dto.setInitialCm(0);
            dto.setInitialTd(0);
            dto.setInitialTp(0);
            dto.setInitialProject(0);
            dto.setInitialTotal(0);
        }

        // Alternance hours
        List<TeacherHours> alternanceHours = allTeacherHours.stream()
            .filter(TeacherHours::getIsAlternance)
            .collect(Collectors.toList());

        if (!alternanceHours.isEmpty()) {
            TeacherHours hours = alternanceHours.get(0);
            dto.setAlternanceCm(hours.getCm() != null ? hours.getCm() : 0);
            dto.setAlternanceTd(hours.getTd() != null ? hours.getTd() : 0);
            dto.setAlternanceTp(hours.getTp() != null ? hours.getTp() : 0);
            dto.setAlternanceProject(0);
            dto.setAlternanceTotal((dto.getAlternanceCm() != null ? dto.getAlternanceCm() : 0) +
                                  (dto.getAlternanceTd() != null ? dto.getAlternanceTd() : 0) +
                                  (dto.getAlternanceTp() != null ? dto.getAlternanceTp() : 0) +
                                  (dto.getAlternanceProject() != null ? dto.getAlternanceProject() : 0));
        } else {
            dto.setAlternanceCm(0);
            dto.setAlternanceTd(0);
            dto.setAlternanceTp(0);
            dto.setAlternanceProject(0);
            dto.setAlternanceTotal(0);
        }
    }

    /**
     * Get institution ID from main teacher
     */
    private Long getInstitutionId(Long resourceId) {
        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        if (!mainTeachers.isEmpty() && mainTeachers.get(0).getUser() != null) {
            UserSyncadia user = mainTeachers.get(0).getUser();
            if (user.getInstitution() != null) {
                return user.getInstitution().getIdInstitution();
            }
        }
        return null;
    }

    /**
     * Get main teacher name
     */
    private String getMainTeacher(Long resourceId) {
        List<MainTeacherForResource> mainTeachers = mainTeacherForResourceRepository.findByIdResource(resourceId);
        if (!mainTeachers.isEmpty() && mainTeachers.get(0).getUser() != null) {
            UserSyncadia user = mainTeachers.get(0).getUser();
            return user.getFirstname() + " " + user.getLastname();
        }
        return "Non assigné";
    }

    /**
     * Get all teachers for this resource
     */
    private List<TeacherInfoDTO> getTeachers(Long resourceId) {
        List<TeachersForResource> teachers = teachersForResourceRepository.findByIdResource(resourceId);
        return teachers.stream()
            .filter(t -> t.getUser() != null)
            .map(t -> {
                UserSyncadia user = t.getUser();
                return new TeacherInfoDTO(
                    user.getIdUser(),
                    user.getFirstname() + " " + user.getLastname()
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * Get UE coefficients
     */
    private List<UECoefficientDTO> getUECoefficients(Long resourceId) {
        List<UeCoefficient> coefficients = ueCoefficientRepository.findByResource_IdResource(resourceId);
        return coefficients.stream()
            .filter(c -> c.getUe() != null)
            .map(c -> {
                UE ue = c.getUe();
                return new UECoefficientDTO(
                    ue.getLabel(),
                    ue.getName(),
                    c.getCoefficient()
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * Get linked SAEs labels
     */
    private List<String> getLinkedSaes(Long resourceId) {
        List<SAELinkResource> links = saeLinkResourceRepository.findByIdResource(resourceId);
        return links.stream()
            .filter(link -> link.getSae() != null)
            .map(link -> link.getSae().getLabel())
            .collect(Collectors.toList());
    }

    /**
     * Convert multiple resources to DTOs
     */
    public List<MCCCResourceDTO> toDTOList(List<Ressource> resources) {
        return resources.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}

