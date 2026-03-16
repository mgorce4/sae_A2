package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.dto.YearDatesDTO;
import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.entity.YearDates;
import iut.unilim.fr.back.mapper.YearDatesMapper;
import iut.unilim.fr.back.service.InstitutionService;
import iut.unilim.fr.back.service.YearDatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/year-dates")
@CrossOrigin(origins = "*")
public class YearDatesController {

    @Autowired
    private YearDatesService yearDatesService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private YearDatesMapper yearDatesMapper;

    @GetMapping
    public List<YearDates> getAll() {
        return yearDatesService.getAll();
    }

    @GetMapping("/v2")
    public List<YearDatesDTO> getAllDTO() {
        return yearDatesService.getAll().stream()
                .map(yearDatesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<YearDates> getById(@PathVariable Long id) {
        return yearDatesService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/v2/{id}")
    public ResponseEntity<YearDatesDTO> getByIdDTO(@PathVariable Long id) {
        return yearDatesService.getById(id)
                .map(yearDates -> ResponseEntity.ok(yearDatesMapper.toDTO(yearDates)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/institution/{institutionId}")
    public List<YearDates> getByInstitution(@PathVariable Long institutionId) {
        return yearDatesService.getByInstitutionId(institutionId);
    }

    @GetMapping("/v2/institution/{institutionId}")
    public List<YearDatesDTO> getByInstitutionDTO(@PathVariable Long institutionId) {
        return yearDatesService.getByInstitutionId(institutionId).stream()
                .map(yearDatesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/institution/{institutionId}/latest")
    public ResponseEntity<YearDates> getLatestByInstitution(@PathVariable Long institutionId) {
        return yearDatesService.getLatestByInstitutionId(institutionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/v2/institution/{institutionId}/latest")
    public ResponseEntity<YearDatesDTO> getLatestByInstitutionDTO(@PathVariable Long institutionId) {
        return yearDatesService.getLatestByInstitutionId(institutionId)
                .map(yearDates -> ResponseEntity.ok(yearDatesMapper.toDTO(yearDates)))
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

    @PostMapping("/v2")
    public ResponseEntity<YearDatesDTO> createDTO(@RequestBody YearDatesDTO yearDatesDTO) {
        Institution institution = institutionService
                .getInstitutionById(yearDatesDTO.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));

        YearDates yearDates = new YearDates();
        yearDates.setStartYear(yearDatesDTO.getStartYear());
        yearDates.setEndYear(yearDatesDTO.getEndYear());
        yearDates.setInstitution(institution);

        YearDates created = yearDatesService.create(yearDates);
        return ResponseEntity.ok(yearDatesMapper.toDTO(created));
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

    @PutMapping("/v2/{id}")
    public ResponseEntity<YearDatesDTO> updateDTO(@PathVariable Long id, @RequestBody YearDatesDTO yearDatesDTO) {
        Institution institution = institutionService
                .getInstitutionById(yearDatesDTO.getInstitutionId())
                .orElseThrow(() -> new RuntimeException("Institution not found"));

        YearDates yearDates = yearDatesService.getById(id)
                .orElseThrow(() -> new RuntimeException("YearDates not found"));

        yearDates.setStartYear(yearDatesDTO.getStartYear());
        yearDates.setEndYear(yearDatesDTO.getEndYear());
        yearDates.setInstitution(institution);

        YearDates updated = yearDatesService.update(id, yearDates);
        return ResponseEntity.ok(yearDatesMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        yearDatesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


