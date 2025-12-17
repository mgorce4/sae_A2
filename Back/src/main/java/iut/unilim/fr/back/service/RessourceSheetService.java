package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.repository.RessourceSheetRepository;
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
    private RessourceSheetRepository ressourceSheetRepository;

    public List<RessourceSheet> getAllRessourceSheets() {
        return ressourceSheetRepository.findAll();
    }

    public Optional<RessourceSheet> getRessourceSheetById(Long id) {
        return ressourceSheetRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RessourceSheet> getRessourceSheetByIdWithDetails(Long id) {
        logger.info("Loading RessourceSheet with ID: {}", id);
        Optional<RessourceSheet> ressourceSheetOpt = ressourceSheetRepository.findById(id);

        if (ressourceSheetOpt.isPresent()) {
            RessourceSheet rs = ressourceSheetOpt.get();
            logger.info("RessourceSheet found: {}", rs.getName());

            // Force loading of lazy relationships
            if (rs.getUser() != null) {
                String username = rs.getUser().getUsername();
                logger.info("User loaded: {}", username);
                if (rs.getUser().getInstitution() != null) {
                    String institutionName = rs.getUser().getInstitution().getName();
                    logger.info("Institution loaded: {}", institutionName);
                } else {
                    logger.warn("No institution found for user");
                }
            } else {
                logger.warn("No user found for ressource sheet");
            }

            if (rs.getRessource() != null) {
                String ressourceLabel = rs.getRessource().getLabel();
                logger.info("Ressource loaded: {}", ressourceLabel);
                if (rs.getRessource().getUeCoefficient() != null) {
                    Integer coef = rs.getRessource().getUeCoefficient().getCoefficient();
                    logger.info("UeCoefficient loaded with coefficient: {}", coef);
                    if (rs.getRessource().getUeCoefficient().getUe() != null) {
                        String ueLabel = rs.getRessource().getUeCoefficient().getUe().getLabel();
                        logger.info("UE loaded: {}", ueLabel);
                    } else {
                        logger.warn("No UE found for ueCoefficient");
                    }
                } else {
                    logger.warn("No ueCoefficient found for ressource");
                }
            } else {
                logger.warn("No ressource found for ressource sheet");
            }

            if (rs.getPedagogicalContent() != null) {
                rs.getPedagogicalContent().getCm();
                logger.info("PedagogicalContent loaded");
            }

            if (rs.getRessourceTracking() != null) {
                rs.getRessourceTracking().getPedagogicalFeedback();
                logger.info("RessourceTracking loaded");
            }

            logger.info("All relationships loaded successfully");
        } else {
            logger.warn("RessourceSheet not found with ID: {}", id);
        }

        return ressourceSheetOpt;
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

