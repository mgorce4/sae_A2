package iut.unilim.fr.back.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RESSOURCE_TRACKING")
public class RessourceTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_RessourceTracking")
    private Long idRessourceTracking;

    @Column(name = "pedagogicalFeedback", columnDefinition = "TEXT")
    private String pedagogicalFeedback;

    @Column(name = "studentFeedback", columnDefinition = "TEXT")
    private String studentFeedback;

    @Column(name = "improvementSuggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;

    public RessourceTracking() {
    }

    public RessourceTracking(Long idRessourceTracking, String pedagogicalFeedback, String studentFeedback, String improvementSuggestions) {
        this.idRessourceTracking = idRessourceTracking;
        this.pedagogicalFeedback = pedagogicalFeedback;
        this.studentFeedback = studentFeedback;
        this.improvementSuggestions = improvementSuggestions;
    }

    public Long getIdRessourceTracking() {
        return idRessourceTracking;
    }

    public void setIdRessourceTracking(Long idRessourceTracking) {
        this.idRessourceTracking = idRessourceTracking;
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
