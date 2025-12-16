package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.RessourceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceSheetRepository extends JpaRepository<RessourceSheet, Long> {
    List<RessourceSheet> findByUser_IdUser(Long userId);
    List<RessourceSheet> findByRessource_IdRessource(Long ressourceId);
    Optional<RessourceSheet> findFirstByRessource_IdRessource(Long ressourceId);
}

