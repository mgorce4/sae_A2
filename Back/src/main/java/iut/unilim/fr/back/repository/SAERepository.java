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
    List<SAE> findByPath_IdPath(Long pathId);

    @Query("SELECT DISTINCT s FROM SAE s " +
           "LEFT JOIN FETCH s.path p " +
           "LEFT JOIN FETCH p.institution " +
           "WHERE p.idPath = :pathId")
    List<SAE> findByPathId(@Param("pathId") Long pathId);
}

