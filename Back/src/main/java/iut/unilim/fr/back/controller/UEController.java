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
    public ResponseEntity<?> createUE(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Received payload: " + payload);
            
            // Extract values from payload
            String euApogeeCode = (String) payload.get("euApogeeCode");

            // Handle label - can be String or Number from frontend
            Object labelObj = payload.get("label");
            String label = labelObj instanceof Number ?
                String.valueOf(labelObj) : (String) labelObj;

            String name = (String) payload.get("name");
            Integer competenceLevel = (Integer) payload.get("competenceLevel");
            Integer semester = (Integer) payload.get("semester");

            // Get userId and retrieve the user's institution
            Long userId = payload.get("userId") != null ?
                ((Number) payload.get("userId")).longValue() : null;

            if (userId == null) {
                System.err.println("userId is null");
                return ResponseEntity.badRequest().body("userId is required");
            }

            // Retrieve user and their institution
            Optional<UserSyncadia> userOpt = userSyncadiaService.getUserById(userId);
            if (userOpt.isEmpty()) {
                System.err.println("User not found with id: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found with id: " + userId);
            }

            if (userOpt.get().getInstitution() == null) {
                System.err.println("User has no institution: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User has no institution");
            }

            UserSyncadia user = userOpt.get();
            Long institutionId = user.getInstitution().getIdInstitution();

            System.out.println("Creating UE for institution: " + institutionId);

            // Find or create a path for this institution
            Path path = pathRepository.findAll().stream()
                .filter(p -> p.getInstitution() != null &&
                            p.getInstitution().getIdInstitution().equals(institutionId))
                .findFirst()
                .orElseGet(() -> {
                    System.out.println("Creating new path for institution: " + institutionId);
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

            System.out.println("Saving UE: " + label + " - " + name);
            UE savedUE = ueService.createUE(ue);
            System.out.println("UE saved successfully with ID: " + savedUE.getUeNumber());
            return ResponseEntity.ok(savedUE);
        } catch (Exception e) {
            String errorMessage = "Error creating UE: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
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