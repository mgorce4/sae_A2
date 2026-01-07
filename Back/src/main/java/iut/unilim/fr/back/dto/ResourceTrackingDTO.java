package iut.unilim.fr.back.dto;

public class ResourceTrackingDTO {
    private String pedagogicalFeedback;
    private String studentFeedback;
    private String improvementSuggestions;

    public ResourceTrackingDTO() {
    }

    public ResourceTrackingDTO(String pedagogicalFeedback, String studentFeedback, String improvementSuggestions) {
        this.pedagogicalFeedback = pedagogicalFeedback;
        this.studentFeedback = studentFeedback;
        this.improvementSuggestions = improvementSuggestions;
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

