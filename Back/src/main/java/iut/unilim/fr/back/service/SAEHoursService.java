package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.SAEHours;
import iut.unilim.fr.back.repository.SAEHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SAEHoursService {
    @Autowired
    private SAEHoursRepository saeHoursRepository;

    public List<SAEHours> getAllSAEHours() {
        return saeHoursRepository.findAll();
    }

    public Optional<SAEHours> getSAEHoursById(Long id) {
        return saeHoursRepository.findById(id);
    }

    public List<SAEHours> getSAEHoursBySAEId(Long saeId) {
        return saeHoursRepository.findBySae_IdSAE(saeId);
    }

    public SAEHours createSAEHours(SAEHours saeHours) {
        return saeHoursRepository.save(saeHours);
    }

    public SAEHours updateSAEHours(Long id, SAEHours saeHours) {
        saeHours.setIdSAEHours(id);
        return saeHoursRepository.save(saeHours);
    }

    public void deleteSAEHours(Long id) {
        saeHoursRepository.deleteById(id);
    }
}

