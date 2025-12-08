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

    @Column(name = "euapogeecode", nullable = false)
    private String euApogeeCode;

    @Column(nullable = false)
    private String label;

    @Column(name = "competencelevel", nullable = false)
    private Integer competenceLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Path")
    private Path path;

    public UE() {
    }

    public UE(Long ueNumber, String euApogeeCode, String label, Integer competenceLevel, Path path) {
        this.ueNumber = ueNumber;
        this.euApogeeCode = euApogeeCode;
        this.label = label;
        this.competenceLevel = competenceLevel;
        this.path = path;
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

    public Integer getCompetenceLevel() {
        return competenceLevel;
    }

    public void setCompetenceLevel(Integer competenceLevel) {
        this.competenceLevel = competenceLevel;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
