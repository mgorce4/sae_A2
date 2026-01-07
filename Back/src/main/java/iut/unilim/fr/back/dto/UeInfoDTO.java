package iut.unilim.fr.back.dto;

public class UeInfoDTO {
    private Long ueNumber;
    private String label;           // e.g., "UE2.1"
    private String name;            // e.g., "Understanding organizations"
    private Integer coefficient;    // Resource coefficient in this UE
    private Integer competenceLevel; // Competence level

    public UeInfoDTO() {
    }

    public UeInfoDTO(Long ueNumber, String label, String name, Integer coefficient, Integer competenceLevel) {
        this.ueNumber = ueNumber;
        this.label = label;
        this.name = name;
        this.coefficient = coefficient;
        this.competenceLevel = competenceLevel;
    }

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

    public Integer getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Integer coefficient) {
        this.coefficient = coefficient;
    }

    public Integer getCompetenceLevel() {
        return competenceLevel;
    }

    public void setCompetenceLevel(Integer competenceLevel) {
        this.competenceLevel = competenceLevel;
    }
}

