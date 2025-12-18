package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.HoursPerStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoursPerStudentRepository extends JpaRepository<HoursPerStudent, Long> {
    List<HoursPerStudent> findByResource_IdResource(Long idResource);
}

