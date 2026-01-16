package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.FinalDeliveryDates;
import iut.unilim.fr.back.service.FinalDeliveryDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/final-delivery-dates")
@CrossOrigin(origins = "*")
public class FinalDeliveryDatesController {
    @Autowired
    private FinalDeliveryDatesService finalDeliveryDatesService;

    @GetMapping
    public List<FinalDeliveryDates> getAllFinalDeliveryDates() {
        return finalDeliveryDatesService.getAllFinalDeliveryDates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinalDeliveryDates> getFinalDeliveryDatesById(@PathVariable Long id) {
        return finalDeliveryDatesService.getFinalDeliveryDatesById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<FinalDeliveryDates> getFinalDeliveryDatesByInstitutionId(@PathVariable Long institutionId) {
        return finalDeliveryDatesService.getFinalDeliveryDatesByInstitutionId(institutionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FinalDeliveryDates createFinalDeliveryDates(@RequestBody FinalDeliveryDates finalDeliveryDates) {
        return finalDeliveryDatesService.createFinalDeliveryDates(finalDeliveryDates);
    }

    @PostMapping("/save-by-institution")
    public ResponseEntity<FinalDeliveryDates> saveOrUpdateByInstitution(@RequestBody FinalDeliveryDates finalDeliveryDates) {
        try {
            FinalDeliveryDates saved = finalDeliveryDatesService.saveOrUpdateByInstitution(finalDeliveryDates);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public FinalDeliveryDates updateFinalDeliveryDates(@PathVariable Long id, @RequestBody FinalDeliveryDates finalDeliveryDates) {
        return finalDeliveryDatesService.updateFinalDeliveryDates(id, finalDeliveryDates);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinalDeliveryDates(@PathVariable Long id) {
        finalDeliveryDatesService.deleteFinalDeliveryDates(id);
        return ResponseEntity.noContent().build();
    }
}

