package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.AccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccessRightRepository extends JpaRepository<AccessRight, AccessRight.AccessRightId> {
    List<AccessRight> findByIdUser(Long userId);
}

