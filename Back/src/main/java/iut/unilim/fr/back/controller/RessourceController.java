package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ressources")
@CrossOrigin(origins = "*")
public class RessourceController {
    @Autowired
    private RessourceService ressourceService;

    @GetMapping
    public List<Ressource> getAllRessources() {
        return ressourceService.getAllRessources();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ressource> getRessourceById(@PathVariable Long id) {
        return ressourceService.getRessourceById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ressource createRessource(@RequestBody Ressource ressource) {
        return ressourceService.createRessource(ressource);
    }

    @PutMapping("/{id}")
    public Ressource updateRessource(@PathVariable Long id, @RequestBody Ressource ressource) {
        return ressourceService.updateRessource(id, ressource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRessource(@PathVariable Long id) {
        ressourceService.deleteRessource(id);
        return ResponseEntity.noContent().build();
    }
}

