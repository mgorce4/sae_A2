package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "HOURS_PER_STUDENT")
public class HoursPerStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_HoursPerStudent")
    private Long idHoursPerStudent;

    @Column
    private Integer cm;

    @Column
    private Integer td;

    @Column
    private Integer tp;

    public HoursPerStudent() {
    }

    public HoursPerStudent(Long idHoursPerStudent, Integer cm, Integer td, Integer tp) {
        this.idHoursPerStudent = idHoursPerStudent;
        this.cm = cm;
        this.td = td;
        this.tp = tp;
    }

    public Long getIdHoursPerStudent() {
        return idHoursPerStudent;
    }

    public void setIdHoursPerStudent(Long idHoursPerStudent) {
        this.idHoursPerStudent = idHoursPerStudent;
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
}
