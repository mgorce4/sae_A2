package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.ModalitiesOfImplementation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModalitiesOfImplementationRepository extends JpaRepository<ModalitiesOfImplementation, ModalitiesOfImplementation.ModalityId> {
    List<ModalitiesOfImplementation> findByResourceSheet_IdResourceSheet(Long idResourceSheet);
}

