package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.service.RessourceSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resource-sheets")
@CrossOrigin(origins = "*")
public class ResourceSheetController {
    @Autowired
    private RessourceSheetService resourceSheetService;

    @GetMapping
    public List<RessourceSheet> getAllResourceSheets() {
        return resourceSheetService.getAllRessourceSheets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceSheet> getResourceSheetById(@PathVariable Long id) {
        return resourceSheetService.getRessourceSheetById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<RessourceSheet> getResourceSheetByIdWithDetails(@PathVariable Long id) {
        return resourceSheetService.getRessourceSheetByIdWithDetails(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    public List<RessourceSheet> getResourceSheetsByResourceId(@PathVariable Long resourceId) {
        return resourceSheetService.getRessourceSheetsByResourceId(resourceId);
    }

    @PostMapping
    public RessourceSheet createResourceSheet(@RequestBody RessourceSheet resourceSheet) {
        return resourceSheetService.createRessourceSheet(resourceSheet);
    }

    @PutMapping("/{id}")
    public RessourceSheet updateResourceSheet(@PathVariable Long id, @RequestBody RessourceSheet resourceSheet) {
        return resourceSheetService.updateRessourceSheet(id, resourceSheet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceSheet(@PathVariable Long id) {
        resourceSheetService.deleteRessourceSheet(id);
        return ResponseEntity.noContent().build();
    }
}

