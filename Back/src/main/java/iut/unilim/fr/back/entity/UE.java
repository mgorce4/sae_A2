package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "UE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UE_Number")
    private Long ueNumber;

    @Column(name = "EuApogeeCode", nullable = false)
    private String euApogeeCode;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String name;

    @Column(name = "competenceLevel", nullable = false)
    private Integer competenceLevel;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Path")
    private Path path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terms")
    private Terms terms;

    public UE() {
    }

    public UE(Long ueNumber, String euApogeeCode, String label, String name, Integer competenceLevel, Integer semester, Path path, Terms terms) {
        this.ueNumber = ueNumber;
        this.euApogeeCode = euApogeeCode;
        this.label = label;
        this.name = name;
        this.competenceLevel = competenceLevel;
        this.semester = semester;
        this.path = path;
        this.terms = terms;
    }

    public Long getUeNumber() {
        return ueNumber;
    }

    public void setUeNumber(Long ueNumber) {
        this.ueNumber = ueNumber;
    }

    public String getEuApogeeCode() {
        return euApogeeCode;
    }

    public void setEuApogeeCode(String euApogeeCode) {
        this.euApogeeCode = euApogeeCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCompetenceLevel() {
        return competenceLevel;
    }

    public void setCompetenceLevel(Integer competenceLevel) {
        this.competenceLevel = competenceLevel;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }
}
