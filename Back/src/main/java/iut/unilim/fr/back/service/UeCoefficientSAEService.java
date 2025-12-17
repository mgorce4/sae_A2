package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.UeCoefficientSAE;
import iut.unilim.fr.back.repository.UeCoefficientSAERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UeCoefficientSAEService {
    @Autowired
    private UeCoefficientSAERepository ueCoefficientSAERepository;

    public List<UeCoefficientSAE> getAllUeCoefficientSAEs() {
        return ueCoefficientSAERepository.findAll();
    }

    public Optional<UeCoefficientSAE> getUeCoefficientSAEById(Long id) {
        return ueCoefficientSAERepository.findById(id);
    }

    public List<UeCoefficientSAE> getUeCoefficientSAEsByUeNumber(Long ueNumber) {
        return ueCoefficientSAERepository.findByUe_UeNumber(ueNumber);
    }

    public List<UeCoefficientSAE> getUeCoefficientSAEsBySAEId(Long saeId) {
        return ueCoefficientSAERepository.findBySae_IdSAE(saeId);
    }

    public UeCoefficientSAE createUeCoefficientSAE(UeCoefficientSAE ueCoefficientSAE) {
        return ueCoefficientSAERepository.save(ueCoefficientSAE);
    }

    public UeCoefficientSAE updateUeCoefficientSAE(Long id, UeCoefficientSAE ueCoefficientSAE) {
        ueCoefficientSAE.setIdCoefficientSAE(id);
        return ueCoefficientSAERepository.save(ueCoefficientSAE);
    }

    public void deleteUeCoefficientSAE(Long id) {
        ueCoefficientSAERepository.deleteById(id);
    }
}

