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
    private Integer cm;

    @Column
    private Integer td;

    @Column
    private Integer tp;

    @Column(name = "isalternance")
    private Boolean isAlternance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public TeacherHours() {
    }

    public TeacherHours(Long idTeacherHours, Integer cm, Integer td, Integer tp, Boolean isAlternance, RessourceSheet resourceSheet) {
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

    public Integer getCm() {
        return cm;
    }

    public void setCm(Integer cm) {
        this.cm = cm;
    }

    public Integer getTd() {
        return td;
    }

    public void setTd(Integer td) {
        this.td = td;
    }

    public Integer getTp() {
        return tp;
    }

    public void setTp(Integer tp) {
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

