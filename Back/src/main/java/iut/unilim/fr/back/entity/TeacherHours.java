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
    private String cm;

    @Column
    private String td;

    @Column
    private String tp;

    @Column(name = "isalternance")
    private Boolean isAlternance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public TeacherHours() {
    }

    public TeacherHours(Long idTeacherHours, String cm, String td, String tp, Boolean isAlternance, RessourceSheet resourceSheet) {
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

    public String getCm() {
        return cm;
    }

    public void setCm(String cm) {
        this.cm = cm;
    }

    public String getTd() {
        return td;
    }

    public void setTd(String td) {
        this.td = td;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
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

