package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TASK")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Task")
    private Long idTask;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "firstDelivery")
    private LocalDate firstDelivery;

    @Column(name = "secondDelivery")
    private LocalDate secondDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_User")
    private UserSyncadia user;

    public Task() {
    }

    public Task(Long idTask, String name, String description, LocalDate firstDelivery, LocalDate secondDelivery, UserSyncadia user) {
        this.idTask = idTask;
        this.name = name;
        this.description = description;
        this.firstDelivery = firstDelivery;
        this.secondDelivery = secondDelivery;
        this.user = user;
    }

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getFirstDelivery() {
        return firstDelivery;
    }

    public void setFirstDelivery(LocalDate firstDelivery) {
        this.firstDelivery = firstDelivery;
    }

    public LocalDate getSecondDelivery() {
        return secondDelivery;
    }

    public void setSecondDelivery(LocalDate secondDelivery) {
        this.secondDelivery = secondDelivery;
    }

    public UserSyncadia getUser() {
        return user;
    }

    public void setUser(UserSyncadia user) {
        this.user = user;
    }
}
