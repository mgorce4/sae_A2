package iut.unilim.fr.back.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Table(name = "YEAR_DATES")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class YearDates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_YearDates")
    private Long idYearDates;

    @Column(name = "start_year", nullable = false)
    private LocalDate startYear;

    @Column(name = "end_year", nullable = false)
    private LocalDate endYear;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_institution")
    private Institution institution;

    public YearDates() {
    }

    public YearDates(Long idYearDates, LocalDate startYear, LocalDate endYear, Institution institution) {
        this.idYearDates = idYearDates;
        this.startYear = startYear;
        this.endYear = endYear;
        this.institution = institution;
    }

    public Long getIdYearDates() {
        return idYearDates;
    }

    public void setIdYearDates(Long idYearDates) {
        this.idYearDates = idYearDates;
    }

    public LocalDate getStartYear() {
        return startYear;
    }

    public void setStartYear(LocalDate startYear) {
        this.startYear = startYear;
    }

    public LocalDate getEndYear() {
        return endYear;
    }

    public void setEndYear(LocalDate endYear) {
        this.endYear = endYear;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
}
