package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.NationalProgramObjective;
import iut.unilim.fr.back.service.NationalProgramObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/national-program-objectives")
@CrossOrigin(origins = "*")
public class NationalProgramObjectiveController {
    @Autowired
    private NationalProgramObjectiveService nationalProgramObjectiveService;

    @GetMapping
    public List<NationalProgramObjective> getAllNationalProgramObjectives() {
        return nationalProgramObjectiveService.getAllNationalProgramObjectives();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NationalProgramObjective> getNationalProgramObjectiveById(@PathVariable Long id) {
        return nationalProgramObjectiveService.getNationalProgramObjectiveById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ressource-sheet/{idRessourceSheet}")
    public List<NationalProgramObjective> getNationalProgramObjectivesByRessourceSheetId(@PathVariable Long idRessourceSheet) {
        return nationalProgramObjectiveService.getNationalProgramObjectivesByRessourceSheetId(idRessourceSheet);
    }

    @PostMapping
    public NationalProgramObjective createNationalProgramObjective(@RequestBody NationalProgramObjective nationalProgramObjective) {
        return nationalProgramObjectiveService.createNationalProgramObjective(nationalProgramObjective);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NationalProgramObjective> updateNationalProgramObjective(@PathVariable Long id, @RequestBody NationalProgramObjective nationalProgramObjectiveDetails) {
        try {
            NationalProgramObjective updatedNationalProgramObjective = nationalProgramObjectiveService.updateNationalProgramObjective(id, nationalProgramObjectiveDetails);
            return ResponseEntity.ok(updatedNationalProgramObjective);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNationalProgramObjective(@PathVariable Long id) {
        nationalProgramObjectiveService.deleteNationalProgramObjective(id);
        return ResponseEntity.noContent().build();
    }
}

