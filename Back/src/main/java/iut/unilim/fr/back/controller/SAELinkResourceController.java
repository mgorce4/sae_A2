package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.SAELinkResource;
import iut.unilim.fr.back.service.SAELinkResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sae-link-resources")
@CrossOrigin(origins = "*")
public class SAELinkResourceController {
    @Autowired
    private SAELinkResourceService saeLinkResourceService;

    @GetMapping
    public List<SAELinkResource> getAllSAELinkResources() {
        return saeLinkResourceService.getAllSAELinkResources();
    }

    @GetMapping("/sae/{saeId}")
    public List<SAELinkResource> getSAELinkResourcesBySAEId(@PathVariable Long saeId) {
        return saeLinkResourceService.getSAELinkResourcesBySAEId(saeId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<SAELinkResource> getSAELinkResourcesByResourceId(@PathVariable Long resourceId) {
        return saeLinkResourceService.getSAELinkResourcesByResourceId(resourceId);
    }

    @PostMapping
    public SAELinkResource createSAELinkResource(@RequestBody SAELinkResource saeLinkResource) {
        return saeLinkResourceService.createSAELinkResource(saeLinkResource);
    }

    @DeleteMapping("/sae/{saeId}/resource/{resourceId}")
    public ResponseEntity<Void> deleteSAELinkResource(@PathVariable Long saeId, @PathVariable Long resourceId) {
        SAELinkResource.SAELinkResourceId id = new SAELinkResource.SAELinkResourceId(saeId, resourceId);
        saeLinkResourceService.deleteSAELinkResource(id);
        return ResponseEntity.noContent().build();
    }
}

