package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column
    private LocalDate delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_User")
    @JsonIgnore
    private UserSyncadia user;

    public Task() {
    }

    public Task(Long idTask, String name, String description, LocalDate delivery, UserSyncadia user) {
        this.idTask = idTask;
        this.name = name;
        this.description = description;
        this.delivery = delivery;
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

    public LocalDate getDelivery() {
        return delivery;
    }

    public void setDelivery(LocalDate delivery) {
        this.delivery = delivery;
    }

    public UserSyncadia getUser() {
        return user;
    }

    public void setUser(UserSyncadia user) {
        this.user = user;
    }
}
