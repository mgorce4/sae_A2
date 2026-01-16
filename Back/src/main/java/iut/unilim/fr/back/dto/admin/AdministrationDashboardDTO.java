package iut.unilim.fr.back.dto.admin;

import java.time.LocalDate;


public class AdministrationDashboardDTO {

    // Resource sheet information
    private Long resourceSheetId;
    private LocalDate year;

    // Resource information
    private Long resourceId;
    private String resourceLabel;      // e.g., "R1.01"
    private String resourceName;       // e.g., "Python"
    private Integer semester;

    // Main teacher
    private Long mainTeacherId;
    private String mainTeacherName;    // e.g., "Jean Dupont"

    // Institution
    private Long institutionId;
    private String institutionName;    // e.g., "GEA"

    // Sheet status
    private Boolean isSubmitted;       // true if the sheet is submitted
    private LocalDate submittedDate;

    public AdministrationDashboardDTO() {
    }

    public AdministrationDashboardDTO(Long resourceSheetId, LocalDate year, Long resourceId,
                                     String resourceLabel, String resourceName, Integer semester,
                                     Long mainTeacherId, String mainTeacherName,
                                     Long institutionId, String institutionName,
                                     Boolean isSubmitted, LocalDate submittedDate) {
        this.resourceSheetId = resourceSheetId;
        this.year = year;
        this.resourceId = resourceId;
        this.resourceLabel = resourceLabel;
        this.resourceName = resourceName;
        this.semester = semester;
        this.mainTeacherId = mainTeacherId;
        this.mainTeacherName = mainTeacherName;
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.isSubmitted = isSubmitted;
        this.submittedDate = submittedDate;
    }

    // Getters et Setters
    public Long getResourceSheetId() {
        return resourceSheetId;
    }

    public void setResourceSheetId(Long resourceSheetId) {
        this.resourceSheetId = resourceSheetId;
    }

    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Long getMainTeacherId() {
        return mainTeacherId;
    }

    public void setMainTeacherId(Long mainTeacherId) {
        this.mainTeacherId = mainTeacherId;
    }

    public String getMainTeacherName() {
        return mainTeacherName;
    }

    public void setMainTeacherName(String mainTeacherName) {
        this.mainTeacherName = mainTeacherName;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Boolean getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(Boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDate submittedDate) {
        this.submittedDate = submittedDate;
    }
}

