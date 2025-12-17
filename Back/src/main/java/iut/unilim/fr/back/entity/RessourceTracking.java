package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RESOURCE_TRACKING")
public class RessourceTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ResourceTracking")
    private Long idResourceTracking;

    @Column(name = "pedagogicalFeedback", columnDefinition = "TEXT")
    private String pedagogicalFeedback;

    @Column(name = "studentFeedback", columnDefinition = "TEXT")
    private String studentFeedback;

    @Column(name = "improvementSuggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ResourceSheet")
    private RessourceSheet resourceSheet;

    public RessourceTracking() {
    }

    public RessourceTracking(Long idResourceTracking, String pedagogicalFeedback, String studentFeedback, String improvementSuggestions, RessourceSheet resourceSheet) {
        this.idResourceTracking = idResourceTracking;
        this.pedagogicalFeedback = pedagogicalFeedback;
        this.studentFeedback = studentFeedback;
        this.improvementSuggestions = improvementSuggestions;
        this.resourceSheet = resourceSheet;
    }

    public Long getIdResourceTracking() {
        return idResourceTracking;
    }

    public void setIdResourceTracking(Long idResourceTracking) {
        this.idResourceTracking = idResourceTracking;
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

    public RessourceSheet getResourceSheet() {
        return resourceSheet;
    }

    public void setResourceSheet(RessourceSheet resourceSheet) {
        this.resourceSheet = resourceSheet;
    }
}
