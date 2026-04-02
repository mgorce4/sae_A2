package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RESOURCE_SHEET")
public class ResourceSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ResourceSheet")
    private Long idResourceSheet;

    @Column
    private LocalDate year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Resource")
    private Resource resource;

    public ResourceSheet() {
    }

    public ResourceSheet(Long idResourceSheet, LocalDate year, Resource resource) {
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

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
