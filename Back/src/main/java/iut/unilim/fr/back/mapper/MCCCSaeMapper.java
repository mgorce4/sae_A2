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

        // Semester (from terms)
        if (sae.getTerms() != null) {
            // Assuming terms code contains semester info (e.g., "S1", "S2")
            String termsCode = sae.getTerms().getCode();
            dto.setSemester(extractSemesterFromTerms(termsCode));
        }

        // Hours
        setHours(dto, sae.getIdSAE());

        // Linked resources
        dto.setLinkedResources(getLinkedResources(sae.getIdSAE()));

        // UE coefficients
        dto.setUeCoefficients(getUECoefficients(sae.getIdSAE()));

        return dto;
    }

    /**
     * Extract semester number from terms code
     */
    private Integer extractSemesterFromTerms(String termsCode) {
        if (termsCode == null) return null;
        // Extract number from terms code (e.g., "S1" -> 1, "S2" -> 2)
        try {
            return Integer.parseInt(termsCode.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Set hours from SAEHours
     */
    private void setHours(MCCCSaeDTO dto, Long saeId) {
        List<SAEHours> hoursList = saeHoursRepository.findBySae_IdSAE(saeId);
        if (!hoursList.isEmpty()) {
            SAEHours hours = hoursList.get(0);
            dto.setHours(hours.getHours() != null ? hours.getHours() : 0);
        } else {
            dto.setHours(0);
        }
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

