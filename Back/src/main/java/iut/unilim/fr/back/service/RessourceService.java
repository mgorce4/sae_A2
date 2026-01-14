package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.TermsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RessourceService {
    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private PathRepository pathRepository;

    public List<Ressource> getAllRessources() {
        return ressourceRepository.findAll();
    }

    public Optional<Ressource> getRessourceById(Long id) {
        return ressourceRepository.findById(id);
    }

    public List<Ressource> getRessourcesByPathId(Long pathId) {
        return ressourceRepository.findByPathId(pathId);
    }

    public Ressource createRessource(Ressource resource) {
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

        return ressourceRepository.save(resource);
    }

    public Ressource updateRessource(Long id, Ressource resource) {
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

        return ressourceRepository.save(resource);
    }

    public void deleteRessource(Long id) {
        ressourceRepository.deleteById(id);
    }
}

