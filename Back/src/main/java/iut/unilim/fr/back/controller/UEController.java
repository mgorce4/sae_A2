package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.service.UEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.service.PathService;

@RestController
@RequestMapping("/api/ues")
@CrossOrigin(origins = "*")
public class UEController {
    @Autowired
    private UEService ueService;

    @Autowired
    private PathService pathService;


    @GetMapping
    public List<UE> getAllUEs() {
        return ueService.getAllUEs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UE> getUEById(@PathVariable Long id) {
        return ueService.getUEById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/path/{pathId}")
    public List<UE> getUEsByPathId(@PathVariable Long pathId) {
        return ueService.getUEsByPathId(pathId);
    }


    @PutMapping("/{id}")
    public UE updateUE(@PathVariable Long id, @RequestBody UE ue) {
        return ueService.updateUE(id, ue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUE(@PathVariable Long id) {
        ueService.deleteUE(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/filter")
    public List<UE> getUEsByFilter(
            @RequestParam Long institutionId,
            @RequestParam Long pathId,
            @RequestParam Integer semester
    ) {
        return ueService.getUEsByInstitutionPathAndSemester(
                institutionId,
                pathId,
                semester
        );
    }


    @PostMapping
    public UE createUE(@RequestBody UE ue) {
        if (ue.getPath() != null && ue.getPath().getIdPath() != null) {
            Path path = pathService
                    .getPathById(ue.getPath().getIdPath())
                    .orElseThrow(() -> new RuntimeException("Path not found"));
            ue.setPath(path);
        }
        return ueService.createUE(ue);
    }

}