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
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public NationalProgramObjective() {
    }

    public NationalProgramObjective(Long idNationalProgramObjective, String content, RessourceSheet resourceSheet) {
        this.idNationalProgramObjective = idNationalProgramObjective;
        this.content = content;
        this.resourceSheet = resourceSheet;
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

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }
}

