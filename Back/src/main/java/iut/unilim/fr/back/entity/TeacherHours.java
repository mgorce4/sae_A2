package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TEACHER_HOURS")
public class TeacherHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_TeacherHours")
    private Long idTeacherHours;

    @Column
    private Double cm;

    @Column
    private Double td;

    @Column
    private Double tp;

    @Column(name = "isalternance")
    private Boolean isAlternance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public TeacherHours() {
    }

    public TeacherHours(Long idTeacherHours, Double cm, Double td, Double tp, Boolean isAlternance, RessourceSheet resourceSheet) {
        this.idTeacherHours = idTeacherHours;
        this.cm = cm;
        this.td = td;
        this.tp = tp;
        this.isAlternance = isAlternance;
        this.resourceSheet = resourceSheet;
    }

    public Long getIdTeacherHours() {
        return idTeacherHours;
    }

    public void setIdTeacherHours(Long idTeacherHours) {
        this.idTeacherHours = idTeacherHours;
    }

    public Double getCm() {
        return cm;
    }

    public void setCm(Double cm) {
        this.cm = cm;
    }

    public Double getTd() {
        return td;
    }

    public void setTd(Double td) {
        this.td = td;
    }

    public Double getTp() {
        return tp;
    }

    public void setTp(Double tp) {
        this.tp = tp;
    }

    public Boolean getIsAlternance() {
        return isAlternance;
    }

    public void setIsAlternance(Boolean isAlternance) {
        this.isAlternance = isAlternance;
    }

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }
}

