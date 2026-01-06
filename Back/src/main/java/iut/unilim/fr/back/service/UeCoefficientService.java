package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.UeCoefficient;
import iut.unilim.fr.back.repository.UeCoefficientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UeCoefficientService {
    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;

    public List<UeCoefficient> getAllUeCoefficients() {
        return ueCoefficientRepository.findAll();
    }

    public Optional<UeCoefficient> getUeCoefficientById(Long id) {
        return ueCoefficientRepository.findById(id);
    }

    public List<UeCoefficient> getUeCoefficientsByUeNumber(Long ueNumber) {
        return ueCoefficientRepository.findByUe_UeNumber(ueNumber);
    }

    public List<UeCoefficient> getUeCoefficientsByResourceId(Long idResource) {
        return ueCoefficientRepository.findByResource_IdResource(idResource);
    }

    public UeCoefficient updateUeCoefficient(Long id, UeCoefficient ueCoefficient) {
        ueCoefficient.setIdCoefficient(id);
        return ueCoefficientRepository.save(ueCoefficient);
    }

    public void deleteUeCoefficient(Long id) {
        ueCoefficientRepository.deleteById(id);
    }
}

