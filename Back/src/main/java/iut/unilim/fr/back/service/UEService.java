package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.UE;
import iut.unilim.fr.back.repository.UERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UEService {
    @Autowired
    private UERepository ueRepository;

    public List<UE> getAllUEs() {
        return ueRepository.findAll();
    }

    public Optional<UE> getUEById(Long id) {
        return ueRepository.findById(id);
    }

    public List<UE> getUEsByPathId(Long pathId) {
        return ueRepository.findByPath_IdPath(pathId);
    }

    public UE createUE(UE ue) {
        return ueRepository.save(ue);
    }

    public UE updateUE(Long id, UE ue) {
        ue.setUeNumber(id);
        return ueRepository.save(ue);
    }
    public List<UE> getUEsByInstitutionPathAndSemester(
            Long institutionId,
            Long pathId,
            Integer semester
    ) {
        // Récupère toutes les UEs pour le path et le semester
        List<UE> ues = ueRepository.findByPath_IdPathAndSemester(pathId, semester);

        // Filtrer en mémoire par institutionId
        return ues.stream()
                .filter(ue -> ue.getPath() != null
                        && ue.getPath().getInstitution() != null
                        && ue.getPath().getInstitution().getIdInstitution().equals(institutionId))
                .toList();
    }




    public void deleteUE(Long id) {
        ueRepository.deleteById(id);
    }
}

