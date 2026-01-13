package iut.unilim.fr.back.dto;


public class SaeInfoDTO {
    private Long id;
    private String label;       // e.g., "SAE 1.01"
    private String apogeeCode;  // e.g., "SAE101"
    private Boolean isLinked;   // true if the resource is linked to this SAE
    private Double hours;       // Hours for formation initiale (has_alternance = 0)
    private Double hoursAlternance; // Hours for alternance (has_alternance = 1)

    public SaeInfoDTO() {
    }

    public SaeInfoDTO(Long id, String label, String apogeeCode, Boolean isLinked) {
        this.id = id;
        this.label = label;
        this.apogeeCode = apogeeCode;
        this.isLinked = isLinked;
    }

    public SaeInfoDTO(Long id, String label, String apogeeCode, Boolean isLinked, Double hours, Double hoursAlternance) {
        this.id = id;
        this.label = label;
        this.apogeeCode = apogeeCode;
        this.isLinked = isLinked;
        this.hours = hours;
        this.hoursAlternance = hoursAlternance;
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

    public String getApogeeCode() {
        return apogeeCode;
    }

    public void setApogeeCode(String apogeeCode) {
        this.apogeeCode = apogeeCode;
    }

    public Boolean getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(Boolean isLinked) {
        this.isLinked = isLinked;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getHoursAlternance() {
        return hoursAlternance;
    }

    public void setHoursAlternance(Double hoursAlternance) {
        this.hoursAlternance = hoursAlternance;
    }
}

