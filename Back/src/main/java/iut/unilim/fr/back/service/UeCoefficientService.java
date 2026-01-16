package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Ressource;
import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.entity.UeCoefficient;
import iut.unilim.fr.back.repository.RessourceRepository;
import iut.unilim.fr.back.repository.UERepository;
import iut.unilim.fr.back.repository.UeCoefficientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UeCoefficientService {
    @Autowired
    private UeCoefficientRepository ueCoefficientRepository;

    @Autowired
    private UERepository ueRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

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

    public UeCoefficient createUeCoefficient(UeCoefficient ueCoefficient) {
        // Gérer UE - récupérer l'entité existante
        if (ueCoefficient.getUe() != null && ueCoefficient.getUe().getUeNumber() != null) {
            Optional<UE> existingUE = ueRepository.findById(ueCoefficient.getUe().getUeNumber());
            existingUE.ifPresent(ueCoefficient::setUe);
        }

        // Gérer Resource - récupérer l'entité existante
        if (ueCoefficient.getResource() != null && ueCoefficient.getResource().getIdResource() != null) {
            Optional<Ressource> existingResource = ressourceRepository.findById(ueCoefficient.getResource().getIdResource());
            existingResource.ifPresent(ueCoefficient::setResource);
        }

        return ueCoefficientRepository.save(ueCoefficient);
    }

    public UeCoefficient updateUeCoefficient(Long id, UeCoefficient ueCoefficient) {
        ueCoefficient.setIdCoefficient(id);
        return ueCoefficientRepository.save(ueCoefficient);
    }

    public void deleteUeCoefficient(Long id) {
        ueCoefficientRepository.deleteById(id);
    }

    public void deleteAllByResourceId(Long idResource) {
        List<UeCoefficient> coefficients = ueCoefficientRepository.findByResource_IdResource(idResource);
        // Supprimer un par un pour éviter les problèmes de contraintes
        for (UeCoefficient coefficient : coefficients) {
            ueCoefficientRepository.deleteById(coefficient.getIdCoefficient());
        }
    }
}

