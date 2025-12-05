package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RessourceService {
    @Autowired
    private RessourceRepository ressourceRepository;

    public List<Ressource> getAllRessources() {
        return ressourceRepository.findAll();
    }

    public Optional<Ressource> getRessourceById(Long id) {
        return ressourceRepository.findById(id);
    }

    public Ressource createRessource(Ressource ressource) {
        return ressourceRepository.save(ressource);
    }

    public Ressource updateRessource(Long id, Ressource ressource) {
        ressource.setIdRessource(id);
        return ressourceRepository.save(ressource);
    }

    public void deleteRessource(Long id) {
        ressourceRepository.deleteById(id);
    }
}

