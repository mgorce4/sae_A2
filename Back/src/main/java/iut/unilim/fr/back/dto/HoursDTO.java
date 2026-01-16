package iut.unilim.fr.back.dto;

public class HoursDTO {
    private Double cm;
    private Double td;
    private Double tp;
    private double total;
    private Boolean hasAlternance;

    public HoursDTO() {
    }

    public HoursDTO(Double cm, Double td, Double tp, Boolean hasAlternance) {
        this.cm = cm != null ? cm : 0;
        this.td = td != null ? td : 0;
        this.tp = tp != null ? tp : 0;
        this.total = this.cm + this.td + this.tp;
        this.hasAlternance = hasAlternance != null ? hasAlternance : false;
    }

    public Double getCm() {
        return cm;
    }

    public void setCm(Double cm) {
        this.cm = cm;
        updateTotal();
    }

    public Double getTd() {
        return td;
    }

    public void setTd(Double td) {
        this.td = td;
        updateTotal();
    }

    public Double getTp() {
        return tp;
    }

    public void setTp(Double tp) {
        this.tp = tp;
        updateTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Boolean getHasAlternance() {
        return hasAlternance != null ? hasAlternance: false;
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

