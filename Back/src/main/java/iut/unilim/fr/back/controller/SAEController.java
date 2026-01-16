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
import iut.unilim.fr.back.repository.PathRepository;
import iut.unilim.fr.back.entity.Path;


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

    @Autowired
    private PathRepository pathRepository;


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
            // ----- 1️⃣ Récupération des champs simples -----
            String label = (String) payload.get("label");
            String apogeeCode = (String) payload.get("apogeeCode");
            Integer semester = null;
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

            if (label == null || label.isEmpty()) return ResponseEntity.badRequest().body(null);
            if (semester == null) return ResponseEntity.badRequest().body(null);
            if (userId == null) return ResponseEntity.badRequest().body(null);

            // ----- 2️⃣ Récupération et validation de l'utilisateur -----
            Optional<UserSyncadia> userOpt = userSyncadiaService.getUserById(userId);
            if (userOpt.isEmpty() || userOpt.get().getInstitution() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            UserSyncadia user = userOpt.get();

            // ----- 3️⃣ Création de la SAE -----
            SAE sae = new SAE();
            sae.setLabel(label);
            sae.setApogeeCode(apogeeCode);
            sae.setSemester(semester);

            // ----- 4️⃣ Récupérer Terms si fourni -----
            if (payload.get("modalite") != null) {
                Long termsId = ((Number) payload.get("modalite")).longValue();
                Optional<Terms> termsOpt = termsRepository.findById(termsId);
                termsOpt.ifPresent(sae::setTerms);
            }

            // ----- 5️⃣ Récupérer Path si fourni -----
            if (payload.get("pathId") != null) {
                Long pathId = ((Number) payload.get("pathId")).longValue();
                Optional<Path> pathOpt = pathRepository.findById(pathId);
                if (pathOpt.isPresent()) {
                    sae.setPath(pathOpt.get()); // ✅ LIGNE CRUCIALE
                } else {
                    System.err.println("❌ Path not found with id: " + pathId);
                    return ResponseEntity.badRequest().build();
                }
            }

            // ----- 6️⃣ Sauvegarde -----
            SAE savedSAE = saeService.createSAE(sae);
            return ResponseEntity.ok(savedSAE);

        } catch (Exception e) {
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

