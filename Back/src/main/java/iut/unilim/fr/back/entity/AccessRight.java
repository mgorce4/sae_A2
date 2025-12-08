package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ACCESSRIGHT")
@IdClass(AccessRight.AccessRightId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AccessRight {
    @Id
    @Column(name = "accessright", nullable = false)
    private Integer accessRight;

    @Id
    @Column(name = "id_User", nullable = false)
    private Long idUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_User", insertable = false, updatable = false)
    private UserSyncadia user;

    public AccessRight() {
    }

    public AccessRight(Integer accessRight, Long idUser, UserSyncadia user) {
        this.accessRight = accessRight;
        this.idUser = idUser;
        this.user = user;
    }

    public Integer getAccessRight() {
        return accessRight;
    }

    public void setAccessRight(Integer accessRight) {
        this.accessRight = accessRight;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public UserSyncadia getUser() {
        return user;
    }

    public void setUser(UserSyncadia user) {
        this.user = user;
    }

    public static class AccessRightId implements Serializable {
        private Integer accessRight;
        private Long idUser;

        public AccessRightId() {
        }

        public AccessRightId(Integer accessRight, Long idUser) {
            this.accessRight = accessRight;
            this.idUser = idUser;
        }

        public Integer getAccessRight() {
            return accessRight;
        }

        public void setAccessRight(Integer accessRight) {
            this.accessRight = accessRight;
        }

        public Long getIdUser() {
            return idUser;
        }

        public void setIdUser(Long idUser) {
            this.idUser = idUser;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccessRightId that = (AccessRightId) o;
            return Objects.equals(accessRight, that.accessRight) && Objects.equals(idUser, that.idUser);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accessRight, idUser);
        }
    }
}
