package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.MainTeacherForResource;
import iut.unilim.fr.back.repository.MainTeacherForResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MainTeacherForResourceService {
    @Autowired
    private MainTeacherForResourceRepository mainTeacherForResourceRepository;

    public List<MainTeacherForResource> getAllMainTeacherForResources() {
        return mainTeacherForResourceRepository.findAll();
    }

    public Optional<MainTeacherForResource> getMainTeacherForResourceById(MainTeacherForResource.MainTeacherForResourceId id) {
        return mainTeacherForResourceRepository.findById(id);
    }

    public List<MainTeacherForResource> getMainTeachersByUserId(Long userId) {
        return mainTeacherForResourceRepository.findByIdUser(userId);
    }

    public List<MainTeacherForResource> getMainTeachersByResourceId(Long resourceId) {
        return mainTeacherForResourceRepository.findByIdResource(resourceId);
    }

    public MainTeacherForResource createMainTeacherForResource(MainTeacherForResource mainTeacherForResource) {
        return mainTeacherForResourceRepository.save(mainTeacherForResource);
    }

    public void deleteMainTeacherForResource(MainTeacherForResource.MainTeacherForResourceId id) {
        mainTeacherForResourceRepository.deleteById(id);
    }
}

