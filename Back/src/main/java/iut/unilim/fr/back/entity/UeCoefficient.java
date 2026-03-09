package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "UE_COEFFICIENT_RESOURCE")
public class UeCoefficient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Coefficient")
    private Long idCoefficient;

    @Column(nullable = false)
    private Double coefficient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UE_Number")
    private UE ue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resource")
    private Resource resource;

    public UeCoefficient() {
    }

    public UeCoefficient(Long idCoefficient, Double coefficient, UE ue, Resource resource) {
        this.idCoefficient = idCoefficient;
        this.coefficient = coefficient;
        this.ue = ue;
        this.resource = resource;
    }

    public Long getIdCoefficient() {
        return idCoefficient;
    }

    public void setIdCoefficient(Long idCoefficient) {
        this.idCoefficient = idCoefficient;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public UE getUe() {
        return ue;
    }

    public void setUe(UE ue) {
        this.ue = ue;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
