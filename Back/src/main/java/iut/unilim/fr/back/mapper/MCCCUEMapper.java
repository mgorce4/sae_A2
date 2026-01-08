package iut.unilim.fr.back.mapper;

import iut.unilim.fr.back.dto.admin.MCCCUEDTO;
import iut.unilim.fr.back.dto.admin.MCCCUEDTO.ResourceCoefficientDTO;
import iut.unilim.fr.back.dto.admin.MCCCUEDTO.SaeCoefficientDTO;
import iut.unilim.fr.back.entity.*;
import iut.unilim.fr.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MCCCUEMapper {

    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;

    @Autowired
    private UeCoefficientSAERepository ueCoefficientSAERepository;

    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    /**
     * Convert a UE entity to MCCCUEDTO
     */
    public MCCCUEDTO toDTO(UE ue) {
        MCCCUEDTO dto = new MCCCUEDTO();

        // Basic UE information
        dto.setUeNumber(ue.getUeNumber());
        dto.setLabel(ue.getLabel());
        dto.setName(ue.getName());
        dto.setEuApogeeCode(ue.getEuApogeeCode());
        dto.setCompetenceLevel(ue.getCompetenceLevel());
        dto.setSemester(ue.getSemester());

        // Institution ID from a resource linked to this UE
        dto.setInstitutionId(getInstitutionId(ue.getUeNumber()));

        // Path information
        if (ue.getPath() != null) {
            dto.setPathNumber(ue.getPath().getNumber());
            dto.setPathName(ue.getPath().getName());
        }

        // Resource coefficients
        dto.setResourceCoefficients(getResourceCoefficients(ue.getUeNumber()));

        // SAE coefficients
        dto.setSaeCoefficients(getSaeCoefficients(ue.getUeNumber()));

        return dto;
    }

    /**
     * Get institution ID from a resource linked to this UE
     */
    private Long getInstitutionId(Long ueNumber) {
        // Get resources linked to this UE
        List<UeCoefficient> coefficients = ueCoefficientRepository.findByUe_UeNumber(ueNumber);

        for (UeCoefficient coef : coefficients) {
            if (coef.getResource() != null) {
                // Find main teacher for this resource
                List<MainTeacherForResource> mainTeachers =
                    mainTeacherForResourceRepository.findByIdResource(coef.getResource().getIdResource());

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
     * Get resource coefficients for this UE
     */
    private List<ResourceCoefficientDTO> getResourceCoefficients(Long ueNumber) {
        List<UeCoefficient> coefficients = ueCoefficientRepository.findByUe_UeNumber(ueNumber);
        return coefficients.stream()
            .filter(c -> c.getResource() != null)
            .map(c -> {
                Ressource resource = c.getResource();
                return new ResourceCoefficientDTO(
                    resource.getLabel(),
                    resource.getName(),
                    c.getCoefficient()
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * Get SAE coefficients for this UE
     */
    private List<SaeCoefficientDTO> getSaeCoefficients(Long ueNumber) {
        List<UeCoefficientSAE> coefficients = ueCoefficientSAERepository.findByUe_UeNumber(ueNumber);
        return coefficients.stream()
            .filter(c -> c.getSae() != null)
            .map(c -> {
                SAE sae = c.getSae();
                return new SaeCoefficientDTO(
                    sae.getLabel(),
                    c.getCoefficient()
                );
            })
            .collect(Collectors.toList());
    }

    /**
     * Convert multiple UEs to DTOs
     */
    public List<MCCCUEDTO> toDTOList(List<UE> ues) {
        return ues.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}

