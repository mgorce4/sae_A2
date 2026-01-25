package iut.unilim.fr.back.dto;

import java.util.List;

import iut.unilim.fr.back.entity.SAE;

public class ResourceDTO {
    private String label;
    private String name;
    private String apogeeCode;
    private Integer semester;
    private Long institutionId;
    private String termsCode;
    private Long pathId;
    private Double initialCm;
    private Double initialTd;
    private Double initialTp;
    private Double alternanceCm;
    private Double alternanceTd;
    private Double alternanceTp;
    private List<String> mainTeachers;  // "Firstname Lastname"
    private List<String> teachers;  // ["Firstname Lastname", ...]
    private List<UeCoefficientDTO> ueCoefficients;
    private List<Long> linkedSaesIds;
    private List<SAE> linkedSaes;

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

        public String toString() {
            return "UeCoefficientDTO{" +
                    "ueId=" + ueId +
                    ", ueLabel='" + ueLabel + '\'' +
                    ", coefficient=" + coefficient +
                    '}';
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

    public Double getInitialCm() {
        return initialCm;
    }

    public void setInitialCm(Double cmInitial) {
        this.initialCm = cmInitial;
    }

    public Double getInitialTd() {
        return initialTd;
    }

    public void setInitialTd(Double tdInitial) {
        this.initialTd = tdInitial;
    }

    public Double getInitialTp() {
        return initialTp;
    }

    public void setInitialTp(Double tpInitial) {
        this.initialTp = tpInitial;
    }

    public Double getAlternanceCm() {
        return alternanceCm;
    }

    public void setAlternanceCm(Double cmAlternance) {
        this.alternanceCm = cmAlternance;
    }

    public Double getAlternanceTd() {
        return alternanceTd;
    }

    public void setAlternanceTd(Double tdAlternance) {
        this.alternanceTd = tdAlternance;
    }

    public Double getAlternanceTp() {
        return alternanceTp;
    }

    public void setAlternanceTp(Double tpAlternance) {
        this.alternanceTp = tpAlternance;
    }

    public List<String> getMainTeachers() {
        return mainTeachers;
    }

    public void setMainTeachers(List<String> mainTeachers) {
        this.mainTeachers = mainTeachers;
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

    public List<SAE> getLinkedSaes() {
        return linkedSaes;
    }

    public void setLinkedSaes(List<SAE> linkedSaes) {
        this.linkedSaes = linkedSaes;
    }

    public List<Long> getLinkedSaesIds() {
        return linkedSaesIds;
    }

    public void setLinkedSaesIds(List<Long> linkedSaesIds) {
        this.linkedSaesIds = linkedSaesIds;
    }

    public String toString() {
        return "ResourceDTO{" +
                "label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", apogeeCode='" + apogeeCode + '\'' +
                ", semester=" + semester +
                ", institutionId=" + institutionId +
                ", termsCode='" + termsCode + '\'' +
                ", pathId=" + pathId +
                ", initialCm=" + initialCm +
                ", initialTd=" + initialTd +
                ", initialTp=" + initialTp +
                ", alternanceCm=" + alternanceCm +
                ", alternanceTd=" + alternanceTd +
                ", alternanceTp=" + alternanceTp +
                ", mainTeacher='" + mainTeachers + '\'' +
                ", teachers=" + teachers +
                ", ueCoefficients=" + ueCoefficients +
                ", linkedSaesIds=" + linkedSaesIds +
                ", linkedSaes=" + linkedSaes +
                '}';
    }
}

