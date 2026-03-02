package iut.unilim.fr.back.dto;

public class ResourceSheetSummaryDTO {

    private Long id;
    private String resourceLabel;
    private Integer semester;
    private String path;
    private boolean hasTeacherHours;

    public ResourceSheetSummaryDTO(Long id, String resourceLabel, Integer semester, String path, Long teacherHoursCount) {
        this.id = id;
        this.resourceLabel = resourceLabel;
        this.semester = semester;
        this.path = path;
        this.hasTeacherHours = teacherHoursCount != null && teacherHoursCount > 0;
    }

    public Long getId() { return id; }
    public String getResourceLabel() { return resourceLabel; }
    public Integer getSemester() { return semester; }
    public String getPath() { return path; }
    public boolean isHasTeacherHours() { return hasTeacherHours; }
}
