package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "INSTITUTION")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_institution")
    private Long idInstitution;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    public Institution() {
    }

    public Institution(Long idInstitution, String name, String location) {
        this.idInstitution = idInstitution;
        this.name = name;
        this.location = location;
    }

    public Long getIdInstitution() {
        return idInstitution;
    }

    public void setIdInstitution(Long idInstitution) {
        this.idInstitution = idInstitution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

