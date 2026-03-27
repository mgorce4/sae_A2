package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

        @Query("SELECT DISTINCT r FROM Resource r WHERE r.path.idPath = :pathId AND r.semester = :semester")
        List<Resource> findByPathIdAndSemester(@Param("pathId") Long pathId, @Param("semester") Integer semester);
    Optional<Resource> findFirstByLabelStartingWith(String resourceName);
    List<Resource> findBySemester(Integer semester);
    boolean existsByLabel(String name);

    @Query("SELECT DISTINCT r FROM Resource r " +
           "JOIN UeCoefficient uc ON uc.resource.idResource = r.idResource " +
           "JOIN UE ue ON ue.ueNumber = uc.ue.ueNumber " +
           "WHERE ue.path.idPath = :pathId")
    List<Resource> findByPathId(@Param("pathId") Long pathId);

    boolean existsByPath_IdPath(Long pathId);

    boolean existsByLabelAndPath_IdPath(String label, Long pathIdPath);
    Optional<Resource> findFirstByLabelAndPath_IdPath(String label, Long pathId);
}

