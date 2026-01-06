package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.ModalitiesOfImplementation;
import iut.unilim.fr.back.service.ModalitiesOfImplementationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modalities-of-implementation")
@CrossOrigin(origins = "*")
public class ModalitiesOfImplementationController {
    @Autowired
    private ModalitiesOfImplementationService modalitiesOfImplementationService;

    @GetMapping
    public List<ModalitiesOfImplementation> getAllModalitiesOfImplementation() {
        return modalitiesOfImplementationService.getAllModalitiesOfImplementation();
    }

    @GetMapping("/resource-sheet/{idResourceSheet}")
    public List<ModalitiesOfImplementation> getModalitiesOfImplementationByResourceSheetId(@PathVariable Long idResourceSheet) {
        return modalitiesOfImplementationService.getModalitiesOfImplementationByResourceSheetId(idResourceSheet);
    }

    @PostMapping
    public ModalitiesOfImplementation createModalitiesOfImplementation(@RequestBody ModalitiesOfImplementation modalitiesOfImplementation) {
        return modalitiesOfImplementationService.createModalitiesOfImplementation(modalitiesOfImplementation);
    }

    @DeleteMapping("/resource-sheet/{idResourceSheet}")
    public ResponseEntity<Void> deleteByResourceSheetId(@PathVariable Long idResourceSheet) {
        modalitiesOfImplementationService.deleteByResourceSheetId(idResourceSheet);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteModalitiesOfImplementation(@RequestParam String modality, @RequestParam Long resourceSheetId) {
        ModalitiesOfImplementation.ModalityId id = new ModalitiesOfImplementation.ModalityId(modality, resourceSheetId);
        modalitiesOfImplementationService.deleteModalitiesOfImplementation(id);
        return ResponseEntity.noContent().build();
    }
}



