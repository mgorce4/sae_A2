package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.RessourceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceSheetRepository extends JpaRepository<RessourceSheet, Long> {
    List<RessourceSheet> findByResource_IdResource(Long resourceId);
    Optional<RessourceSheet> findFirstByResource_IdResource(Long resourceId);
}

