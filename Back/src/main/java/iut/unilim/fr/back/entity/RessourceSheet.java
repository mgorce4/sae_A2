package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RESOURCE_SHEET")
public class RessourceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ResourceSheet")
    private Long idResourceSheet;

    @Column
    private LocalDate year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Resource")
    private Ressource resource;

    public RessourceSheet() {
    }

    public RessourceSheet(Long idResourceSheet, LocalDate year, Ressource resource) {
        this.idResourceSheet = idResourceSheet;
        this.year = year;
        this.resource = resource;
    }

    public Long getIdResourceSheet() {
        return idResourceSheet;
    }

    public void setIdResourceSheet(Long idResourceSheet) {
        this.idResourceSheet = idResourceSheet;
    }

    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }

    public Ressource getResource() {
        return resource;
    }

    public void setResource(Ressource resource) {
        this.resource = resource;
    }
}
