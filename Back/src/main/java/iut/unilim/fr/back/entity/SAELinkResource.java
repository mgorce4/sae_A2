package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SAE_LINK_RESOURCE")
@IdClass(SAELinkResource.SAELinkResourceId.class)
public class SAELinkResource {
    @Id
    @Column(name = "id_SAE")
    private Long idSAE;

    @Id
    @Column(name = "id_resource")
    private Long idResource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_SAE", insertable = false, updatable = false)
    private SAE sae;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resource", insertable = false, updatable = false)
    private Ressource resource;

    public SAELinkResource() {
    }

    public SAELinkResource(Long idSAE, Long idResource) {
        this.idSAE = idSAE;
        this.idResource = idResource;
    }

    public Long getIdSAE() {
        return idSAE;
    }

    public void setIdSAE(Long idSAE) {
        this.idSAE = idSAE;
    }

    public Long getIdResource() {
        return idResource;
    }

    public void setIdResource(Long idResource) {
        this.idResource = idResource;
    }

    public SAE getSae() {
        return sae;
    }

    public void setSae(SAE sae) {
        this.sae = sae;
    }

    public Ressource getResource() {
        return resource;
    }

    public void setResource(Ressource resource) {
        this.resource = resource;
    }

    public static class SAELinkResourceId implements Serializable {
        private Long idSAE;
        private Long idResource;

        public SAELinkResourceId() {
        }

        public SAELinkResourceId(Long idSAE, Long idResource) {
            this.idSAE = idSAE;
            this.idResource = idResource;
        }

        public Long getIdSAE() {
            return idSAE;
        }

        public void setIdSAE(Long idSAE) {
            this.idSAE = idSAE;
        }

        public Long getIdResource() {
            return idResource;
        }

        public void setIdResource(Long idResource) {
            this.idResource = idResource;
        }
    }
}
