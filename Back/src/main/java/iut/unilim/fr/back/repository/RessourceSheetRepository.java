package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.RessourceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceSheetRepository extends JpaRepository<RessourceSheet, Long> {
    List<RessourceSheet> findByResource_IdResource(Long resourceId);
    Optional<RessourceSheet> findFirstByResource_IdResource(Long resourceId);

    /**
     * Find all resource sheets where the user is the main teacher
     * Joins RESOURCE_SHEET -> RESOURCE -> MAIN_TEACHER_FOR_RESOURCE
     */
    @Query("SELECT rs FROM RessourceSheet rs " +
           "JOIN rs.resource r " +
           "JOIN MainTeacherForResource mt ON mt.idResource = r.idResource " +
           "WHERE mt.idUser = :userId")
    List<RessourceSheet> findByMainTeacher(@Param("userId") Long userId);
}

