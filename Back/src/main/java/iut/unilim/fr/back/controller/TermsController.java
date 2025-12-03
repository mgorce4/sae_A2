package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.service.TermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/terms")
@CrossOrigin(origins = "*")
public class TermsController {
    @Autowired
    private TermsService termsService;

    @GetMapping
    public List<Terms> getAllTerms() {
        return termsService.getAllTerms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terms> getTermsById(@PathVariable Long id) {
        return termsService.getTermsById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Terms createTerms(@RequestBody Terms terms) {
        return termsService.createTerms(terms);
    }

    @PutMapping("/{id}")
    public Terms updateTerms(@PathVariable Long id, @RequestBody Terms terms) {
        return termsService.updateTerms(id, terms);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerms(@PathVariable Long id) {
        termsService.deleteTerms(id);
        return ResponseEntity.noContent().build();
    }
}

