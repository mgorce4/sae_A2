package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathRepository extends JpaRepository<Path, Long> {
}

