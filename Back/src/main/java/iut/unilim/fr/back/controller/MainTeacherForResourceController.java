package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.MainTeacherForResource;
import iut.unilim.fr.back.service.MainTeacherForResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/main-teachers-for-resource")
@CrossOrigin(origins = "*")
public class MainTeacherForResourceController {
    @Autowired
    private MainTeacherForResourceService mainTeacherForResourceService;

    @GetMapping
    public List<MainTeacherForResource> getAllMainTeachersForResource() {
        return mainTeacherForResourceService.getAllMainTeacherForResources();
    }

    @GetMapping("/user/{userId}")
    public List<MainTeacherForResource> getMainTeachersByUserId(@PathVariable Long userId) {
        return mainTeacherForResourceService.getMainTeachersByUserId(userId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<MainTeacherForResource> getMainTeachersByResourceId(@PathVariable Long resourceId) {
        return mainTeacherForResourceService.getMainTeachersByResourceId(resourceId);
    }

    @PostMapping
    public MainTeacherForResource createMainTeacherForResource(@RequestBody MainTeacherForResource mainTeacherForResource) {
        return mainTeacherForResourceService.createMainTeacherForResource(mainTeacherForResource);
    }

    @DeleteMapping("/user/{userId}/resource/{resourceId}")
    public ResponseEntity<Void> deleteMainTeacherForResource(@PathVariable Long userId, @PathVariable Long resourceId) {
        MainTeacherForResource.MainTeacherForResourceId id = new MainTeacherForResource.MainTeacherForResourceId(userId, resourceId);
        mainTeacherForResourceService.deleteMainTeacherForResource(id);
        return ResponseEntity.noContent().build();
    }
}

