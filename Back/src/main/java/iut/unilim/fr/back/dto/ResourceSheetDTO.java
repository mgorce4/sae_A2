package iut.unilim.fr.back.dto;

import java.time.LocalDate;
import java.util.List;

public class ResourceSheetDTO {

    // Basic information
    private Long id;
    private LocalDate year;

    // Resource information
    private Long resourceId;
    private String resourceName;        // e.g., "Python"
    private String resourceLabel;       // e.g., "R1.01"
    private String resourceApogeeCode;  // e.g., "R101"
    private String qualityReference;    // e.g., "IU EN FOR 001"
    private Integer semester;
    private Boolean diffMultiCompetences;

    // Department
    private String department;          // e.g., "GEA"

    // Main teacher and teachers
    private String mainTeacher;         // e.g., "Jean Dupont"
    private List<String> teachers;      // List of teachers

    // UE and coefficients
    private List<UeInfoDTO> ueReferences; // e.g., ["UE2.1", "UE3.2"]

    // Objectives and skills
    private String objective;           // National program objective
    private List<SkillDTO> skills;      // National program skills

    // Linked SAEs
    private List<SaeInfoDTO> linkedSaes;

    // Keywords
    private List<String> keywords;

    // Implementation modalities
    private List<String> modalities;

    // PN hours (National Program)
    private HoursDTO hoursPN;

    // Teacher hours
    private HoursDTO hoursTeacher;

    // Pedagogical content
    private PedagogicalContentDTO pedagogicalContent;

    // Resource tracking
    private ResourceTrackingDTO tracking;

    // Constructors
    public ResourceSheetDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceLabel() {
        return resourceLabel;
    }

    public void setResourceLabel(String resourceLabel) {
        this.resourceLabel = resourceLabel;
    }

    public String getResourceApogeeCode() {
        return resourceApogeeCode;
    }

    public void setResourceApogeeCode(String resourceApogeeCode) {
        this.resourceApogeeCode = resourceApogeeCode;
    }

    public String getQualityReference() {
        return qualityReference;
    }

    public void setQualityReference(String qualityReference) {
        this.qualityReference = qualityReference;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Boolean getDiffMultiCompetences() {
        return diffMultiCompetences;
    }

    public void setDiffMultiCompetences(Boolean diffMultiCompetences) {
        this.diffMultiCompetences = diffMultiCompetences;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public List<UeInfoDTO> getUeReferences() {
        return ueReferences;
    }

    public void setUeReferences(List<UeInfoDTO> ueReferences) {
        this.ueReferences = ueReferences;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public List<SaeInfoDTO> getLinkedSaes() {
        return linkedSaes;
    }

    public void setLinkedSaes(List<SaeInfoDTO> linkedSaes) {
        this.linkedSaes = linkedSaes;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getModalities() {
        return modalities;
    }

    public void setModalities(List<String> modalities) {
        this.modalities = modalities;
    }

    public HoursDTO getHoursPN() {
        return hoursPN;
    }

    public void setHoursPN(HoursDTO hoursPN) {
        this.hoursPN = hoursPN;
    }

    public HoursDTO getHoursTeacher() {
        return hoursTeacher;
    }

    public void setHoursTeacher(HoursDTO hoursTeacher) {
        this.hoursTeacher = hoursTeacher;
    }

    public PedagogicalContentDTO getPedagogicalContent() {
        return pedagogicalContent;
    }

    public void setPedagogicalContent(PedagogicalContentDTO pedagogicalContent) {
        this.pedagogicalContent = pedagogicalContent;
    }

    public ResourceTrackingDTO getTracking() {
        return tracking;
    }

    public void setTracking(ResourceTrackingDTO tracking) {
        this.tracking = tracking;
    }
}

