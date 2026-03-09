package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.ResourceTracking;
import iut.unilim.fr.back.service.ResourceTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resource-trackings")
@CrossOrigin(origins = "*")
public class ResourceTrackingController {
    @Autowired
    private ResourceTrackingService resourceTrackingService;

    @GetMapping
    public List<ResourceTracking> getAllResourceTrackings() {
        return resourceTrackingService.getAllResourceTrackings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceTracking> getResourceTrackingById(@PathVariable Long id) {
        return resourceTrackingService.getResourceTrackingById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource-sheet/{resourceSheetId}")
    public List<ResourceTracking> getResourceTrackingsByResourceSheetId(@PathVariable Long resourceSheetId) {
        return resourceTrackingService.getResourceTrackingsByResourceSheetId(resourceSheetId);
    }

        @PostMapping
    public ResourceTracking createResourceTracking(@RequestBody ResourceTracking resourceTracking) {
        return resourceTrackingService.createResourceTracking(resourceTracking);
    }

    @PutMapping("/{id}")
    public ResourceTracking updateResourceTracking(@PathVariable Long id, @RequestBody ResourceTracking resourceTracking) {
        return resourceTrackingService.updateResourceTracking(id, resourceTracking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResourceTracking(@PathVariable Long id) {
        resourceTrackingService.deleteResourceTracking(id);
        return ResponseEntity.noContent().build();
    }
}

