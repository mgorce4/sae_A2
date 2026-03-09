package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.repository.ResourceSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RessourceSheetService {
    private static final Logger logger = LoggerFactory.getLogger(RessourceSheetService.class);

    @Autowired
    private ResourceSheetRepository resourceSheetRepository;

    public List<RessourceSheet> getAllRessourceSheets() {
        return resourceSheetRepository.findAll();
    }

    public Optional<RessourceSheet> getRessourceSheetById(Long id) {
        return resourceSheetRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RessourceSheet> getRessourceSheetByIdWithDetails(Long id) {
        logger.info("Loading RessourceSheet with ID: {}", id);
        Optional<RessourceSheet> ressourceSheetOpt = resourceSheetRepository.findById(id);

        if (ressourceSheetOpt.isPresent()) {
            RessourceSheet rs = ressourceSheetOpt.get();
            logger.info("RessourceSheet found for year: {}", rs.getYear());

            // Force loading of lazy relationships
            if (rs.getResource() != null) {
                String ressourceLabel = rs.getResource().getLabel();
                logger.info("Resource loaded: {}", ressourceLabel);
            } else {
                logger.warn("No resource found for resource sheet");
            }

            logger.info("All relationships loaded successfully");
        } else {
            logger.warn("RessourceSheet not found with ID: {}", id);
        }

        return ressourceSheetOpt;
    }

    public List<RessourceSheet> getRessourceSheetsByResourceId(Long resourceId) {
        return resourceSheetRepository.findByResource_IdResource(resourceId);
    }

    public RessourceSheet createRessourceSheet(RessourceSheet resourceSheet) {
        return resourceSheetRepository.save(resourceSheet);
    }

    public RessourceSheet updateRessourceSheet(Long id, RessourceSheet resourceSheet) {
        resourceSheet.setIdResourceSheet(id);
        return resourceSheetRepository.save(resourceSheet);
    }

    public void deleteRessourceSheet(Long id) {
        resourceSheetRepository.deleteById(id);
    }
}

