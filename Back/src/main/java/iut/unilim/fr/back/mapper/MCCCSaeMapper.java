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

        // Path information
        if (sae.getPath() != null) {
            dto.setPathId(sae.getPath().getIdPath());
            dto.setPathName(sae.getPath().getName());

            // Get institution ID from path
            if (sae.getPath().getInstitution() != null) {
                dto.setInstitutionId(sae.getPath().getInstitution().getIdInstitution());
            }
        }

        // Hours from SAEHours table
        SAEHoursInfo hoursInfo = getHoursAndAlternance(sae.getIdSAE());
        dto.setHours(hoursInfo.hours);
        dto.setHoursAlternance(hoursInfo.hoursAlternance);
        dto.setHasAlternance(hoursInfo.hasAlternance);


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
        Double hours;              // Formation initiale (has_alternance = 0)
        Double hoursAlternance;    // Alternance (has_alternance = 1)
        Boolean hasAlternance;     // True if at least one alternance entry exists

        SAEHoursInfo(Double hours, Double hoursAlternance, Boolean hasAlternance) {
            this.hours = hours;
            this.hoursAlternance = hoursAlternance;
            this.hasAlternance = hasAlternance;
        }
    }

    /**
     * Get hours and alternance from SAEHours table
     */
    private SAEHoursInfo getHoursAndAlternance(Long saeId) {
        List<SAEHours> hoursList = saeHoursRepository.findBySae_IdSAE(saeId);

        Double hours = null;
        Double hoursAlternance = null;
        Boolean hasAlternance = false;

        for (SAEHours saeHours : hoursList) {
            if (saeHours.getHasAlternance() != null && saeHours.getHasAlternance()) {
                hoursAlternance = saeHours.getHours();
                hasAlternance = true;
            } else {
                hours = saeHours.getHours();
            }
        }

        return new SAEHoursInfo(hours, hoursAlternance, hasAlternance);
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
                UECoefficientDTO dto = new UECoefficientDTO(
                    ue.getLabel(),
                    ue.getName(),
                    c.getCoefficient()
                );
                dto.setUeId(ue.getUeNumber());  // Set the UE ID
                return dto;
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

