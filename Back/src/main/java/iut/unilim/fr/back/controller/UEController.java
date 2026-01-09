package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.service.UEService;
import iut.unilim.fr.back.service.UserSyncadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ues")
@CrossOrigin(origins = "*")
public class UEController {
    @Autowired
    private UEService ueService;

    @Autowired
    private PathRepository pathRepository;

    @Autowired
    private UserSyncadiaService userSyncadiaService;

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

    @PostMapping
    public ResponseEntity<UE> createUE(@RequestBody Map<String, Object> payload) {
        try {
            // Extract values from payload
            String euApogeeCode = (String) payload.get("euApogeeCode");
            String label = (String) payload.get("label");
            String name = (String) payload.get("name");
            Integer competenceLevel = (Integer) payload.get("competenceLevel");
            Integer semester = (Integer) payload.get("semester");

            // Get userId and retrieve the user's institution
            Long userId = payload.get("userId") != null ?
                ((Number) payload.get("userId")).longValue() : null;

            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }

            // Retrieve user and their institution
            Optional<UserSyncadia> userOpt = userSyncadiaService.getUserById(userId);
            if (userOpt.isEmpty() || userOpt.get().getInstitution() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
            }

            UserSyncadia user = userOpt.get();
            Long institutionId = user.getInstitution().getIdInstitution();

            // Find or create a path for this institution
            Path path = pathRepository.findAll().stream()
                .filter(p -> p.getInstitution() != null &&
                            p.getInstitution().getIdInstitution().equals(institutionId))
                .findFirst()
                .orElseGet(() -> {
                    Path newPath = new Path();
                    newPath.setNumber(1);
                    newPath.setName("Default Path - " + user.getInstitution().getName());
                    newPath.setInstitution(user.getInstitution());
                    return pathRepository.save(newPath);
                });

            // Create UE entity
            UE ue = new UE();
            ue.setEuApogeeCode(euApogeeCode);
            ue.setLabel(label);
            ue.setName(name);
            ue.setCompetenceLevel(competenceLevel);
            ue.setSemester(semester);
            ue.setPath(path);

            UE savedUE = ueService.createUE(ue);
            return ResponseEntity.ok(savedUE);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
}

