package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.RessourceTracking;
import iut.unilim.fr.back.service.RessourceTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resource-trackings")
@CrossOrigin(origins = "*")
public class ResourceTrackingController {
    @Autowired
    private RessourceTrackingService resourceTrackingService;

    @GetMapping
    public List<RessourceTracking> getAllResourceTrackings() {
        return resourceTrackingService.getAllRessourceTrackings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceTracking> getResourceTrackingById(@PathVariable Long id) {
        return resourceTrackingService.getRessourceTrackingById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource-sheet/{resourceSheetId}")
    public List<RessourceTracking> getResourceTrackingsByResourceSheetId(@PathVariable Long resourceSheetId) {
        return resourceTrackingService.getRessourceTrackingsByResourceSheetId(resourceSheetId);
    }

        @PostMapping
    public RessourceTracking createResourceTracking(@RequestBody RessourceTracking resourceTracking) {
        return resourceTrackingService.createRessourceTracking(resourceTracking);
    }

    @PutMapping("/{id}")
    public RessourceTracking updateResourceTracking(@PathVariable Long id, @RequestBody RessourceTracking resourceTracking) {
        return resourceTrackingService.updateRessourceTracking(id, resourceTracking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceTracking(@PathVariable Long id) {
        resourceTrackingService.deleteRessourceTracking(id);
        return ResponseEntity.noContent().build();
    }
}

