package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.ResourceSheet;
import iut.unilim.fr.back.service.ResourceSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resource-sheets")
@CrossOrigin(origins = "*")
public class ResourceSheetController {
    @Autowired
    private ResourceSheetService resourceSheetService;

    @GetMapping
    public List<ResourceSheet> getAllResourceSheets() {
        return resourceSheetService.getAllResourceSheets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceSheet> getResourceSheetById(@PathVariable Long id) {
        return resourceSheetService.getResourceSheetById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ResourceSheet> getResourceSheetByIdWithDetails(@PathVariable Long id) {
        return resourceSheetService.getResourceSheetByIdWithDetails(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    public List<ResourceSheet> getResourceSheetsByResourceId(@PathVariable Long resourceId) {
        return resourceSheetService.getResourceSheetsByResourceId(resourceId);
    }

    @PostMapping
    public ResourceSheet createResourceSheet(@RequestBody ResourceSheet resourceSheet) {
        return resourceSheetService.createResourceSheet(resourceSheet);
    }

    @PutMapping("/{id}")
    public ResourceSheet updateResourceSheet(@PathVariable Long id, @RequestBody ResourceSheet resourceSheet) {
        return resourceSheetService.updateResourceSheet(id, resourceSheet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceSheet(@PathVariable Long id) {
        resourceSheetService.deleteResourceSheet(id);
        return ResponseEntity.noContent().build();
    }
}

