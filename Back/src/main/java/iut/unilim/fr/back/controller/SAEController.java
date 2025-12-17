package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.service.SAEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saes")
@CrossOrigin(origins = "*")
public class SAEController {
    @Autowired
    private SAEService saeService;

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

    @PostMapping
    public SAE createSAE(@RequestBody SAE sae) {
        return saeService.createSAE(sae);
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

