package iut.unilim.fr.back.dto;

import java.util.ArrayList;
import java.util.List;


public class PedagogicalContentDTO {
    private List<ContentItemDTO> cm;
    private List<ContentItemDTO> td;
    private List<ContentItemDTO> tp;
    private List<ContentItemDTO> ds;

    public PedagogicalContentDTO() {
        this.cm = new ArrayList<>();
        this.td = new ArrayList<>();
        this.tp = new ArrayList<>();
        this.ds = new ArrayList<>();
    }

    public List<ContentItemDTO> getCm() {
        return cm;
    }

    public void setCm(List<ContentItemDTO> cm) {
        this.cm = cm;
    }

    public List<ContentItemDTO> getTd() {
        return td;
    }

    public void setTd(List<ContentItemDTO> td) {
        this.td = td;
    }

    public List<ContentItemDTO> getTp() {
        return tp;
    }

    public void setTp(List<ContentItemDTO> tp) {
        this.tp = tp;
    }

    public List<ContentItemDTO> getDs() {
        return ds;
    }

    public void setDs(List<ContentItemDTO> ds) {
        this.ds = ds;
    }


    /**
     * Inner class for a pedagogical content item
     */
    public static class ContentItemDTO {
        private Integer order;  // Order number (1, 2, 3...)
        private String content; // Item content

        public ContentItemDTO() {
        }

        public ContentItemDTO(Integer order, String content) {
            this.order = order;
            this.content = content;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

