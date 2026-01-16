package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.ResourceDTO;
import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/mccc/resources")
@CrossOrigin(origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping
    public ResponseEntity<Ressource> createResource(@RequestBody ResourceDTO dto) {
        Ressource resource = resourceService.createResource(dto);
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ressource> updateResource(@PathVariable Long id, @RequestBody ResourceDTO dto) {
        Ressource resource = resourceService.updateResource(id, dto);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}

