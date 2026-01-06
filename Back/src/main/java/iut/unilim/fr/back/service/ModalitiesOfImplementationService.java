package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.ModalitiesOfImplementation;
import iut.unilim.fr.back.repository.ModalitiesOfImplementationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModalitiesOfImplementationService {
    @Autowired
    private ModalitiesOfImplementationRepository modalitiesOfImplementationRepository;

    public List<ModalitiesOfImplementation> getAllModalitiesOfImplementation() {
        return modalitiesOfImplementationRepository.findAll();
    }

    public Optional<ModalitiesOfImplementation> getModalitiesOfImplementationById(ModalitiesOfImplementation.ModalityId id) {
        return modalitiesOfImplementationRepository.findById(id);
    }

    public List<ModalitiesOfImplementation> getModalitiesOfImplementationByResourceSheetId(Long idResourceSheet) {
        return modalitiesOfImplementationRepository.findByResourceSheet_IdResourceSheet(idResourceSheet);
    }

    public ModalitiesOfImplementation createModalitiesOfImplementation(ModalitiesOfImplementation modalitiesOfImplementation) {
        return modalitiesOfImplementationRepository.save(modalitiesOfImplementation);
    }

    public ModalitiesOfImplementation updateModalitiesOfImplementation(ModalitiesOfImplementation.ModalityId id, ModalitiesOfImplementation modalitiesOfImplementationDetails) {
        ModalitiesOfImplementation modalitiesOfImplementation = modalitiesOfImplementationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ModalitiesOfImplementation not found"));

        modalitiesOfImplementation.setModality(modalitiesOfImplementationDetails.getModality());
        modalitiesOfImplementation.setResourceSheet(modalitiesOfImplementationDetails.getResourceSheet());

        return modalitiesOfImplementationRepository.save(modalitiesOfImplementation);
    }

    public void deleteModalitiesOfImplementation(ModalitiesOfImplementation.ModalityId id) {
        modalitiesOfImplementationRepository.deleteById(id);
    }

    public void deleteByResourceSheetId(Long idResourceSheet) {
        List<ModalitiesOfImplementation> modalities = getModalitiesOfImplementationByResourceSheetId(idResourceSheet);
        modalitiesOfImplementationRepository.deleteAll(modalities);
    }
}

