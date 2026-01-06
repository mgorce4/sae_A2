package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.PedagogicalContent;
import iut.unilim.fr.back.service.PedagogicalContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedagogical-contents")
@CrossOrigin(origins = "*")
public class PedagogicalContentController {
    @Autowired
    private PedagogicalContentService pedagogicalContentService;

    @GetMapping
    public List<PedagogicalContent> getAllPedagogicalContents() {
        return pedagogicalContentService.getAllPedagogicalContents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedagogicalContent> getPedagogicalContentById(@PathVariable Long id) {
        return pedagogicalContentService.getPedagogicalContentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource-sheet/{idResourceSheet}")
    public List<PedagogicalContent> getPedagogicalContentsByResourceSheetId(@PathVariable Long idResourceSheet) {
        return pedagogicalContentService.getPedagogicalContentsByResourceSheetId(idResourceSheet);
    }

    @PostMapping
    public PedagogicalContent createPedagogicalContent(@RequestBody PedagogicalContent pedagogicalContent) {
        return pedagogicalContentService.createPedagogicalContent(pedagogicalContent);
    }

    @PutMapping("/{id}")
    public PedagogicalContent updatePedagogicalContent(@PathVariable Long id, @RequestBody PedagogicalContent pedagogicalContent) {
        return pedagogicalContentService.updatePedagogicalContent(id, pedagogicalContent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedagogicalContent(@PathVariable Long id) {
        pedagogicalContentService.deletePedagogicalContent(id);
        return ResponseEntity.noContent().build();
    }
}

