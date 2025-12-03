package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.HoursPerStudent;
import iut.unilim.fr.back.repository.HoursPerStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HoursPerStudentService {
    @Autowired
    private HoursPerStudentRepository hoursPerStudentRepository;

    public List<HoursPerStudent> getAllHoursPerStudent() {
        return hoursPerStudentRepository.findAll();
    }

    public Optional<HoursPerStudent> getHoursPerStudentById(Long id) {
        return hoursPerStudentRepository.findById(id);
    }

    public HoursPerStudent createHoursPerStudent(HoursPerStudent hoursPerStudent) {
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

