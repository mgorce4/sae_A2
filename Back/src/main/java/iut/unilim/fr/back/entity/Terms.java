package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TERMS")
public class Terms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Terms")
    private Long idTerms;

    @Column(nullable = false)
    private String code;

    public Terms() {
    }

    public Terms(Long idTerms, String code) {
        this.idTerms = idTerms;
        this.code = code;
    }

    public Long getIdTerms() {
        return idTerms;
    }

    public void setIdTerms(Long idTerms) {
        this.idTerms = idTerms;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
