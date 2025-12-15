package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "NATIONAL_PROGRAM_OBJECTIVE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NationalProgramObjective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_NationalProgramObjective")
    private Long idNationalProgramObjective;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_RessourceSheet")
    private RessourceSheet ressourceSheet;

    public NationalProgramObjective() {
    }

    public NationalProgramObjective(Long idNationalProgramObjective, String content, RessourceSheet ressourceSheet) {
        this.idNationalProgramObjective = idNationalProgramObjective;
        this.content = content;
        this.ressourceSheet = ressourceSheet;
    }

    public Long getIdNationalProgramObjective() {
        return idNationalProgramObjective;
    }

    public void setIdNationalProgramObjective(Long idNationalProgramObjective) {
        this.idNationalProgramObjective = idNationalProgramObjective;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RessourceSheet getRessourceSheet() {
        return ressourceSheet;
    }

    public void setRessourceSheet(RessourceSheet ressourceSheet) {
        this.ressourceSheet = ressourceSheet;
    }
}

