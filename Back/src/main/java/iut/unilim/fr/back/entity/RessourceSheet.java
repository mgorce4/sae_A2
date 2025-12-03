package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RESSOURCE_SHEET")
public class RessourceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_RessourceSheet")
    private Long idRessourceSheet;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String competence;

    @Column(name = "SAE", columnDefinition = "TEXT")
    private String sae;

    @Column
    private LocalDate year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Ressource")
    private Ressource ressource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_PedagogicalContent")
    private PedagogicalContent pedagogicalContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_RessourceTracking")
    private RessourceTracking ressourceTracking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_User")
    private UserSyncadia user;

    public RessourceSheet() {
    }

    public RessourceSheet(Long idRessourceSheet, String name, String competence, String sae, LocalDate year, Ressource ressource, PedagogicalContent pedagogicalContent, RessourceTracking ressourceTracking, UserSyncadia user) {
        this.idRessourceSheet = idRessourceSheet;
        this.name = name;
        this.competence = competence;
        this.sae = sae;
        this.year = year;
        this.ressource = ressource;
        this.pedagogicalContent = pedagogicalContent;
        this.ressourceTracking = ressourceTracking;
        this.user = user;
    }

    public Long getIdRessourceSheet() {
        return idRessourceSheet;
    }

    public void setIdRessourceSheet(Long idRessourceSheet) {
        this.idRessourceSheet = idRessourceSheet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getSae() {
        return sae;
    }

    public void setSae(String sae) {
        this.sae = sae;
    }

    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }

    public Ressource getRessource() {
        return ressource;
    }

    public void setRessource(Ressource ressource) {
        this.ressource = ressource;
    }

    public PedagogicalContent getPedagogicalContent() {
        return pedagogicalContent;
    }

    public void setPedagogicalContent(PedagogicalContent pedagogicalContent) {
        this.pedagogicalContent = pedagogicalContent;
    }

    public RessourceTracking getRessourceTracking() {
        return ressourceTracking;
    }

    public void setRessourceTracking(RessourceTracking ressourceTracking) {
        this.ressourceTracking = ressourceTracking;
    }

    public UserSyncadia getUser() {
        return user;
    }

    public void setUser(UserSyncadia user) {
        this.user = user;
    }
}
