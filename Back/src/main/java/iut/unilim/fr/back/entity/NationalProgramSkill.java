package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "NATIONAL_PROGRAM_SKILL")
public class NationalProgramSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Skill")
    private Long idSkill;

    @Column(columnDefinition = "TEXT")
    private String label;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public NationalProgramSkill() {
    }

    public NationalProgramSkill(Long idSkill, String label, String description, RessourceSheet resourceSheet) {
        this.idSkill = idSkill;
        this.label = label;
        this.description = description;
        this.resourceSheet = resourceSheet;
    }

    public Long getIdSkill() {
        return idSkill;
    }

    public void setIdSkill(Long idSkill) {
        this.idSkill = idSkill;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }
}

