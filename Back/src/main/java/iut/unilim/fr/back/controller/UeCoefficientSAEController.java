package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.UeCoefficientSAE;
import iut.unilim.fr.back.service.UeCoefficientSAEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ue-coefficient-saes")
@CrossOrigin(origins = "*")
public class UeCoefficientSAEController {
    @Autowired
    private UeCoefficientSAEService ueCoefficientSAEService;

    @GetMapping
    public List<UeCoefficientSAE> getAllUeCoefficientSAEs() {
        return ueCoefficientSAEService.getAllUeCoefficientSAEs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UeCoefficientSAE> getUeCoefficientSAEById(@PathVariable Long id) {
        return ueCoefficientSAEService.getUeCoefficientSAEById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ue/{ueNumber}")
    public List<UeCoefficientSAE> getUeCoefficientSAEsByUeNumber(@PathVariable Long ueNumber) {
        return ueCoefficientSAEService.getUeCoefficientSAEsByUeNumber(ueNumber);
    }

    @GetMapping("/sae/{saeId}")
    public List<UeCoefficientSAE> getUeCoefficientSAEsBySAEId(@PathVariable Long saeId) {
        return ueCoefficientSAEService.getUeCoefficientSAEsBySAEId(saeId);
    }

    @PostMapping
    public UeCoefficientSAE createUeCoefficientSAE(@RequestBody UeCoefficientSAE ueCoefficientSAE) {
        return ueCoefficientSAEService.createUeCoefficientSAE(ueCoefficientSAE);
    }

    @PutMapping("/{id}")
    public UeCoefficientSAE updateUeCoefficientSAE(@PathVariable Long id, @RequestBody UeCoefficientSAE ueCoefficientSAE) {
        return ueCoefficientSAEService.updateUeCoefficientSAE(id, ueCoefficientSAE);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUeCoefficientSAE(@PathVariable Long id) {
        ueCoefficientSAEService.deleteUeCoefficientSAE(id);
        return ResponseEntity.noContent().build();
    }
}

