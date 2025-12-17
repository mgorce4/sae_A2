package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.SAELinkResource;
import iut.unilim.fr.back.repository.SAELinkResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SAELinkResourceService {
    @Autowired
    private SAELinkResourceRepository saeLinkResourceRepository;

    public List<SAELinkResource> getAllSAELinkResources() {
        return saeLinkResourceRepository.findAll();
    }

    public Optional<SAELinkResource> getSAELinkResourceById(SAELinkResource.SAELinkResourceId id) {
        return saeLinkResourceRepository.findById(id);
    }

    public List<SAELinkResource> getSAELinkResourcesBySAEId(Long saeId) {
        return saeLinkResourceRepository.findByIdSAE(saeId);
    }

    public List<SAELinkResource> getSAELinkResourcesByResourceId(Long resourceId) {
        return saeLinkResourceRepository.findByIdResource(resourceId);
    }

    public SAELinkResource createSAELinkResource(SAELinkResource saeLinkResource) {
        return saeLinkResourceRepository.save(saeLinkResource);
    }

    public void deleteSAELinkResource(SAELinkResource.SAELinkResourceId id) {
        saeLinkResourceRepository.deleteById(id);
    }
}

