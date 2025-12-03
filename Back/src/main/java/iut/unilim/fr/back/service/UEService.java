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

    public void deleteUE(Long id) {
        ueRepository.deleteById(id);
    }
}

