package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.NationalProgramSkill;
import iut.unilim.fr.back.service.NationalProgramSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/national-program-skills")
@CrossOrigin(origins = "*")
public class NationalProgramSkillController {
    @Autowired
    private NationalProgramSkillService nationalProgramSkillService;

    @GetMapping
    public List<NationalProgramSkill> getAllNationalProgramSkills() {
        return nationalProgramSkillService.getAllNationalProgramSkills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NationalProgramSkill> getNationalProgramSkillById(@PathVariable Long id) {
        return nationalProgramSkillService.getNationalProgramSkillById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource-sheet/{resourceSheetId}")
    public List<NationalProgramSkill> getNationalProgramSkillsByResourceSheetId(@PathVariable Long resourceSheetId) {
        return nationalProgramSkillService.getNationalProgramSkillsByResourceSheetId(resourceSheetId);
    }

    @PostMapping
    public NationalProgramSkill createNationalProgramSkill(@RequestBody NationalProgramSkill nationalProgramSkill) {
        return nationalProgramSkillService.createNationalProgramSkill(nationalProgramSkill);
    }

    @PutMapping("/{id}")
    public NationalProgramSkill updateNationalProgramSkill(@PathVariable Long id, @RequestBody NationalProgramSkill nationalProgramSkill) {
        return nationalProgramSkillService.updateNationalProgramSkill(id, nationalProgramSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNationalProgramSkill(@PathVariable Long id) {
        nationalProgramSkillService.deleteNationalProgramSkill(id);
        return ResponseEntity.noContent().build();
    }
}

