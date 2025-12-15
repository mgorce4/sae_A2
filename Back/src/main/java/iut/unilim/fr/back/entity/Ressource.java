package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "RESSOURCE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ressource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Ressource")
    private Long idRessource;

    @Column(name = "apogeecode", nullable = false)
    private String apogeeCode;

    @Column(nullable = false)
    private String label;

    @Column(name = "diffmulticompetences", nullable = false)
    private Boolean diffMultiCompetences;

    @Column(nullable = false)
    private Short semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Terms")
    private Terms terms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_HoursPerStudent")
    private HoursPerStudent hoursPerStudent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Coefficient")
    private UeCoefficient ueCoefficient;

    public Ressource() {
    }

    public Ressource(Long idRessource, String apogeeCode, String label, Boolean diffMultiCompetences, Short semester, Terms terms, HoursPerStudent hoursPerStudent, UeCoefficient ueCoefficient) {
        this.idRessource = idRessource;
        this.apogeeCode = apogeeCode;
        this.label = label;
        this.diffMultiCompetences = diffMultiCompetences;
        this.semester = semester;
        this.terms = terms;
        this.hoursPerStudent = hoursPerStudent;
        this.ueCoefficient = ueCoefficient;
    }

    public Long getIdRessource() {
        return idRessource;
    }

    public void setIdRessource(Long idRessource) {
        this.idRessource = idRessource;
    }

    public String getApogeeCode() {
        return apogeeCode;
    }

    public void setApogeeCode(String apogeeCode) {
        this.apogeeCode = apogeeCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getDiffMultiCompetences() {
        return diffMultiCompetences;
    }

    public void setDiffMultiCompetences(Boolean diffMultiCompetences) {
        this.diffMultiCompetences = diffMultiCompetences;
    }

    public Short getSemester() {
        return semester;
    }

    public void setSemester(Short semester) {
        this.semester = semester;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    public HoursPerStudent getHoursPerStudent() {
        return hoursPerStudent;
    }

    public void setHoursPerStudent(HoursPerStudent hoursPerStudent) {
        this.hoursPerStudent = hoursPerStudent;
    }

    public UeCoefficient getUeCoefficient() {
        return ueCoefficient;
    }

    public void setUeCoefficient(UeCoefficient ueCoefficient) {
        this.ueCoefficient = ueCoefficient;
    }
}
