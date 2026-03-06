package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.YearDates;
import iut.unilim.fr.back.repository.YearDatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YearDatesService {

    @Autowired
    private YearDatesRepository yearDatesRepository;

    public List<YearDates> getAll() {
        return yearDatesRepository.findAll();
    }

    public Optional<YearDates> getById(Long id) {
        return yearDatesRepository.findById(id);
    }

    public List<YearDates> getByInstitutionId(Long institutionId) {
        return yearDatesRepository.findByInstitution_IdInstitution(institutionId);
    }

    public Optional<YearDates> getLatestByInstitutionId(Long institutionId) {
        return yearDatesRepository.findFirstByInstitution_IdInstitutionOrderByStartYearDesc(institutionId);
    }

    public YearDates create(YearDates yearDates) {
        return yearDatesRepository.save(yearDates);
    }

    public YearDates update(Long id, YearDates yearDates) {
        yearDates.setIdYearDates(id);
        return yearDatesRepository.save(yearDates);
    }

    public void delete(Long id) {
        yearDatesRepository.deleteById(id);
    }
}
