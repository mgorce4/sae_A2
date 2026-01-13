package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.SAE;
import iut.unilim.fr.back.repository.SAERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SAEService {
    @Autowired
    private SAERepository saeRepository;

    public List<SAE> getAllSAEs() {
        return saeRepository.findAll();
    }

    public Optional<SAE> getSAEById(Long id) {
        return saeRepository.findById(id);
    }

    public List<SAE> getSAEsByTermsId(Long termsId) {
        return saeRepository.findByTerms_IdTerms(termsId);
    }

    public List<SAE> getSAEsByPathId(Long pathId) {
        return saeRepository.findByPathId(pathId);
    }

    public SAE createSAE(SAE sae) {
        return saeRepository.save(sae);
    }

    public SAE updateSAE(Long id, SAE sae) {
        sae.setIdSAE(id);
        return saeRepository.save(sae);
    }

    public void deleteSAE(Long id) {
        saeRepository.deleteById(id);
    }
}

