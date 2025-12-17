package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.TeachersForResource;
import iut.unilim.fr.back.service.TeachersForResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers-for-resource")
@CrossOrigin(origins = "*")
public class TeachersForResourceController {
    @Autowired
    private TeachersForResourceService teachersForResourceService;

    @GetMapping
    public List<TeachersForResource> getAllTeachersForResource() {
        return teachersForResourceService.getAllTeachersForResources();
    }

    @GetMapping("/user/{userId}")
    public List<TeachersForResource> getTeachersByUserId(@PathVariable Long userId) {
        return teachersForResourceService.getTeachersByUserId(userId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<TeachersForResource> getTeachersByResourceId(@PathVariable Long resourceId) {
        return teachersForResourceService.getTeachersByResourceId(resourceId);
    }

    @PostMapping
    public TeachersForResource createTeachersForResource(@RequestBody TeachersForResource teachersForResource) {
        return teachersForResourceService.createTeachersForResource(teachersForResource);
    }

    @DeleteMapping("/user/{userId}/resource/{resourceId}")
    public ResponseEntity<Void> deleteTeachersForResource(@PathVariable Long userId, @PathVariable Long resourceId) {
        TeachersForResource.TeachersForResourceId id = new TeachersForResource.TeachersForResourceId(userId, resourceId);
        teachersForResourceService.deleteTeachersForResource(id);
        return ResponseEntity.noContent().build();
    }
}

