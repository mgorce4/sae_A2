package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "RESOURCE")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ressource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Resource")
    private Long idResource;

    @Column(name = "apogeeCode", nullable = false)
    private String apogeeCode;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String name;

    @Column(name = "diffMultiCompetences", nullable = false)
    private Boolean diffMultiCompetences;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_terms")
    private Terms terms;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "path_id")
    private Path path;

    public Ressource() {
    }

    public Ressource(Long idResource, String apogeeCode, String label, String name, Boolean diffMultiCompetences, Integer semester, Terms terms, Path path) {
        this.idResource = idResource;
        this.apogeeCode = apogeeCode;
        this.label = label;
        this.name = name;
        this.diffMultiCompetences = diffMultiCompetences;
        this.semester = semester;
        this.terms = terms;
        this.path = path;
    }

    public Long getIdResource() {
        return idResource;
    }

    public void setIdResource(Long idResource) {
        this.idResource = idResource;
    }

    public String getApogeeCode() {
        return apogeeCode;
    }

    public void setApogeeCode(String apogeeCode) {
        this.apogeeCode = apogeeCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDiffMultiCompetences() {
        return diffMultiCompetences;
    }

    public void setDiffMultiCompetences(Boolean diffMultiCompetences) {
        this.diffMultiCompetences = diffMultiCompetences;
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

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String toString() {
        return "Ressource [idResource=" + idResource + ", apogeeCode=" + apogeeCode + ", label=" + label + ", name="
                + name + ", diffMultiCompetences=" + diffMultiCompetences + ", semester=" + semester + ", terms="
                + terms + ", path=" + path + "]";
    }
}
