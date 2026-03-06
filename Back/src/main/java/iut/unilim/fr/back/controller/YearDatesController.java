package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.YearDates;
import iut.unilim.fr.back.service.InstitutionService;
import iut.unilim.fr.back.service.YearDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/year-dates")
@CrossOrigin(origins = "*")
public class YearDatesController {

    @Autowired
    private YearDatesService yearDatesService;

    @Autowired
    private InstitutionService institutionService;

    @GetMapping
    public List<YearDates> getAll() {
        return yearDatesService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<YearDates> getById(@PathVariable Long id) {
        return yearDatesService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/institution/{institutionId}")
    public List<YearDates> getByInstitution(@PathVariable Long institutionId) {
        return yearDatesService.getByInstitutionId(institutionId);
    }

    @GetMapping("/institution/{institutionId}/latest")
    public ResponseEntity<YearDates> getLatestByInstitution(@PathVariable Long institutionId) {
        return yearDatesService.getLatestByInstitutionId(institutionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<YearDates> create(@RequestBody YearDates yearDates) {
        if (yearDates.getInstitution() != null && yearDates.getInstitution().getIdInstitution() != null) {
            Institution institution = institutionService
                    .getInstitutionById(yearDates.getInstitution().getIdInstitution())
                    .orElseThrow(() -> new RuntimeException("Institution not found"));
            yearDates.setInstitution(institution);
        }
        return ResponseEntity.ok(yearDatesService.create(yearDates));
    }

    @PutMapping("/{id}")
    public ResponseEntity<YearDates> update(@PathVariable Long id, @RequestBody YearDates yearDates) {
        if (yearDates.getInstitution() != null && yearDates.getInstitution().getIdInstitution() != null) {
            Institution institution = institutionService
                    .getInstitutionById(yearDates.getInstitution().getIdInstitution())
                    .orElseThrow(() -> new RuntimeException("Institution not found"));
            yearDates.setInstitution(institution);
        }
        return ResponseEntity.ok(yearDatesService.update(id, yearDates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        yearDatesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
