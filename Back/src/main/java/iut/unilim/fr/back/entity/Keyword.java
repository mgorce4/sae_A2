package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "KEYWORD")
@IdClass(Keyword.KeywordId.class)
public class Keyword {
    @Id
    @Column(nullable = false)
    private String keyword;

    @Id
    @Column(name = "id_ResourceSheet")
    private Long idResourceSheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet", insertable = false, updatable = false)
    private RessourceSheet resourceSheet;

    public Keyword() {
    }

    public Keyword(String keyword, Long idResourceSheet) {
        this.keyword = keyword;
        this.idResourceSheet = idResourceSheet;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getIdResourceSheet() {
        return idResourceSheet;
    }

    public void setIdResourceSheet(Long idResourceSheet) {
        this.idResourceSheet = idResourceSheet;
    }

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }

    public static class KeywordId implements Serializable {
        private String keyword;
        private Long idResourceSheet;

        public KeywordId() {
        }

        public KeywordId(String keyword, Long idResourceSheet) {
            this.keyword = keyword;
            this.idResourceSheet = idResourceSheet;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public Long getIdResourceSheet() {
            return idResourceSheet;
        }

        public void setIdResourceSheet(Long idResourceSheet) {
            this.idResourceSheet = idResourceSheet;
        }
    }
}

