package iut.unilim.fr.back.dto;

import java.time.LocalDate;

public class YearDatesDTO {
    private Long idYearDates;
    private LocalDate startYear;
    private LocalDate endYear;
    private Long institutionId;
    private String institutionName;

    public YearDatesDTO() {
    }

    public YearDatesDTO(Long idYearDates, LocalDate startYear, LocalDate endYear, Long institutionId, String institutionName) {
        this.idYearDates = idYearDates;
        this.startYear = startYear;
        this.endYear = endYear;
        this.institutionId = institutionId;
        this.institutionName = institutionName;
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

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}

