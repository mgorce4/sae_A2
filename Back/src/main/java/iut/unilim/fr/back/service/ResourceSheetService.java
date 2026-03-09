package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.ResourceSheet;
import iut.unilim.fr.back.repository.ResourceSheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ResourceSheetService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceSheetService.class);

    @Autowired
    private ResourceSheetRepository resourceSheetRepository;

    public List<ResourceSheet> getAllResourceSheets() {
        return resourceSheetRepository.findAll();
    }

    public Optional<ResourceSheet> getResourceSheetById(Long id) {
        return resourceSheetRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ResourceSheet> getResourceSheetByIdWithDetails(Long id) {
        logger.info("Loading ResourceSheet with ID: {}", id);
        Optional<ResourceSheet> resourceSheetOpt = resourceSheetRepository.findById(id);

        if (resourceSheetOpt.isPresent()) {
            ResourceSheet rs = resourceSheetOpt.get();
            logger.info("ResourceSheet found for year: {}", rs.getYear());

            // Force loading of lazy relationships
            if (rs.getResource() != null) {
                String resourceLabel = rs.getResource().getLabel();
                logger.info("Resource loaded: {}", resourceLabel);
            } else {
                logger.warn("No resource found for resource sheet");
            }

            logger.info("All relationships loaded successfully");
        } else {
            logger.warn("ResourceSheet not found with ID: {}", id);
        }

        return resourceSheetOpt;
    }

    public List<ResourceSheet> getResourceSheetsByResourceId(Long resourceId) {
        return resourceSheetRepository.findByResource_IdResource(resourceId);
    }

    public ResourceSheet createResourceSheet(ResourceSheet resourceSheet) {
        return resourceSheetRepository.save(resourceSheet);
    }

    public ResourceSheet updateResourceSheet(Long id, ResourceSheet resourceSheet) {
        resourceSheet.setIdResourceSheet(id);
        return resourceSheetRepository.save(resourceSheet);
    }

    public void deleteResourceSheet(Long id) {
        resourceSheetRepository.deleteById(id);
    }
}

