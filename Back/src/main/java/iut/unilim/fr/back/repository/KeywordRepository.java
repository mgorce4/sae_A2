package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Keyword.KeywordId> {
    List<Keyword> findByIdResourceSheet(Long idResourceSheet);
}

