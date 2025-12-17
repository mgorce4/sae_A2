package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TEACHERS_FOR_RESOURCE")
@IdClass(TeachersForResource.TeachersForResourceId.class)
public class TeachersForResource {
    @Id
    @Column(name = "id_User")
    private Long idUser;

    @Id
    @Column(name = "id_Resource")
    private Long idResource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_User", insertable = false, updatable = false)
    private UserSyncadia user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Resource", insertable = false, updatable = false)
    private Ressource resource;

    public TeachersForResource() {
    }

    public TeachersForResource(Long idUser, Long idResource) {
        this.idUser = idUser;
        this.idResource = idResource;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getIdResource() {
        return idResource;
    }

    public void setIdResource(Long idResource) {
        this.idResource = idResource;
    }

    public UserSyncadia getUser() {
        return user;
    }

    public void setUser(UserSyncadia user) {
        this.user = user;
    }

    public Ressource getResource() {
        return resource;
    }

    public void setResource(Ressource resource) {
        this.resource = resource;
    }

    public static class TeachersForResourceId implements Serializable {
        private Long idUser;
        private Long idResource;

        public TeachersForResourceId() {
        }

        public TeachersForResourceId(Long idUser, Long idResource) {
            this.idUser = idUser;
            this.idResource = idResource;
        }

        public Long getIdUser() {
            return idUser;
        }

        public void setIdUser(Long idUser) {
            this.idUser = idUser;
        }

        public Long getIdResource() {
            return idResource;
        }

        public void setIdResource(Long idResource) {
            this.idResource = idResource;
        }
    }
}

