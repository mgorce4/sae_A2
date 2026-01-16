package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.FinalDeliveryDates;
import iut.unilim.fr.back.repository.FinalDeliveryDatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinalDeliveryDatesService {
    @Autowired
    private FinalDeliveryDatesRepository finalDeliveryDatesRepository;

    public List<FinalDeliveryDates> getAllFinalDeliveryDates() {
        return finalDeliveryDatesRepository.findAll();
    }

    public Optional<FinalDeliveryDates> getFinalDeliveryDatesById(Long id) {
        return finalDeliveryDatesRepository.findById(id);
    }

    public Optional<FinalDeliveryDates> getFinalDeliveryDatesByInstitutionId(Long institutionId) {
        return finalDeliveryDatesRepository.findByInstitution_IdInstitution(institutionId);
    }

    public FinalDeliveryDates createFinalDeliveryDates(FinalDeliveryDates finalDeliveryDates) {
        return finalDeliveryDatesRepository.save(finalDeliveryDates);
    }

    public FinalDeliveryDates updateFinalDeliveryDates(Long id, FinalDeliveryDates finalDeliveryDates) {
        finalDeliveryDates.setIdFinalDelivery(id);
        return finalDeliveryDatesRepository.save(finalDeliveryDates);
    }

    /**
     * Save or update delivery dates for an institution
     * If dates already exist for this institution, update them
     * Otherwise, create new dates
     */
    public FinalDeliveryDates saveOrUpdateByInstitution(FinalDeliveryDates finalDeliveryDates) {
        if (finalDeliveryDates.getInstitution() == null || finalDeliveryDates.getInstitution().getIdInstitution() == null) {
            throw new IllegalArgumentException("Institution cannot be null");
        }

        // Check if dates already exist for this institution
        Optional<FinalDeliveryDates> existing = finalDeliveryDatesRepository
            .findByInstitution_IdInstitution(finalDeliveryDates.getInstitution().getIdInstitution());

        if (existing.isPresent()) {
            // Update existing dates
            FinalDeliveryDates existingDates = existing.get();
            existingDates.setFirstDelivery(finalDeliveryDates.getFirstDelivery());
            existingDates.setSecondDelivery(finalDeliveryDates.getSecondDelivery());
            return finalDeliveryDatesRepository.save(existingDates);
        } else {
            // Create new dates
            return finalDeliveryDatesRepository.save(finalDeliveryDates);
        }
    }

    public void deleteFinalDeliveryDates(Long id) {
        finalDeliveryDatesRepository.deleteById(id);
    }
}

