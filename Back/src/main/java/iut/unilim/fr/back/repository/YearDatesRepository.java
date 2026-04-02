package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.YearDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YearDatesRepository extends JpaRepository<YearDates, Long> {
    List<YearDates> findByInstitution_IdInstitution(Long institutionId);
    Optional<YearDates> findFirstByInstitution_IdInstitutionOrderByStartYearDesc(Long institutionId);
}
