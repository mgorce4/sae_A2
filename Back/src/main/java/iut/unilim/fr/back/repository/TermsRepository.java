package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByCode(String code);
    Optional<Terms> findFirstByCode(String code);
}

