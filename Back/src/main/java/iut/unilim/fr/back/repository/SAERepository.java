package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.SAE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SAERepository extends JpaRepository<SAE, Long> {
    List<SAE> findByTerms_IdTerms(Long idTerms);
    List<SAE> findBySemester(Integer semester);

    @Query("SELECT DISTINCT s FROM SAE s " +
           "JOIN UeCoefficientSAE ucs ON ucs.sae.idSAE = s.idSAE " +
           "JOIN UE ue ON ue.ueNumber = ucs.ue.ueNumber " +
           "WHERE ue.path.idPath = :pathId")
    List<SAE> findByPathId(@Param("pathId") Long pathId);
}

