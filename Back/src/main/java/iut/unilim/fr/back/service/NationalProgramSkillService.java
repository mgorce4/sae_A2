package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.NationalProgramSkill;
import iut.unilim.fr.back.repository.NationalProgramSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NationalProgramSkillService {
    @Autowired
    private NationalProgramSkillRepository nationalProgramSkillRepository;

    public List<NationalProgramSkill> getAllNationalProgramSkills() {
        return nationalProgramSkillRepository.findAll();
    }

    public Optional<NationalProgramSkill> getNationalProgramSkillById(Long id) {
        return nationalProgramSkillRepository.findById(id);
    }

    public List<NationalProgramSkill> getNationalProgramSkillsByResourceSheetId(Long resourceSheetId) {
        return nationalProgramSkillRepository.findByResourceSheet_IdResourceSheet(resourceSheetId);
    }

    public NationalProgramSkill createNationalProgramSkill(NationalProgramSkill nationalProgramSkill) {
        return nationalProgramSkillRepository.save(nationalProgramSkill);
    }

    public NationalProgramSkill updateNationalProgramSkill(Long id, NationalProgramSkill nationalProgramSkill) {
        nationalProgramSkill.setIdSkill(id);
        return nationalProgramSkillRepository.save(nationalProgramSkill);
    }

    public void deleteNationalProgramSkill(Long id) {
        nationalProgramSkillRepository.deleteById(id);
    }
}

