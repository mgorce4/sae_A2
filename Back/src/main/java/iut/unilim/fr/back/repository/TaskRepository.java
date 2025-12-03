package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser_IdUser(Long userId);
}

