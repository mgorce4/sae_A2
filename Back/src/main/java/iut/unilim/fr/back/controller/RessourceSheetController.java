package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.RessourceSheet;
import iut.unilim.fr.back.service.RessourceSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ressource-sheets")
@CrossOrigin(origins = "*")
public class RessourceSheetController {
    @Autowired
    private RessourceSheetService ressourceSheetService;

    @GetMapping
    public List<RessourceSheet> getAllRessourceSheets() {
        return ressourceSheetService.getAllRessourceSheets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceSheet> getRessourceSheetById(@PathVariable Long id) {
        return ressourceSheetService.getRessourceSheetById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<RessourceSheet> getRessourceSheetsByUserId(@PathVariable Long userId) {
        return ressourceSheetService.getRessourceSheetsByUserId(userId);
    }

    @GetMapping("/ressource/{ressourceId}")
    public List<RessourceSheet> getRessourceSheetsByRessourceId(@PathVariable Long ressourceId) {
        return ressourceSheetService.getRessourceSheetsByRessourceId(ressourceId);
    }

    @PostMapping
    public RessourceSheet createRessourceSheet(@RequestBody RessourceSheet ressourceSheet) {
        return ressourceSheetService.createRessourceSheet(ressourceSheet);
    }

    @PutMapping("/{id}")
    public RessourceSheet updateRessourceSheet(@PathVariable Long id, @RequestBody RessourceSheet ressourceSheet) {
        return ressourceSheetService.updateRessourceSheet(id, ressourceSheet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRessourceSheet(@PathVariable Long id) {
        ressourceSheetService.deleteRessourceSheet(id);
        return ResponseEntity.noContent().build();
    }
}

