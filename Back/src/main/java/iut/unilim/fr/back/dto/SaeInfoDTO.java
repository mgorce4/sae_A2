package iut.unilim.fr.back.dto;


public class SaeInfoDTO {
    private Long id;
    private String label;       // e.g., "SAE 1.01"
    private String apogeeCode;  // e.g., "SAE101"
    private Boolean isLinked;   // true if the resource is linked to this SAE

    public SaeInfoDTO() {
    }

    public SaeInfoDTO(Long id, String label, String apogeeCode, Boolean isLinked) {
        this.id = id;
        this.label = label;
        this.apogeeCode = apogeeCode;
        this.isLinked = isLinked;
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
}

