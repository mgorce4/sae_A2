package iut.unilim.fr.back.dto;

public class HoursDTO {
    private Integer cm;
    private Integer td;
    private Integer tp;
    private Integer total;
    private Boolean hasAlternance;

    public HoursDTO() {
    }

    public HoursDTO(Integer cm, Integer td, Integer tp, Boolean hasAlternance) {
        this.cm = cm != null ? cm : 0;
        this.td = td != null ? td : 0;
        this.tp = tp != null ? tp : 0;
        this.total = this.cm + this.td + this.tp;
        this.hasAlternance = hasAlternance != null ? hasAlternance : false;
    }

    public Integer getCm() {
        return cm;
    }

    public void setCm(Integer cm) {
        this.cm = cm;
        updateTotal();
    }

    public Integer getTd() {
        return td;
    }

    public void setTd(Integer td) {
        this.td = td;
        updateTotal();
    }

    public Integer getTp() {
        return tp;
    }

    public void setTp(Integer tp) {
        this.tp = tp;
        updateTotal();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getHasAlternance() {
        return hasAlternance;
    }

    public void setHasAlternance(Boolean hasAlternance) {
        this.hasAlternance = hasAlternance;
    }

    private void updateTotal() {
        this.total = (this.cm != null ? this.cm : 0) +
                     (this.td != null ? this.td : 0) +
                     (this.tp != null ? this.tp : 0);
    }
}

