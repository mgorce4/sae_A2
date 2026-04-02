package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.ResourceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceSheetRepository extends JpaRepository<ResourceSheet, Long> {
    List<ResourceSheet> findByResource_IdResource(Long resourceId);
    Optional<ResourceSheet> findFirstByResource_IdResource(Long resourceId);

    /**
     * Find all resource sheets where the user is the main teacher
     * Joins RESOURCE_SHEET -> RESOURCE -> MAIN_TEACHER_FOR_RESOURCE
     */
    @Query("SELECT rs FROM ResourceSheet rs " +
           "JOIN rs.resource r " +
           "JOIN MainTeacherForResource mt ON mt.idResource = r.idResource " +
           "WHERE mt.idUser = :userId")
    List<ResourceSheet> findByMainTeacher(@Param("userId") Long userId);

    @Query("SELECT rs FROM ResourceSheet rs " +
           "JOIN rs.resource r " +
           "JOIN r.path p " +
           "JOIN p.institution i " +
           "WHERE i.idInstitution = :institutionId")
    List<ResourceSheet> findByInstitutionId(@Param("institutionId") Long institutionId);

    @Query("SELECT new iut.unilim.fr.back.dto.ResourceSheetSummaryDTO(" +
           "rs.idResourceSheet, r.label, r.semester, r.path.name, " +
           "(SELECT COUNT(th) FROM TeacherHours th WHERE th.resourceSheet = rs AND th.isAlternance = false)) " +
           "FROM ResourceSheet rs " +
           "JOIN rs.resource r " +
           "JOIN r.path p " +
           "JOIN p.institution i " +
           "WHERE i.idInstitution = :institutionId")
    List<iut.unilim.fr.back.dto.ResourceSheetSummaryDTO> findSummaryByInstitutionId(@Param("institutionId") Long institutionId);
}

