package iut.unilim.fr.back.mapper;

import iut.unilim.fr.back.dto.admin.MCCCSaeDTO;
import iut.unilim.fr.back.dto.admin.MCCCSaeDTO.UECoefficientDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MCCCSaeMapper {

    @Autowired
    private SAEHoursRepository saeHoursRepository;

    @Autowired
    private SAELinkResourceRepository saeLinkResourceRepository;

    @Autowired
    private UeCoefficientSAERepository ueCoefficientSAERepository;

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;


    /**
     * Convert a SAE entity to MCCCSaeDTO
     */
    public MCCCSaeDTO toDTO(SAE sae) {
        MCCCSaeDTO dto = new MCCCSaeDTO();

        // Basic SAE information
        dto.setSaeId(sae.getIdSAE());
        dto.setLabel(sae.getLabel());
        dto.setApogeeCode(sae.getApogeeCode());

        // Semester (directly from SAE entity)
        dto.setSemester(sae.getSemester());

        // Terms code
        if (sae.getTerms() != null) {
            dto.setTermsCode(sae.getTerms().getCode());
        }

        // Hours from SAEHours table
        SAEHoursInfo hoursInfo = getHoursAndAlternance(sae.getIdSAE());
        dto.setHours(hoursInfo.hours);
        dto.setHasAlternance(hoursInfo.hasAlternance);

        // Institution ID from main teacher of linked resource
        dto.setInstitutionId(getInstitutionId(sae.getIdSAE()));

        // Linked resources
        dto.setLinkedResources(getLinkedResources(sae.getIdSAE()));

        // UE coefficients
        dto.setUeCoefficients(getUECoefficients(sae.getIdSAE()));

        return dto;
    }


    /**
     * Inner class to hold hours and alternance info
     */
    private static class SAEHoursInfo {
        Integer hours;
        Boolean hasAlternance;

        SAEHoursInfo(Integer hours, Boolean hasAlternance) {
            this.hours = hours;
            this.hasAlternance = hasAlternance;
        }
    }

    /**
     * Get hours and alternance from SAEHours table
     */
    private SAEHoursInfo getHoursAndAlternance(Long saeId) {
        List<SAEHours> hoursList = saeHoursRepository.findBySae_IdSAE(saeId);
        if (!hoursList.isEmpty()) {
            SAEHours saeHours = hoursList.get(0);
            return new SAEHoursInfo(
                saeHours.getHours() != null ? saeHours.getHours() : 0,
                saeHours.getHasAlternance() != null ? saeHours.getHasAlternance() : false
            );
        }
        return new SAEHoursInfo(0, false);
    }

    /**
     * Get institution ID from main teacher of a linked resource
     */
    private Long getInstitutionId(Long saeId) {
        // Get linked resources
        List<SAELinkResource> links = saeLinkResourceRepository.findByIdSAE(saeId);

        for (SAELinkResource link : links) {
            if (link.getResource() != null) {
                // Find main teacher for this resource
                List<MainTeacherForResource> mainTeachers =
                    mainTeacherForResourceRepository.findByIdResource(link.getResource().getIdResource());

                if (!mainTeachers.isEmpty()) {
                    MainTeacherForResource mainTeacher = mainTeachers.get(0);
                    if (mainTeacher.getUser() != null && mainTeacher.getUser().getInstitution() != null) {
                        return mainTeacher.getUser().getInstitution().getIdInstitution();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get linked resources labels
     */
    private List<String> getLinkedResources(Long saeId) {
        List<SAELinkResource> links = saeLinkResourceRepository.findByIdSAE(saeId);
        return links.stream()
            .filter(link -> link.getResource() != null)
            .map(link -> link.getResource().getLabel())
            .collect(Collectors.toList());
    }

    /**
     * Get UE coefficients
     */
    private List<UECoefficientDTO> getUECoefficients(Long saeId) {
        List<UeCoefficientSAE> coefficients = ueCoefficientSAERepository.findBySae_IdSAE(saeId);
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
     * Convert multiple SAEs to DTOs
     */
    public List<MCCCSaeDTO> toDTOList(List<SAE> saes) {
        return saes.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}

