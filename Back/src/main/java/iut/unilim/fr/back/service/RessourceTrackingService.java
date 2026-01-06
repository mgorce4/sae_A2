package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.RessourceTracking;
import iut.unilim.fr.back.repository.RessourceTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RessourceTrackingService {
    @Autowired
    private RessourceTrackingRepository ressourceTrackingRepository;

    public List<RessourceTracking> getAllRessourceTrackings() {
        return ressourceTrackingRepository.findAll();
    }

    public Optional<RessourceTracking> getRessourceTrackingById(Long id) {
        return ressourceTrackingRepository.findById(id);
    }

    public List<RessourceTracking> getRessourceTrackingsByResourceSheetId(Long idResourceSheet) {
        return ressourceTrackingRepository.findByResourceSheet_IdResourceSheet(idResourceSheet);
    }

    public RessourceTracking createRessourceTracking(RessourceTracking resourceTracking) {
        return ressourceTrackingRepository.save(resourceTracking);
    }

    public RessourceTracking updateRessourceTracking(Long id, RessourceTracking resourceTracking) {
        resourceTracking.setIdResourceTracking(id);
        return ressourceTrackingRepository.save(resourceTracking);
    }

    public void deleteRessourceTracking(Long id) {
        ressourceTrackingRepository.deleteById(id);
    }
}

