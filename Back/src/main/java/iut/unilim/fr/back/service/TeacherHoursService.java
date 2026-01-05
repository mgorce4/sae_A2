package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.TeacherHours;
import iut.unilim.fr.back.repository.TeacherHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherHoursService {
    @Autowired
    private TeacherHoursRepository teacherHoursRepository;

    public List<TeacherHours> getAllTeacherHours() {
        return teacherHoursRepository.findAll();
    }

    public Optional<TeacherHours> getTeacherHoursById(Long id) {
        return teacherHoursRepository.findById(id);
    }

    public List<TeacherHours> getTeacherHoursByResourceSheetId(Long idResourceSheet) {
        return teacherHoursRepository.findByResourceSheet_IdResourceSheet(idResourceSheet);
    }

    public TeacherHours createTeacherHours(TeacherHours teacherHours) {
        return teacherHoursRepository.save(teacherHours);
    }

    public TeacherHours updateTeacherHours(Long id, TeacherHours teacherHoursDetails) {
        TeacherHours teacherHours = teacherHoursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TeacherHours not found"));

        teacherHours.setCm(teacherHoursDetails.getCm());
        teacherHours.setTd(teacherHoursDetails.getTd());
        teacherHours.setTp(teacherHoursDetails.getTp());
        teacherHours.setIsAlternance(teacherHoursDetails.getIsAlternance());
        teacherHours.setResourceSheet(teacherHoursDetails.getResourceSheet());

        return teacherHoursRepository.save(teacherHours);
    }

    public void deleteTeacherHours(Long id) {
        teacherHoursRepository.deleteById(id);
    }
}

