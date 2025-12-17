package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.TeachersForResource;
import iut.unilim.fr.back.repository.TeachersForResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeachersForResourceService {
    @Autowired
    private TeachersForResourceRepository teachersForResourceRepository;

    public List<TeachersForResource> getAllTeachersForResources() {
        return teachersForResourceRepository.findAll();
    }

    public Optional<TeachersForResource> getTeachersForResourceById(TeachersForResource.TeachersForResourceId id) {
        return teachersForResourceRepository.findById(id);
    }

    public List<TeachersForResource> getTeachersByUserId(Long userId) {
        return teachersForResourceRepository.findByIdUser(userId);
    }

    public List<TeachersForResource> getTeachersByResourceId(Long resourceId) {
        return teachersForResourceRepository.findByIdResource(resourceId);
    }

    public TeachersForResource createTeachersForResource(TeachersForResource teachersForResource) {
        return teachersForResourceRepository.save(teachersForResource);
    }

    public void deleteTeachersForResource(TeachersForResource.TeachersForResourceId id) {
        teachersForResourceRepository.deleteById(id);
    }
}

