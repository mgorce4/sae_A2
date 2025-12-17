package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Keyword;
import iut.unilim.fr.back.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keywords")
@CrossOrigin(origins = "*")
public class KeywordController {
    @Autowired
    private KeywordService keywordService;

    @GetMapping
    public List<Keyword> getAllKeywords() {
        return keywordService.getAllKeywords();
    }

    @GetMapping("/resource-sheet/{resourceSheetId}")
    public List<Keyword> getKeywordsByResourceSheetId(@PathVariable Long resourceSheetId) {
        return keywordService.getKeywordsByResourceSheetId(resourceSheetId);
    }

    @PostMapping
    public Keyword createKeyword(@RequestBody Keyword keyword) {
        return keywordService.createKeyword(keyword);
    }

    @DeleteMapping("/keyword/{keyword}/resource-sheet/{resourceSheetId}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable String keyword, @PathVariable Long resourceSheetId) {
        Keyword.KeywordId id = new Keyword.KeywordId(keyword, resourceSheetId);
        keywordService.deleteKeyword(id);
        return ResponseEntity.noContent().build();
    }
}

