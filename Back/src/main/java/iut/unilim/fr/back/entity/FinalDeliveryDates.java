package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "FINAL_DELIVERY_DATES")
public class FinalDeliveryDates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_FinalDelivery")
    private Long idFinalDelivery;

    @Column(name = "firstDelivery")
    private LocalDate firstDelivery;

    @Column(name = "secondDelivery")
    private LocalDate secondDelivery;

    public FinalDeliveryDates() {
    }

    public FinalDeliveryDates(Long idFinalDelivery, LocalDate firstDelivery, LocalDate secondDelivery) {
        this.idFinalDelivery = idFinalDelivery;
        this.firstDelivery = firstDelivery;
        this.secondDelivery = secondDelivery;
    }

    public Long getIdFinalDelivery() {
        return idFinalDelivery;
    }

    public void setIdFinalDelivery(Long idFinalDelivery) {
        this.idFinalDelivery = idFinalDelivery;
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
}

