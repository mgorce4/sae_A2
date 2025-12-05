package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.RessourceTracking;
import iut.unilim.fr.back.service.RessourceTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ressource-trackings")
@CrossOrigin(origins = "*")
public class RessourceTrackingController {
    @Autowired
    private RessourceTrackingService ressourceTrackingService;

    @GetMapping
    public List<RessourceTracking> getAllRessourceTrackings() {
        return ressourceTrackingService.getAllRessourceTrackings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceTracking> getRessourceTrackingById(@PathVariable Long id) {
        return ressourceTrackingService.getRessourceTrackingById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RessourceTracking createRessourceTracking(@RequestBody RessourceTracking ressourceTracking) {
        return ressourceTrackingService.createRessourceTracking(ressourceTracking);
    }

    @PutMapping("/{id}")
    public RessourceTracking updateRessourceTracking(@PathVariable Long id, @RequestBody RessourceTracking ressourceTracking) {
        return ressourceTrackingService.updateRessourceTracking(id, ressourceTracking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRessourceTracking(@PathVariable Long id) {
        ressourceTrackingService.deleteRessourceTracking(id);
        return ResponseEntity.noContent().build();
    }
}

