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

    @Column(name = "apogeeCode", columnDefinition = "TEXT")
    private String apogeeCode;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terms")
    private Terms terms;

    public SAE() {
    }

    public SAE(Long idSAE, String label, String apogeeCode, Integer semester, Terms terms) {
        this.idSAE = idSAE;
        this.label = label;
        this.apogeeCode = apogeeCode;
        this.semester = semester;
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

    public String getApogeeCode() {
        return apogeeCode;
    }

    public void setApogeeCode(String apogeeCode) {
        this.apogeeCode = apogeeCode;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }
}

