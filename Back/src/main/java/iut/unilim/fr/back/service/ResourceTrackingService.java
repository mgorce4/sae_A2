package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.ResourceTracking;
import iut.unilim.fr.back.repository.ResourceTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceTrackingService {
    @Autowired
    private ResourceTrackingRepository resourceTrackingRepository;

    public List<ResourceTracking> getAllResourceTrackings() {
        return resourceTrackingRepository.findAll();
    }

    public Optional<ResourceTracking> getResourceTrackingById(Long id) {
        return resourceTrackingRepository.findById(id);
    }

    public List<ResourceTracking> getResourceTrackingsByResourceSheetId(Long idResourceSheet) {
        return resourceTrackingRepository.findByResourceSheet_IdResourceSheet(idResourceSheet);
    }

    public ResourceTracking createResourceTracking(ResourceTracking resourceTracking) {
        return resourceTrackingRepository.save(resourceTracking);
    }

    public ResourceTracking updateResourceTracking(Long id, ResourceTracking resourceTracking) {
        resourceTracking.setIdResourceTracking(id);
        return resourceTrackingRepository.save(resourceTracking);
    }

    public void deleteResourceTracking(Long id) {
        resourceTrackingRepository.deleteById(id);
    }
}

