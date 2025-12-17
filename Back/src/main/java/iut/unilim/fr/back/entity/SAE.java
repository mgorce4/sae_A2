package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "SAE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SAE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_SAE")
    private Long idSAE;

    @Column(nullable = false)
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terms")
    private Terms terms;

    public SAE() {
    }

    public SAE(Long idSAE, String label, Terms terms) {
        this.idSAE = idSAE;
        this.label = label;
        this.terms = terms;
    }

    public Long getIdSAE() {
        return idSAE;
    }

    public void setIdSAE(Long idSAE) {
        this.idSAE = idSAE;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }
}

