package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.NationalProgramObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationalProgramObjectiveRepository extends JpaRepository<NationalProgramObjective, Long> {
    List<NationalProgramObjective> findByResourceSheet_IdResourceSheet(Long idResourceSheet);
}

