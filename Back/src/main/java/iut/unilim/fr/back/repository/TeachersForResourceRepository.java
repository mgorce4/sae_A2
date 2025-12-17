package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.TeachersForResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeachersForResourceRepository extends JpaRepository<TeachersForResource, TeachersForResource.TeachersForResourceId> {
    List<TeachersForResource> findByIdUser(Long idUser);
    List<TeachersForResource> findByIdResource(Long idResource);
}

