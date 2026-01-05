package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.TeacherHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherHoursRepository extends JpaRepository<TeacherHours, Long> {
    List<TeacherHours> findByResourceSheet_IdResourceSheet(Long idResourceSheet);
}

