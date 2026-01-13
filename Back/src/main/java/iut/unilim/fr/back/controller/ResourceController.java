package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {
    @Autowired
    private RessourceService resourceService;

    @GetMapping
    public List<Ressource> getAllResources() {
        return resourceService.getAllRessources();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ressource> getResourceById(@PathVariable Long id) {
        return resourceService.getRessourceById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/path/{pathId}")
    public List<Ressource> getResourcesByPathId(@PathVariable Long pathId) {
        return resourceService.getRessourcesByPathId(pathId);
    }

    @PostMapping
    public Ressource createResource(@RequestBody Ressource resource) {
        return resourceService.createRessource(resource);
    }

    @PutMapping("/{id}")
    public Ressource updateResource(@PathVariable Long id, @RequestBody Ressource resource) {
        return resourceService.updateRessource(id, resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteRessource(id);
        return ResponseEntity.noContent().build();
    }
}

