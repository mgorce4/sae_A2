package iut.unilim.fr.back.mapper;

import iut.unilim.fr.back.dto.YearDatesDTO;
import iut.unilim.fr.back.entity.YearDates;
import org.springframework.stereotype.Component;

@Component
public class YearDatesMapper {

    public YearDatesDTO toDTO(YearDates yearDates) {
        if (yearDates == null) {
            return null;
        }
        YearDatesDTO dto = new YearDatesDTO();
        dto.setIdYearDates(yearDates.getIdYearDates());
        dto.setStartYear(yearDates.getStartYear());
        dto.setEndYear(yearDates.getEndYear());
        if (yearDates.getInstitution() != null) {
            dto.setInstitutionId(yearDates.getInstitution().getIdInstitution());
            dto.setInstitutionName(yearDates.getInstitution().getName());
        }
        return dto;
    }

    public YearDates toEntity(YearDatesDTO dto) {
        if (dto == null) {
            return null;
        }
        YearDates yearDates = new YearDates();
        yearDates.setIdYearDates(dto.getIdYearDates());
        yearDates.setStartYear(dto.getStartYear());
        yearDates.setEndYear(dto.getEndYear());
        return yearDates;
    }
}

