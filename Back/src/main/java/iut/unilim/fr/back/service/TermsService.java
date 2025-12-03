package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Terms;
import iut.unilim.fr.back.repository.TermsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TermsService {
    @Autowired
    private TermsRepository termsRepository;

    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    public Optional<Terms> getTermsById(Long id) {
        return termsRepository.findById(id);
    }

    public Terms createTerms(Terms terms) {
        return termsRepository.save(terms);
    }

    public Terms updateTerms(Long id, Terms terms) {
        terms.setIdTerms(id);
        return termsRepository.save(terms);
    }

    public void deleteTerms(Long id) {
        termsRepository.deleteById(id);
    }
}

