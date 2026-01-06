package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.PedagogicalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedagogicalContentRepository extends JpaRepository<PedagogicalContent, Long> {
    List<PedagogicalContent> findByResourceSheet_IdResourceSheet(Long idResourceSheet);
}

