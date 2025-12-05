package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.Institution;
import iut.unilim.fr.back.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionService {
    @Autowired
    private InstitutionRepository institutionRepository;

    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    public Optional<Institution> getInstitutionById(Long id) {
        return institutionRepository.findById(id);
    }

    public Institution createInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    public Institution updateInstitution(Long id, Institution institution) {
        institution.setIdInstitution(id);
        return institutionRepository.save(institution);
    }

    public void deleteInstitution(Long id) {
        institutionRepository.deleteById(id);
    }
}

