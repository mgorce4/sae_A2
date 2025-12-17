package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.NationalProgramObjective;
import iut.unilim.fr.back.repository.NationalProgramObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NationalProgramObjectiveService {
    @Autowired
    private NationalProgramObjectiveRepository nationalProgramObjectiveRepository;

    public List<NationalProgramObjective> getAllNationalProgramObjectives() {
        return nationalProgramObjectiveRepository.findAll();
    }

    public Optional<NationalProgramObjective> getNationalProgramObjectiveById(Long id) {
        return nationalProgramObjectiveRepository.findById(id);
    }

    public List<NationalProgramObjective> getNationalProgramObjectivesByRessourceSheetId(Long idRessourceSheet) {
        return nationalProgramObjectiveRepository.findByResourceSheet_IdResourceSheet(idRessourceSheet);
    }

    public NationalProgramObjective createNationalProgramObjective(NationalProgramObjective nationalProgramObjective) {
        return nationalProgramObjectiveRepository.save(nationalProgramObjective);
    }

    public NationalProgramObjective updateNationalProgramObjective(Long id, NationalProgramObjective nationalProgramObjectiveDetails) {
        NationalProgramObjective nationalProgramObjective = nationalProgramObjectiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NationalProgramObjective not found"));

        nationalProgramObjective.setContent(nationalProgramObjectiveDetails.getContent());
        nationalProgramObjective.setResourceSheet(nationalProgramObjectiveDetails.getResourceSheet());

        return nationalProgramObjectiveRepository.save(nationalProgramObjective);
    }

    public void deleteNationalProgramObjective(Long id) {
        nationalProgramObjectiveRepository.deleteById(id);
    }
}

