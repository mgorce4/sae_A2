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

        // Terms code
        if (resource.getTerms() != null) {
            dto.setTermsCode(resource.getTerms().getCode());
        }

        // Institution ID from main teacher
        dto.setInstitutionId(getInstitutionIdFromPath(resource));
        // Path information
        if (resource.getPath() != null) {
            dto.setPathId(resource.getPath().getIdPath());
            dto.setPathName(resource.getPath().getName());
        };

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
            dto.setPnCm(hours.getCm() != null ? hours.getCm() : 0.0);
            dto.setPnTd(hours.getTd() != null ? hours.getTd() : 0.0);
            dto.setPnTp(hours.getTp() != null ? hours.getTp() : 0.0);
            dto.setPnTotal((dto.getPnCm() != null ? dto.getPnCm() : 0.0) +
                          (dto.getPnTd() != null ? dto.getPnTd() : 0.0) +
                          (dto.getPnTp() != null ? dto.getPnTp() : 0.0));
        } else {
            dto.setPnCm(0.0);
            dto.setPnTd(0.0);
            dto.setPnTp(0.0);
            dto.setPnTotal(0.0);
        }
    }

    /**
     * Set teacher hours (Initial and Alternance)
     * We need to find all resource sheets for this resource
     * Fallback: if no TeacherHours, use HoursPerStudent
     */
    private void setTeacherHours(MCCCResourceDTO dto, Long resourceId) {
        List<TeacherHours> hoursList = teacherHoursRepository.findAll().stream()
                .filter(th -> th.getResourceSheet() != null
                        && th.getResourceSheet().getResource() != null
                        && th.getResourceSheet().getResource().getIdResource().equals(resourceId))
                .toList();

        boolean foundInitial = false;
        boolean foundAlternance = false;

        for (TeacherHours th : hoursList) {
            if (Boolean.TRUE.equals(th.getIsAlternance())) {
                dto.setAlternanceCm(parseHoursString(th.getCm()));
                dto.setAlternanceTd(parseHoursString(th.getTd()));
                dto.setAlternanceTp(parseHoursString(th.getTp()));
                dto.setAlternanceProject(0);
                dto.setAlternanceTotal(
                        dto.getAlternanceCm() + dto.getAlternanceTd() + dto.getAlternanceTp()
                );
                foundAlternance = true;
            } else {
                dto.setInitialCm(parseHoursString(th.getCm()));
                dto.setInitialTd(parseHoursString(th.getTd()));
                dto.setInitialTp(parseHoursString(th.getTp()));
                dto.setInitialProject(0);
                dto.setInitialTotal(
                        dto.getInitialCm() + dto.getInitialTd() + dto.getInitialTp()
                );
                foundInitial = true;
            }
        }

        if (!foundInitial || !foundAlternance) {
            List<HoursPerStudent> hpsList = hoursPerStudentRepository.findByResource_IdResource(resourceId);
            for (HoursPerStudent hps : hpsList) {
                if (Boolean.TRUE.equals(hps.getHasAlternance()) && !foundAlternance) {
                    dto.setAlternanceCm(hps.getCm() != null ? hps.getCm() : 0.0);
                    dto.setAlternanceTd(hps.getTd() != null ? hps.getTd() : 0.0);
                    dto.setAlternanceTp(hps.getTp() != null ? hps.getTp() : 0.0);
                    dto.setAlternanceProject(0);
                    dto.setAlternanceTotal(
                            dto.getAlternanceCm() + dto.getAlternanceTd() + dto.getAlternanceTp()
                    );
                    foundAlternance = true;
                } else if (!Boolean.TRUE.equals(hps.getHasAlternance()) && !foundInitial) {
                    dto.setInitialCm(hps.getCm() != null ? hps.getCm() : 0.0);
                    dto.setInitialTd(hps.getTd() != null ? hps.getTd() : 0.0);
                    dto.setInitialTp(hps.getTp() != null ? hps.getTp() : 0.0);
                    dto.setInitialProject(0);
                    dto.setInitialTotal(
                            dto.getInitialCm() + dto.getInitialTd() + dto.getInitialTp()
                    );
                    foundInitial = true;
                }
            }
        }
    }



    /**
     * Get institution ID from main teacher
     */

    private Long getInstitutionIdFromPath(Ressource resource) {
    if (resource.getPath() != null && resource.getPath().getInstitution() != null) {
        return resource.getPath().getInstitution().getIdInstitution();
    }
    return null;
}

    /**
     * Get main teacher name
     */
    private String getMainTeacher(Long resourceId) {
        return mainTeacherForResourceRepository.findByIdResource(resourceId).stream()
                .findFirst()
                .map(MainTeacherForResource::getUser)
                .map(u -> u.getFirstname() + " " + u.getLastname())
                .orElse("Non assigné");
    }


    /**
     * Get all teachers for this resource
     */
    private List<MCCCResourceDTO.TeacherInfoDTO> getTeachers(Long resourceId) {
        return teachersForResourceRepository.findByIdResource(resourceId).stream()
                .filter(t -> t.getUser() != null)
                .map(t -> new MCCCResourceDTO.TeacherInfoDTO(
                        t.getUser().getIdUser(),
                        t.getUser().getFirstname() + " " + t.getUser().getLastname()
                ))
                .toList();
    }

    private List<Long> getTeacherIds(Long resourceId) {
    return teachersForResourceRepository.findByIdResource(resourceId).stream()
        .filter(t -> t.getUser() != null)
        .map(t -> t.getUser().getIdUser())
        .toList();
    }

    /**
     * Get UE coefficients
     */
    private List<MCCCResourceDTO.UECoefficientDTO> getUECoefficients(Long resourceId) {
        return ueCoefficientRepository.findByResource_IdResource(resourceId).stream()
                .filter(c -> c.getUe() != null)
                .map(c -> {
                    UE ue = c.getUe();
                    MCCCResourceDTO.UECoefficientDTO dto =
                            new MCCCResourceDTO.UECoefficientDTO(
                                    ue.getLabel(),
                                    ue.getName(),
                                    c.getCoefficient()
                            );
                    dto.setUeId(ue.getUeNumber()); // 🔥 FIX ICI
                    return dto;
                })
                .toList();
    }


    /**
     * Get linked SAEs labels
     */
    private List<String> getLinkedSaes(Long resourceId) {
        return saeLinkResourceRepository.findByIdResource(resourceId).stream()
                .filter(link -> link.getSae() != null)
                .map(link -> link.getSae().getLabel())
                .toList();
    }


    /**
     * Convert multiple resources to DTOs
     */
    public List<MCCCResourceDTO> toDTOList(List<Ressource> resources) {
        return resources.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Parse hours string from database to Double
     */
    private Double parseHoursString(String hoursStr) {
        if (hoursStr == null || hoursStr.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(hoursStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

