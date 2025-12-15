package iut.unilim.fr.back.entity;
}
    }
        this.ressourceSheet = ressourceSheet;
    public void setRessourceSheet(RessourceSheet ressourceSheet) {

    }
        return ressourceSheet;
    public RessourceSheet getRessourceSheet() {

    }
        this.content = content;
    public void setContent(String content) {

    }
        return content;
    public String getContent() {

    }
        this.idNationalProgramObjective = idNationalProgramObjective;
    public void setIdNationalProgramObjective(Long idNationalProgramObjective) {

    }
        return idNationalProgramObjective;
    public Long getIdNationalProgramObjective() {

    }
        this.ressourceSheet = ressourceSheet;
        this.content = content;
        this.idNationalProgramObjective = idNationalProgramObjective;
    public NationalProgramObjective(Long idNationalProgramObjective, String content, RessourceSheet ressourceSheet) {

    }
    public NationalProgramObjective() {

    private RessourceSheet ressourceSheet;
    @JoinColumn(name = "id_RessourceSheet")
    @ManyToOne(fetch = FetchType.LAZY)

    private String content;
    @Column(nullable = false, columnDefinition = "TEXT")

    private Long idNationalProgramObjective;
    @Column(name = "id_NationalProgramObjective")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
public class NationalProgramObjective {
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "NATIONAL_PROGRAM_OBJECTIVE")
@Entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


