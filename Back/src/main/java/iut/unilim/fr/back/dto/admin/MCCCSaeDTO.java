package iut.unilim.fr.back.dto.admin;

import java.util.List;

public class MCCCSaeDTO {

    private Long saeId;
    private String label;              // e.g., "SAE 1.01"
    private String apogeeCode;         // e.g., "SAE101"
    private Integer semester;
    private Long institutionId;        // Institution ID

    // Hours
    private Integer hours;

    // Linked resources
    private List<String> linkedResources;   // Labels of linked resources

    // UEs and coefficients
    private List<UECoefficientDTO> ueCoefficients;

    public MCCCSaeDTO() {
    }

    // Getters and Setters
    public Long getSaeId() {
        return saeId;
    }

    public void setSaeId(Long saeId) {
        this.saeId = saeId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getApogeeCode() {
        return apogeeCode;
    }

    public void setApogeeCode(String apogeeCode) {
        this.apogeeCode = apogeeCode;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public List<String> getLinkedResources() {
        return linkedResources;
    }

    public void setLinkedResources(List<String> linkedResources) {
        this.linkedResources = linkedResources;
    }

    public List<UECoefficientDTO> getUeCoefficients() {
        return ueCoefficients;
    }

    public void setUeCoefficients(List<UECoefficientDTO> ueCoefficients) {
        this.ueCoefficients = ueCoefficients;
    }

    // Inner class for UE and coefficient
    public static class UECoefficientDTO {
        private String ueLabel;        // e.g., "UE2.1"
        private String ueName;         // e.g., "Understanding organizations"
        private Integer coefficient;

        public UECoefficientDTO() {
        }

        public UECoefficientDTO(String ueLabel, String ueName, Integer coefficient) {
            this.ueLabel = ueLabel;
            this.ueName = ueName;
            this.coefficient = coefficient;
        }

        public String getUeLabel() {
            return ueLabel;
        }

        public void setUeLabel(String ueLabel) {
            this.ueLabel = ueLabel;
        }

        public String getUeName() {
            return ueName;
        }

        public void setUeName(String ueName) {
            this.ueName = ueName;
        }

        public Integer getCoefficient() {
            return coefficient;
        }

        public void setCoefficient(Integer coefficient) {
            this.coefficient = coefficient;
        }
    }
}

