package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.MainTeacherForResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainTeacherForResourceRepository extends JpaRepository<MainTeacherForResource, MainTeacherForResource.MainTeacherForResourceId> {
    List<MainTeacherForResource> findByIdUser(Long idUser);
    List<MainTeacherForResource> findByIdResource(Long idResource);
}

