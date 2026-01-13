package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PATH")
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Path")
    private Long idPath;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_institution")
    private Institution institution;


    public Path() {
    }

    public Path(Long idPath, Integer number, String name, Institution institution) {
        this.idPath = idPath;
        this.number = number;
        this.name = name;
        this.institution = institution;
    }

    public Long getIdPath() {
        return idPath;
    }

    public void setIdPath(Long idPath) {
        this.idPath = idPath;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
}
