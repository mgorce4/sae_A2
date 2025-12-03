package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.UE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {
    List<UE> findByPath_IdPath(Long pathId);
}

