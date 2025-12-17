package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.SAE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SAERepository extends JpaRepository<SAE, Long> {
    List<SAE> findByTerms_IdTerms(Long idTerms);
}

