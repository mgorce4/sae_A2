package iut.unilim.fr.back.dto.admin;

import java.util.List;

public class MCCCSaeDTO {

    private Long saeId;
    private String label;              // e.g., "SAE 1.01"
    private String apogeeCode;         // e.g., "SAE101"
    private Integer semester;
    private Long institutionId;        // Institution ID
    private String termsCode;          // Terms code (e.g., "NGCC")
    private Long pathId;               // Path ID
    private String pathName;           // Path name

    // Hours
    private Double hours;              // Hours for formation initiale (has_alternance = 0)
    private Double hoursAlternance;    // Hours for alternance (has_alternance = 1)
    private Boolean hasAlternance;     // Deprecated: use hoursAlternance instead

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

    public String getTermsCode() {
        return termsCode;
    }

    public void setTermsCode(String termsCode) {
        this.termsCode = termsCode;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHoursAlternance() {
        return hoursAlternance;
    }

    public void setHoursAlternance(Double hoursAlternance) {
        this.hoursAlternance = hoursAlternance;
    }

    public Boolean getHasAlternance() {
        return hasAlternance;
    }

    public void setHasAlternance(Boolean hasAlternance) {
        this.hasAlternance = hasAlternance;
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
        private Long ueId;             // UE ID for identification
        private String ueLabel;        // e.g., "UE2.1"
        private String ueName;         // e.g., "Understanding organizations"
        private Double coefficient;

        public UECoefficientDTO() {
        }

        public UECoefficientDTO(String ueLabel, String ueName, Double coefficient) {
            this.ueLabel = ueLabel;
            this.ueName = ueName;
            this.coefficient = coefficient;
        }

        public Long getUeId() {
            return ueId;
        }

        public void setUeId(Long ueId) {
            this.ueId = ueId;
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

        public Double getCoefficient() {
            return coefficient;
        }

        public void setCoefficient(Double coefficient) {
            this.coefficient = coefficient;
        }
    }
}

