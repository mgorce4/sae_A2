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

        // Semester (derived from linked resources or terms)
        dto.setSemester(getSemester(sae));

        // Hours from SAEHours table
        dto.setHours(getHours(sae.getIdSAE()));

        // Institution ID from main teacher of linked resource
        dto.setInstitutionId(getInstitutionId(sae.getIdSAE()));

        // Linked resources
        dto.setLinkedResources(getLinkedResources(sae.getIdSAE()));

        // UE coefficients
        dto.setUeCoefficients(getUECoefficients(sae.getIdSAE()));

        return dto;
    }

    /**
     * Get semester from linked resources or terms
     */
    private Integer getSemester(SAE sae) {
        // First try to get semester from linked resources
        List<SAELinkResource> links = saeLinkResourceRepository.findByIdSAE(sae.getIdSAE());
        if (!links.isEmpty()) {
            SAELinkResource firstLink = links.get(0);
            if (firstLink.getResource() != null) {
                return firstLink.getResource().getSemester();
            }
        }

        // Fallback: extract from terms code if available
        if (sae.getTerms() != null && sae.getTerms().getCode() != null) {
            return extractSemesterFromTerms(sae.getTerms().getCode());
        }

        return null;
    }

    /**
     * Extract semester number from terms code
     */
    private Integer extractSemesterFromTerms(String termsCode) {
        if (termsCode == null) return null;
        // Extract number from terms code (e.g., "S1" -> 1, "S2" -> 2)
        try {
            String numericPart = termsCode.replaceAll("[^0-9]", "");
            if (!numericPart.isEmpty()) {
                return Integer.parseInt(numericPart);
            }
        } catch (NumberFormatException e) {
            // Ignore parsing errors
        }
        return null;
    }

    /**
     * Get hours from SAEHours table
     */
    private Integer getHours(Long saeId) {
        List<SAEHours> hoursList = saeHoursRepository.findBySae_IdSAE(saeId);
        if (!hoursList.isEmpty()) {
            SAEHours hours = hoursList.get(0);
            return hours.getHours() != null ? hours.getHours() : 0;
        }
        return 0;
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

