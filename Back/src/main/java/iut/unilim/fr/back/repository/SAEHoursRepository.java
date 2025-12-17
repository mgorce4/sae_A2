package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.SAEHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SAEHoursRepository extends JpaRepository<SAEHours, Long> {
    List<SAEHours> findBySae_IdSAE(Long idSAE);
}

