package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.RessourceTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RessourceTrackingRepository extends JpaRepository<RessourceTracking, Long> {
    List<RessourceTracking> findByResourceSheet_IdResourceSheet(Long idResourceSheet);
}

