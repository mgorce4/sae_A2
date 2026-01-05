package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.UeCoefficient;
import iut.unilim.fr.back.service.UeCoefficientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ue-coefficients")
@CrossOrigin(origins = "*")
public class UeCoefficientController {
    @Autowired
    private UeCoefficientService ueCoefficientService;

    @GetMapping
    public List<UeCoefficient> getAllUeCoefficients() {
        return ueCoefficientService.getAllUeCoefficients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UeCoefficient> getUeCoefficientById(@PathVariable Long id) {
        return ueCoefficientService.getUeCoefficientById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ue/{ueNumber}")
    public List<UeCoefficient> getUeCoefficientsByUeNumber(@PathVariable Long ueNumber) {
        return ueCoefficientService.getUeCoefficientsByUeNumber(ueNumber);
    }

    @GetMapping("/resource/{idResource}")
    public List<UeCoefficient> getUeCoefficientsByResourceId(@PathVariable Long idResource) {
        return ueCoefficientService.getUeCoefficientsByResourceId(idResource);
    }

    @PostMapping
    public UeCoefficient createUeCoefficient(@RequestBody UeCoefficient ueCoefficient) {
        return ueCoefficientService.createUeCoefficient(ueCoefficient);
    }

    @PutMapping("/{id}")
    public UeCoefficient updateUeCoefficient(@PathVariable Long id, @RequestBody UeCoefficient ueCoefficient) {
        return ueCoefficientService.updateUeCoefficient(id, ueCoefficient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUeCoefficient(@PathVariable Long id) {
        ueCoefficientService.deleteUeCoefficient(id);
        return ResponseEntity.noContent().build();
    }
}

