package iut.unilim.fr.back.dto.admin;

import java.util.List;

public class MCCCUEDTO {

    private Long ueNumber;
    private String label;              // e.g., "UE2.1"
    private String name;               // e.g., "Understanding organizations"
    private String euApogeeCode;       // e.g., "UE21"
    private Integer competenceLevel;   // Competence level
    private Integer semester;          // Semester
    private Long institutionId;        // Institution ID
    private Long userId;               // User ID (for creation)
    private String termsCode;          // Terms code (e.g., "NGCC")

    // Path information
    private Integer pathNumber;
    private String pathName;

    // Resources with coefficients
    private List<ResourceCoefficientDTO> resourceCoefficients;

    // SAEs with coefficients
    private List<SaeCoefficientDTO> saeCoefficients;

    public MCCCUEDTO() {
    }

    // Getters and Setters
    public Long getUeNumber() {
        return ueNumber;
    }

    public void setUeNumber(Long ueNumber) {
        this.ueNumber = ueNumber;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEuApogeeCode() {
        return euApogeeCode;
    }

    public void setEuApogeeCode(String euApogeeCode) {
        this.euApogeeCode = euApogeeCode;
    }

    public Integer getCompetenceLevel() {
        return competenceLevel;
    }

    public void setCompetenceLevel(Integer competenceLevel) {
        this.competenceLevel = competenceLevel;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPathNumber() {
        return pathNumber;
    }

    public void setPathNumber(Integer pathNumber) {
        this.pathNumber = pathNumber;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getTermsCode() {
        return termsCode;
    }

    public void setTermsCode(String termsCode) {
        this.termsCode = termsCode;
    }

    public List<ResourceCoefficientDTO> getResourceCoefficients() {
        return resourceCoefficients;
    }

    public void setResourceCoefficients(List<ResourceCoefficientDTO> resourceCoefficients) {
        this.resourceCoefficients = resourceCoefficients;
    }

    public List<SaeCoefficientDTO> getSaeCoefficients() {
        return saeCoefficients;
    }

    public void setSaeCoefficients(List<SaeCoefficientDTO> saeCoefficients) {
        this.saeCoefficients = saeCoefficients;
    }

    // Inner class for Resource and coefficient
    public static class ResourceCoefficientDTO {
        private String resourceLabel;  // e.g., "R1.01"
        private String resourceName;   // e.g., "Python"
        private Double coefficient;

        public ResourceCoefficientDTO() {
        }

        public ResourceCoefficientDTO(String resourceLabel, String resourceName, Double coefficient) {
            this.resourceLabel = resourceLabel;
            this.resourceName = resourceName;
            this.coefficient = coefficient;
        }

        public String getResourceLabel() {
            return resourceLabel;
        }

        public void setResourceLabel(String resourceLabel) {
            this.resourceLabel = resourceLabel;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public Double getCoefficient() {
            return coefficient;
        }

        public void setCoefficient(Double coefficient) {
            this.coefficient = coefficient;
        }
    }

    // Inner class for SAE and coefficient
    public static class SaeCoefficientDTO {
        private String saeLabel;       // e.g., "SAE 1.01"
        private Double coefficient;

        public SaeCoefficientDTO() {
        }

        public SaeCoefficientDTO(String saeLabel, Double coefficient) {
            this.saeLabel = saeLabel;
            this.coefficient = coefficient;
        }

        public String getSaeLabel() {
            return saeLabel;
        }

        public void setSaeLabel(String saeLabel) {
            this.saeLabel = saeLabel;
        }

        public Double getCoefficient() {
            return coefficient;
        }

        public void setCoefficient(Double coefficient) {
            this.coefficient = coefficient;
        }
    }
}

