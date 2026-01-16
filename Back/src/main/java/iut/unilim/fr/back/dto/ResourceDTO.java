package iut.unilim.fr.back.dto;

import java.util.List;

public class ResourceDTO {
    private String label;
    private String name;
    private String apogeeCode;
    private Integer semester;
    private Long institutionId;
    private String termsCode;
    private Long pathId;
    private Double cmInitial;
    private Double tdInitial;
    private Double tpInitial;
    private Double cmAlternance;
    private Double tdAlternance;
    private Double tpAlternance;
    private String mainTeacher;  // "Firstname Lastname"
    private List<String> teachers;  // ["Firstname Lastname", ...]
    private List<UeCoefficientDTO> ueCoefficients;

    public static class UeCoefficientDTO {
        private Long ueId;
        private String ueLabel;
        private Double coefficient;

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

        public Double getCoefficient() {
            return coefficient;
        }

        public void setCoefficient(Double coefficient) {
            this.coefficient = coefficient;
        }
    }

    // Getters and Setters
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

    public Double getCmInitial() {
        return cmInitial;
    }

    public void setCmInitial(Double cmInitial) {
        this.cmInitial = cmInitial;
    }

    public Double getTdInitial() {
        return tdInitial;
    }

    public void setTdInitial(Double tdInitial) {
        this.tdInitial = tdInitial;
    }

    public Double getTpInitial() {
        return tpInitial;
    }

    public void setTpInitial(Double tpInitial) {
        this.tpInitial = tpInitial;
    }

    public Double getCmAlternance() {
        return cmAlternance;
    }

    public void setCmAlternance(Double cmAlternance) {
        this.cmAlternance = cmAlternance;
    }

    public Double getTdAlternance() {
        return tdAlternance;
    }

    public void setTdAlternance(Double tdAlternance) {
        this.tdAlternance = tdAlternance;
    }

    public Double getTpAlternance() {
        return tpAlternance;
    }

    public void setTpAlternance(Double tpAlternance) {
        this.tpAlternance = tpAlternance;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public List<String> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<String> teachers) {
        this.teachers = teachers;
    }

    public List<UeCoefficientDTO> getUeCoefficients() {
        return ueCoefficients;
    }

    public void setUeCoefficients(List<UeCoefficientDTO> ueCoefficients) {
        this.ueCoefficients = ueCoefficients;
    }
}

