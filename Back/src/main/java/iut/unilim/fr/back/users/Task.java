package iut.unilim.fr.back.users;

import java.time.LocalDate;

public class Task {
    private String name;
    private String description;
    private LocalDate firstDeliveryDate;
    private LocalDate secoundDeliveryDate;

    public Task(String name, String description, LocalDate firstDeliveryDate, LocalDate secoundDeliveryDate) {
        this.name = name;
        this.description = description;
        this.firstDeliveryDate =  firstDeliveryDate;
        this.secoundDeliveryDate = secoundDeliveryDate;
    }

    //getter and setter

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate firstDeliveryDate() {
        return firstDeliveryDate;
    }

    public void setFirstDeliveryDate(LocalDate firstDeliveryDate) {
        this.firstDeliveryDate = firstDeliveryDate;
    }

    public LocalDate secoundDeliveryDate() {
        return secoundDeliveryDate;
    }

    public void setSecoundDeliveryDate(LocalDate secoundDeliveryDate) {
        this.secoundDeliveryDate = secoundDeliveryDate;
    }
}
