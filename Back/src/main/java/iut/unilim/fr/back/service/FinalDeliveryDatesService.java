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

    public FinalDeliveryDates createFinalDeliveryDates(FinalDeliveryDates finalDeliveryDates) {
        return finalDeliveryDatesRepository.save(finalDeliveryDates);
    }

    public FinalDeliveryDates updateFinalDeliveryDates(Long id, FinalDeliveryDates finalDeliveryDates) {
        finalDeliveryDates.setIdFinalDelivery(id);
        return finalDeliveryDatesRepository.save(finalDeliveryDates);
    }

    public void deleteFinalDeliveryDates(Long id) {
        finalDeliveryDatesRepository.deleteById(id);
    }
}

