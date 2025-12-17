package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.SAEHours;
import iut.unilim.fr.back.service.SAEHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sae-hours")
@CrossOrigin(origins = "*")
public class SAEHoursController {
    @Autowired
    private SAEHoursService saeHoursService;

    @GetMapping
    public List<SAEHours> getAllSAEHours() {
        return saeHoursService.getAllSAEHours();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SAEHours> getSAEHoursById(@PathVariable Long id) {
        return saeHoursService.getSAEHoursById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sae/{saeId}")
    public List<SAEHours> getSAEHoursBySAEId(@PathVariable Long saeId) {
        return saeHoursService.getSAEHoursBySAEId(saeId);
    }

    @PostMapping
    public SAEHours createSAEHours(@RequestBody SAEHours saeHours) {
        return saeHoursService.createSAEHours(saeHours);
    }

    @PutMapping("/{id}")
    public SAEHours updateSAEHours(@PathVariable Long id, @RequestBody SAEHours saeHours) {
        return saeHoursService.updateSAEHours(id, saeHours);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSAEHours(@PathVariable Long id) {
        saeHoursService.deleteSAEHours(id);
        return ResponseEntity.noContent().build();
    }
}

