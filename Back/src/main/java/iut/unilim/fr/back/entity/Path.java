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

    public Path() {
    }

    public Path(Long idPath, Integer number, String name) {
        this.idPath = idPath;
        this.number = number;
        this.name = name;
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
}
