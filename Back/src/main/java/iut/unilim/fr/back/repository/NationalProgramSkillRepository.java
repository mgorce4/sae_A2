package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.NationalProgramSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NationalProgramSkillRepository extends JpaRepository<NationalProgramSkill, Long> {
    List<NationalProgramSkill> findByResourceSheet_IdResourceSheet(Long idResourceSheet);
}

