package iut.unilim.fr.back.dto;

public class SkillDTO {
    private Long id;
    private String label;       // e.g., "AC1"
    private String description; // e.g., "Analyze data"

    public SkillDTO() {
    }

    public SkillDTO(Long id, String label, String description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

