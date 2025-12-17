package iut.unilim.fr.back.repository;

import iut.unilim.fr.back.entity.SAELinkResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SAELinkResourceRepository extends JpaRepository<SAELinkResource, SAELinkResource.SAELinkResourceId> {
    List<SAELinkResource> findByIdSAE(Long idSAE);
    List<SAELinkResource> findByIdResource(Long idResource);
}

