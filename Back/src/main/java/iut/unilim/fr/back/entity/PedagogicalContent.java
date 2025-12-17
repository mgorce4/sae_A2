package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PEDAGOGICAL_CONTENT")
public class PedagogicalContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_PedagogicalContent")
    private Long idPedagogicalContent;

    @Column(columnDefinition = "TEXT")
    private String cm;

    @Column(columnDefinition = "TEXT")
    private String td;

    @Column(columnDefinition = "TEXT")
    private String tp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public PedagogicalContent() {
    }

    public PedagogicalContent(Long idPedagogicalContent, String cm, String td, String tp, RessourceSheet resourceSheet) {
        this.idPedagogicalContent = idPedagogicalContent;
        this.cm = cm;
        this.td = td;
        this.tp = tp;
        this.resourceSheet = resourceSheet;
    }

    public Long getIdPedagogicalContent() {
        return idPedagogicalContent;
    }

    public void setIdPedagogicalContent(Long idPedagogicalContent) {
        this.idPedagogicalContent = idPedagogicalContent;
    }

    public String getCm() {
        return cm;
    }

    public void setCm(String cm) {
        this.cm = cm;
    }

    public String getTd() {
        return td;
    }

    public void setTd(String td) {
        this.td = td;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }
}
