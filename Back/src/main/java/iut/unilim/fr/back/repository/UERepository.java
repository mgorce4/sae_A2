package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.UE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {
    List<UE> findByPath_IdPath(Long pathId);
    List<UE> findBySemester(Integer semester);
    List<UE> findByCompetenceLevel(Integer competenceLevel);
    Optional<UE> findByLabel(String label);
    Optional<UE> findByLabelAndSemesterAndPath_IdPath(String label, Integer semester, Long pathId);
    List<UE> findByPath_IdPathAndSemester(Long pathId, Integer semester);

}

