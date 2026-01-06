package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@Entity
@Table(name = "MODALITIES_OF_IMPLEMENTATION")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@IdClass(ModalitiesOfImplementation.ModalityId.class)
public class ModalitiesOfImplementation {

    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String modality;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet", nullable = false)
    private RessourceSheet resourceSheet;

    public ModalitiesOfImplementation() {
    }

    public ModalitiesOfImplementation(String modality, RessourceSheet resourceSheet) {
        this.modality = modality;
        this.resourceSheet = resourceSheet;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }

    // Composite key class
    public static class ModalityId implements Serializable {
        private String modality;
        private Long resourceSheet;

        public ModalityId() {
        }

        public ModalityId(String modality, Long resourceSheet) {
            this.modality = modality;
            this.resourceSheet = resourceSheet;
        }

        public String getModality() {
            return modality;
        }

        public void setModality(String modality) {
            this.modality = modality;
        }

        public Long getResourceSheet() {
            return resourceSheet;
        }

        public void setResourceSheet(Long resourceSheet) {
            this.resourceSheet = resourceSheet;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModalityId that = (ModalityId) o;
            return modality.equals(that.modality) && resourceSheet.equals(that.resourceSheet);
        }

        @Override
        public int hashCode() {
            return modality.hashCode() + resourceSheet.hashCode();
        }
    }
}

