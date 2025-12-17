package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Keyword;
import iut.unilim.fr.back.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;

    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAll();
    }

    public Optional<Keyword> getKeywordById(Keyword.KeywordId id) {
        return keywordRepository.findById(id);
    }

    public List<Keyword> getKeywordsByResourceSheetId(Long resourceSheetId) {
        return keywordRepository.findByIdResourceSheet(resourceSheetId);
    }

    public Keyword createKeyword(Keyword keyword) {
        return keywordRepository.save(keyword);
    }

    public void deleteKeyword(Keyword.KeywordId id) {
        keywordRepository.deleteById(id);
    }
}

