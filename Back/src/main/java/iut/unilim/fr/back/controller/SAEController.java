package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.TermsRepository;
import iut.unilim.fr.back.service.SAEService;
import iut.unilim.fr.back.service.UserSyncadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/saes")
@CrossOrigin(origins = "*")
public class SAEController {
    @Autowired
    private SAEService saeService;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private UserSyncadiaService userSyncadiaService;

    @GetMapping
    public List<SAE> getAllSAEs() {
        return saeService.getAllSAEs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SAE> getSAEById(@PathVariable Long id) {
        return saeService.getSAEById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/terms/{termsId}")
    public List<SAE> getSAEsByTermsId(@PathVariable Long termsId) {
        return saeService.getSAEsByTermsId(termsId);
    }

    @GetMapping("/path/{pathId}")
    public List<SAE> getSAEsByPathId(@PathVariable Long pathId) {
        return saeService.getSAEsByPathId(pathId);
    }

    @PostMapping
    public ResponseEntity<SAE> createSAE(@RequestBody Map<String, Object> payload) {
        try {
            // Extract values from payload
            String label = (String) payload.get("label");
            String apogeeCode = (String) payload.get("apogeeCode");
            Integer semester = null;
            Object modalite = payload.get("modalite");
            
            // Handle semester - it might be a string or number
            Object semesterObj = payload.get("semester");
            if (semesterObj != null) {
                if (semesterObj instanceof Number) {
                    semester = ((Number) semesterObj).intValue();
                } else {
                    semester = Integer.parseInt(semesterObj.toString());
                }
            }
            
            Long userId = payload.get("userId") != null ?
                ((Number) payload.get("userId")).longValue() : null;

            if (label == null || label.isEmpty()) {
                System.err.println("❌ Label is required");
                return ResponseEntity.badRequest().build();
            }
            
            if (semester == null) {
                System.err.println("❌ Semester is required");
                return ResponseEntity.badRequest().build();
            }
            
            if (userId == null) {
                System.err.println("❌ userId is required");
                return ResponseEntity.badRequest().build();
            }

            // Retrieve user and their institution
            Optional<UserSyncadia> userOpt = userSyncadiaService.getUserById(userId);
            if (userOpt.isEmpty()) {
                System.err.println("❌ User not found with id: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (userOpt.get().getInstitution() == null) {
                System.err.println("❌ User has no institution: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            UserSyncadia user = userOpt.get();
            Long institutionId = user.getInstitution().getIdInstitution();

            System.out.println("✅ Creating SAE for institution: " + institutionId);

            // Create SAE entity
            SAE sae = new SAE();
            sae.setLabel(label);
            sae.setApogeeCode(apogeeCode);
            sae.setSemester(semester);
            
            // Try to find Terms if termsId is provided
            if (payload.containsKey("modalite") && payload.get("modalite") != null) {
                Long termsId = ((Number) payload.get("modalite")).longValue();
                Optional<Terms> termsOpt = termsRepository.findById(termsId);
                if (termsOpt.isPresent()) {
                    sae.setTerms(termsOpt.get());
                } else {
                    System.err.println("❌ Terms not found with id: " + termsId);
                    return ResponseEntity.badRequest().build();
                }
            }
            
            // Save SAE
            System.out.println("✅ Saving SAE: " + label);
            SAE savedSAE = saeService.createSAE(sae);
            return ResponseEntity.ok(savedSAE);
        } catch (Exception e) {
            System.err.println("❌ Error creating SAE: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public SAE updateSAE(@PathVariable Long id, @RequestBody SAE sae) {
        return saeService.updateSAE(id, sae);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSAE(@PathVariable Long id) {
        saeService.deleteSAE(id);
        return ResponseEntity.noContent().build();
    }
}

