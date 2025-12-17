package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SAE_HOURS")
public class SAEHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_SAE_Hours")
    private Long idSAEHours;

    @Column(nullable = false)
    private Integer hours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_SAE")
    private SAE sae;

    public SAEHours() {
    }

    public SAEHours(Long idSAEHours, Integer hours, SAE sae) {
        this.idSAEHours = idSAEHours;
        this.hours = hours;
        this.sae = sae;
    }

    public Long getIdSAEHours() {
        return idSAEHours;
    }

    public void setIdSAEHours(Long idSAEHours) {
        this.idSAEHours = idSAEHours;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public SAE getSae() {
        return sae;
    }

    public void setSae(SAE sae) {
        this.sae = sae;
    }
}

