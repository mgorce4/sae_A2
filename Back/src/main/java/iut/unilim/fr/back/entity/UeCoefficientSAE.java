package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "UE_COEFFICIENT_SAE")
public class UeCoefficientSAE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Coefficient_SAE")
    private Long idCoefficientSAE;

    @Column(nullable = false)
    private Double coefficient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UE_Number")
    private UE ue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_SAE")
    private SAE sae;

    public UeCoefficientSAE() {
    }

    public UeCoefficientSAE(Long idCoefficientSAE, Double coefficient, UE ue, SAE sae) {
        this.idCoefficientSAE = idCoefficientSAE;
        this.coefficient = coefficient;
        this.ue = ue;
        this.sae = sae;
    }

    public Long getIdCoefficientSAE() {
        return idCoefficientSAE;
    }

    public void setIdCoefficientSAE(Long idCoefficientSAE) {
        this.idCoefficientSAE = idCoefficientSAE;
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

    public SAE getSae() {
        return sae;
    }

    public void setSae(SAE sae) {
        this.sae = sae;
    }
}

