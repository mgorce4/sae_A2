package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "HOURS_PER_STUDENT")
public class HoursPerStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_HoursPerStudent")
    private Long idHoursPerStudent;

    @Column(name = "has_alternance", nullable = false)
    private Boolean hasAlternance;

    @Column
    private Integer cm;

    @Column
    private Integer td;

    @Column
    private Integer tp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resource")
    private Ressource resource;

    public HoursPerStudent() {
    }

    public HoursPerStudent(Long idHoursPerStudent, Boolean hasAlternance, Integer cm, Integer td, Integer tp, Ressource resource) {
        this.idHoursPerStudent = idHoursPerStudent;
        this.hasAlternance = hasAlternance;
        this.cm = cm;
        this.td = td;
        this.tp = tp;
        this.resource = resource;
    }

    public Long getIdHoursPerStudent() {
        return idHoursPerStudent;
    }

    public void setIdHoursPerStudent(Long idHoursPerStudent) {
        this.idHoursPerStudent = idHoursPerStudent;
    }

    public Boolean getHasAlternance() {
        return hasAlternance;
    }

    public void setHasAlternance(Boolean hasAlternance) {
        this.hasAlternance = hasAlternance;
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

    public Ressource getResource() {
        return resource;
    }

    public void setResource(Ressource resource) {
        this.resource = resource;
    }
}
