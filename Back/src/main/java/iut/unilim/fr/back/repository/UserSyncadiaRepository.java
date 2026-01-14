package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.UserSyncadia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSyncadiaRepository extends JpaRepository<UserSyncadia, Long> {
    Optional<UserSyncadia> findByUsername(String username);
    List<UserSyncadia> findByFirstnameAndLastname(String firstname, String lastname);
}

