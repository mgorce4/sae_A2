package iut.unilim.fr.back.dto;

import java.util.List;

/**
 * DTO for updating a ResourceSheet
 * Contains only the editable fields that can be modified by teachers
 */
public class ResourceSheetUpdateDTO {

    private String objective;
    private List<SkillUpdateDTO> skills;
    private List<String> keywords;
    private List<String> modalities;
    private List<Long> linkedSaeIds;
    private HoursUpdateDTO teacherHours;
    private HoursUpdateDTO teacherHoursAlternance;
    private PedagogicalContentUpdateDTO pedagogicalContent;
    private ResourceTrackingUpdateDTO tracking;

    public ResourceSheetUpdateDTO() {
    }

    // Getters and Setters
    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public List<SkillUpdateDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillUpdateDTO> skills) {
        this.skills = skills;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getModalities() {
        return modalities;
    }

    public void setModalities(List<String> modalities) {
        this.modalities = modalities;
    }

    public List<Long> getLinkedSaeIds() {
        return linkedSaeIds;
    }

    public void setLinkedSaeIds(List<Long> linkedSaeIds) {
        this.linkedSaeIds = linkedSaeIds;
    }

    public HoursUpdateDTO getTeacherHours() {
        return teacherHours;
    }

    public void setTeacherHours(HoursUpdateDTO teacherHours) {
        this.teacherHours = teacherHours;
    }

    public HoursUpdateDTO getTeacherHoursAlternance() {
        return teacherHoursAlternance;
    }

    public void setTeacherHoursAlternance(HoursUpdateDTO teacherHoursAlternance) {
        this.teacherHoursAlternance = teacherHoursAlternance;
    }

    public PedagogicalContentUpdateDTO getPedagogicalContent() {
        return pedagogicalContent;
    }

    public void setPedagogicalContent(PedagogicalContentUpdateDTO pedagogicalContent) {
        this.pedagogicalContent = pedagogicalContent;
    }

    public ResourceTrackingUpdateDTO getTracking() {
        return tracking;
    }

    public void setTracking(ResourceTrackingUpdateDTO tracking) {
        this.tracking = tracking;
    }

    // Inner DTOs
    public static class SkillUpdateDTO {
        private Long id;
        private String label;
        private String description;

        public SkillUpdateDTO() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class HoursUpdateDTO {
        private Double cm;
        private Double td;
        private Double tp;

        public HoursUpdateDTO() {
        }

        public Double getCm() {
            return cm;
        }

        public void setCm(Double cm) {
            this.cm = cm;
        }

        public Double getTd() {
            return td;
        }

        public void setTd(Double td) {
            this.td = td;
        }

        public Double getTp() {
            return tp;
        }

        public void setTp(Double tp) {
            this.tp = tp;
        }
    }

    public static class PedagogicalContentUpdateDTO {
        private List<ContentItemUpdateDTO> cm;
        private List<ContentItemUpdateDTO> td;
        private List<ContentItemUpdateDTO> tp;
        private List<ContentItemUpdateDTO> ds;

        public PedagogicalContentUpdateDTO() {
        }

        public List<ContentItemUpdateDTO> getCm() {
            return cm;
        }

        public void setCm(List<ContentItemUpdateDTO> cm) {
            this.cm = cm;
        }

        public List<ContentItemUpdateDTO> getTd() {
            return td;
        }

        public void setTd(List<ContentItemUpdateDTO> td) {
            this.td = td;
        }

        public List<ContentItemUpdateDTO> getTp() {
            return tp;
        }

        public void setTp(List<ContentItemUpdateDTO> tp) {
            this.tp = tp;
        }

        public List<ContentItemUpdateDTO> getDs() {
            return ds;
        }

        public void setDs(List<ContentItemUpdateDTO> ds) {
            this.ds = ds;
        }

        public static class ContentItemUpdateDTO {
            private Integer order;
            private String content;

            public ContentItemUpdateDTO() {
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

    public static class ResourceTrackingUpdateDTO {
        private String pedagogicalFeedback;
        private String studentFeedback;
        private String improvementSuggestions;

        public ResourceTrackingUpdateDTO() {
        }

        public String getPedagogicalFeedback() {
            return pedagogicalFeedback;
        }

        public void setPedagogicalFeedback(String pedagogicalFeedback) {
            this.pedagogicalFeedback = pedagogicalFeedback;
        }

        public String getStudentFeedback() {
            return studentFeedback;
        }

        public void setStudentFeedback(String studentFeedback) {
            this.studentFeedback = studentFeedback;
        }

        public String getImprovementSuggestions() {
            return improvementSuggestions;
        }

        public void setImprovementSuggestions(String improvementSuggestions) {
            this.improvementSuggestions = improvementSuggestions;
        }
    }
}

