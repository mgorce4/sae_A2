package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource, Long> {

        @Query("SELECT DISTINCT r FROM Ressource r WHERE r.path.idPath = :pathId AND r.semester = :semester")
        List<Ressource> findByPathIdAndSemester(@Param("pathId") Long pathId, @Param("semester") Integer semester);
    Optional<Ressource> findFirstByLabelStartingWith(String ressourceName);
    List<Ressource> findBySemester(Integer semester);

    @Query("SELECT DISTINCT r FROM Ressource r " +
           "JOIN UeCoefficient uc ON uc.resource.idResource = r.idResource " +
           "JOIN UE ue ON ue.ueNumber = uc.ue.ueNumber " +
           "WHERE ue.path.idPath = :pathId")
    List<Ressource> findByPathId(@Param("pathId") Long pathId);
}

