package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RessourceSheetService {
    @Autowired
    private RessourceSheetRepository ressourceSheetRepository;

    public List<RessourceSheet> getAllRessourceSheets() {
        return ressourceSheetRepository.findAll();
    }

    public Optional<RessourceSheet> getRessourceSheetById(Long id) {
        return ressourceSheetRepository.findById(id);
    }

    public List<RessourceSheet> getRessourceSheetsByUserId(Long userId) {
        return ressourceSheetRepository.findByUser_IdUser(userId);
    }

    public List<RessourceSheet> getRessourceSheetsByRessourceId(Long ressourceId) {
        return ressourceSheetRepository.findByRessource_IdRessource(ressourceId);
    }

    public RessourceSheet createRessourceSheet(RessourceSheet ressourceSheet) {
        return ressourceSheetRepository.save(ressourceSheet);
    }

    public RessourceSheet updateRessourceSheet(Long id, RessourceSheet ressourceSheet) {
        ressourceSheet.setIdRessourceSheet(id);
        return ressourceSheetRepository.save(ressourceSheet);
    }

    public void deleteRessourceSheet(Long id) {
        ressourceSheetRepository.deleteById(id);
    }
}

