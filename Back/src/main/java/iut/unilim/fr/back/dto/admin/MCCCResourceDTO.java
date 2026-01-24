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
    private Long pathId;               // Path ID
    private String pathName;           // Path name

    // PN hours (National Program)
    private Double pnCm;
    private Double pnTd;
    private Double pnTp;
    private double pnTotal;

    // Initial training hours
    private Double initialCm;
    private Double initialTd;
    private Double initialTp;
    private Integer initialProject;
    private double initialTotal;

    // Work-study hours
    private Double alternanceCm;
    private Double alternanceTd;
    private Double alternanceTp;
    private Integer alternanceProject;
    private double alternanceTotal;

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

    public Double getPnCm() {
        return pnCm;
    }

    public void setPnCm(Double pnCm) {
        this.pnCm = pnCm;
    }

    public Double getPnTd() {
        return pnTd;
    }

    public void setPnTd(Double pnTd) {
        this.pnTd = pnTd;
    }

    public Double getPnTp() {
        return pnTp;
    }

    public void setPnTp(Double pnTp) {
        this.pnTp = pnTp;
    }

    public double getPnTotal() {
        return pnTotal;
    }

    public void setPnTotal(double pnTotal) {
        this.pnTotal = pnTotal;
    }

    public Double getInitialCm() {
        return initialCm;
    }

    public void setInitialCm(Double initialCm) {
        this.initialCm = initialCm;
    }

    public Double getInitialTd() {
        return initialTd;
    }

    public void setInitialTd(Double initialTd) {
        this.initialTd = initialTd;
    }

    public Double getInitialTp() {
        return initialTp;
    }

    public void setInitialTp(Double initialTp) {
        this.initialTp = initialTp;
    }

    public Integer getInitialProject() {
        return initialProject;
    }

    public void setInitialProject(Integer initialProject) {
        this.initialProject = initialProject;
    }

    public double getInitialTotal() {
        return initialTotal;
    }

    public void setInitialTotal(double initialTotal) {
        this.initialTotal = initialTotal;
    }

    public Double getAlternanceCm() {
        return alternanceCm;
    }

    public void setAlternanceCm(Double alternanceCm) {
        this.alternanceCm = alternanceCm;
    }

    public Double getAlternanceTd() {
        return alternanceTd;
    }

    public void setAlternanceTd(Double alternanceTd) {
        this.alternanceTd = alternanceTd;
    }

    public Double getAlternanceTp() {
        return alternanceTp;
    }

    public void setAlternanceTp(Double alternanceTp) {
        this.alternanceTp = alternanceTp;
    }

    public Integer getAlternanceProject() {
        return alternanceProject;
    }

    public void setAlternanceProject(Integer alternanceProject) {
        this.alternanceProject = alternanceProject;
    }

    public double getAlternanceTotal() {
        return alternanceTotal;
    }

    public void setAlternanceTotal(double alternanceTotal) {
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
        private Long ueId;             // UE ID for identification
        private String ueLabel;        // e.g., "UE2.1"
        private String ueName;         // e.g., "Understanding organizations"
        private Double coefficient;

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

