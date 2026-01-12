package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.FinalDeliveryDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinalDeliveryDatesRepository extends JpaRepository<FinalDeliveryDates, Long> {
    Optional<FinalDeliveryDates> findByInstitution_IdInstitution(Long institutionId);
}

