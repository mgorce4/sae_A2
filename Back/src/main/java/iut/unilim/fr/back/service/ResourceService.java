package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.repository.ResourceRepository;
import iut.unilim.fr.back.repository.TermsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private PathRepository pathRepository;

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public List<Resource> getResourcesByPathId(Long pathId) {
        return resourceRepository.findByPathId(pathId);
    }

    public Resource createResource(Resource resource) {
        // Gérer Terms
        if (resource.getTerms() != null && resource.getTerms().getCode() != null) {
            Optional<Terms> existingTerms = termsRepository.findFirstByCode(resource.getTerms().getCode());
            if (existingTerms.isPresent()) {
                resource.setTerms(existingTerms.get());
            } else {
                Terms newTerms = new Terms();
                newTerms.setCode(resource.getTerms().getCode());
                resource.setTerms(termsRepository.save(newTerms));
            }
        }

        // Gérer Path
        if (resource.getPath() != null && resource.getPath().getIdPath() != null) {
            Optional<Path> existingPath = pathRepository.findById(resource.getPath().getIdPath());
            existingPath.ifPresent(resource::setPath);
        }

        return resourceRepository.save(resource);
    }

    public Resource updateResource(Long id, Resource resource) {
        resource.setIdResource(id);

        // Gérer Terms
        if (resource.getTerms() != null && resource.getTerms().getCode() != null) {
            Optional<Terms> existingTerms = termsRepository.findFirstByCode(resource.getTerms().getCode());
            if (existingTerms.isPresent()) {
                resource.setTerms(existingTerms.get());
            } else {
                Terms newTerms = new Terms();
                newTerms.setCode(resource.getTerms().getCode());
                resource.setTerms(termsRepository.save(newTerms));
            }
        }

        // Gérer Path
        if (resource.getPath() != null && resource.getPath().getIdPath() != null) {
            Optional<Path> existingPath = pathRepository.findById(resource.getPath().getIdPath());
            existingPath.ifPresent(resource::setPath);
        }

        return resourceRepository.save(resource);
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }
}

