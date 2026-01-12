package iut.unilim.fr.back.dto.admin;

import java.util.List;

public class MCCCResourceDTO {

    private Long resourceId;
    private String label;              // e.g., "R1.01"
    private String name;               // e.g., "Python"
    private String apogeeCode;         // e.g., "R101"
    private Integer semester;
    private Long institutionId;        // Institution ID
    private String termsCode;          // Terms code (e.g., "NGCC")

    // PN hours (National Program)
    private Integer pnCm;
    private Integer pnTd;
    private Integer pnTp;
    private Integer pnTotal;

    // Initial training hours
    private Integer initialCm;
    private Integer initialTd;
    private Integer initialTp;
    private Integer initialProject;
    private Integer initialTotal;

    // Work-study hours
    private Integer alternanceCm;
    private Integer alternanceTd;
    private Integer alternanceTp;
    private Integer alternanceProject;
    private Integer alternanceTotal;

    // Main teacher and coefficient
    private String mainTeacherName;
    private List<TeacherInfoDTO> teachers;  // All teachers

    // UEs and coefficients
    private List<UECoefficientDTO> ueCoefficients;

    // Linked SAEs
    private List<String> linkedSaes;   // Labels of linked SAEs

    public MCCCResourceDTO() {
    }

    // Getters and Setters
    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public Integer getPnCm() {
        return pnCm;
    }

    public void setPnCm(Integer pnCm) {
        this.pnCm = pnCm;
    }

    public Integer getPnTd() {
        return pnTd;
    }

    public void setPnTd(Integer pnTd) {
        this.pnTd = pnTd;
    }

    public Integer getPnTp() {
        return pnTp;
    }

    public void setPnTp(Integer pnTp) {
        this.pnTp = pnTp;
    }

    public Integer getPnTotal() {
        return pnTotal;
    }

    public void setPnTotal(Integer pnTotal) {
        this.pnTotal = pnTotal;
    }

    public Integer getInitialCm() {
        return initialCm;
    }

    public void setInitialCm(Integer initialCm) {
        this.initialCm = initialCm;
    }

    public Integer getInitialTd() {
        return initialTd;
    }

    public void setInitialTd(Integer initialTd) {
        this.initialTd = initialTd;
    }

    public Integer getInitialTp() {
        return initialTp;
    }

    public void setInitialTp(Integer initialTp) {
        this.initialTp = initialTp;
    }

    public Integer getInitialProject() {
        return initialProject;
    }

    public void setInitialProject(Integer initialProject) {
        this.initialProject = initialProject;
    }

    public Integer getInitialTotal() {
        return initialTotal;
    }

    public void setInitialTotal(Integer initialTotal) {
        this.initialTotal = initialTotal;
    }

    public Integer getAlternanceCm() {
        return alternanceCm;
    }

    public void setAlternanceCm(Integer alternanceCm) {
        this.alternanceCm = alternanceCm;
    }

    public Integer getAlternanceTd() {
        return alternanceTd;
    }

    public void setAlternanceTd(Integer alternanceTd) {
        this.alternanceTd = alternanceTd;
    }

    public Integer getAlternanceTp() {
        return alternanceTp;
    }

    public void setAlternanceTp(Integer alternanceTp) {
        this.alternanceTp = alternanceTp;
    }

    public Integer getAlternanceProject() {
        return alternanceProject;
    }

    public void setAlternanceProject(Integer alternanceProject) {
        this.alternanceProject = alternanceProject;
    }

    public Integer getAlternanceTotal() {
        return alternanceTotal;
    }

    public void setAlternanceTotal(Integer alternanceTotal) {
        this.alternanceTotal = alternanceTotal;
    }

    public String getMainTeacherName() {
        return mainTeacherName;
    }

    public void setMainTeacherName(String mainTeacherName) {
        this.mainTeacherName = mainTeacherName;
    }

    public List<TeacherInfoDTO> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherInfoDTO> teachers) {
        this.teachers = teachers;
    }

    public List<UECoefficientDTO> getUeCoefficients() {
        return ueCoefficients;
    }

    public void setUeCoefficients(List<UECoefficientDTO> ueCoefficients) {
        this.ueCoefficients = ueCoefficients;
    }

    public List<String> getLinkedSaes() {
        return linkedSaes;
    }

    public void setLinkedSaes(List<String> linkedSaes) {
        this.linkedSaes = linkedSaes;
    }

    // Inner class for teacher information
    public static class TeacherInfoDTO {
        private Long teacherId;
        private String teacherName;

        public TeacherInfoDTO() {
        }

        public TeacherInfoDTO(Long teacherId, String teacherName) {
            this.teacherId = teacherId;
            this.teacherName = teacherName;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }
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

