package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource, Long> {
    Optional<Ressource> findFirstByLabelStartingWith(String ressourceName);
    List<Ressource> findBySemester(Integer semester);
}

