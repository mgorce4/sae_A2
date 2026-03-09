package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.HoursPerStudent;
import iut.unilim.fr.back.entity.Resource;
import iut.unilim.fr.back.repository.HoursPerStudentRepository;
import iut.unilim.fr.back.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HoursPerStudentService {
    @Autowired
    private HoursPerStudentRepository hoursPerStudentRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    public List<HoursPerStudent> getAllHoursPerStudent() {
        return hoursPerStudentRepository.findAll();
    }

    public Optional<HoursPerStudent> getHoursPerStudentById(Long id) {
        return hoursPerStudentRepository.findById(id);
    }

    public List<HoursPerStudent> getHoursPerStudentByResourceId(Long resourceId) {
        return hoursPerStudentRepository.findByResource_IdResource(resourceId);
    }

    public HoursPerStudent createHoursPerStudent(HoursPerStudent hoursPerStudent) {
        // Gérer Resource - récupérer l'entité existante
        if (hoursPerStudent.getResource() != null && hoursPerStudent.getResource().getIdResource() != null) {
            Optional<Resource> existingResource = resourceRepository.findById(hoursPerStudent.getResource().getIdResource());
            existingResource.ifPresent(hoursPerStudent::setResource);
        }

        return hoursPerStudentRepository.save(hoursPerStudent);
    }

    public HoursPerStudent updateHoursPerStudent(Long id, HoursPerStudent hoursPerStudent) {
        hoursPerStudent.setIdHoursPerStudent(id);
        return hoursPerStudentRepository.save(hoursPerStudent);
    }

    public void deleteHoursPerStudent(Long id) {
        hoursPerStudentRepository.deleteById(id);
    }
}

