package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "UE_COEFFICIENT")
public class UeCoefficient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Coefficient")
    private Long idCoefficient;

    @Column(nullable = false)
    private Integer coefficient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UE_Number")
    private UE ue;

    public UeCoefficient() {
    }

    public UeCoefficient(Long idCoefficient, Integer coefficient, UE ue) {
        this.idCoefficient = idCoefficient;
        this.coefficient = coefficient;
        this.ue = ue;
    }

    public Long getIdCoefficient() {
        return idCoefficient;
    }

    public void setIdCoefficient(Long idCoefficient) {
        this.idCoefficient = idCoefficient;
    }

    public Integer getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Integer coefficient) {
        this.coefficient = coefficient;
    }

    public UE getUe() {
        return ue;
    }

    public void setUe(UE ue) {
        this.ue = ue;
    }
}
