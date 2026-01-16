package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.Path;
import iut.unilim.fr.back.service.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/paths")
@CrossOrigin(origins = "*")
public class PathController {
    @Autowired
    private PathService pathService;

    @GetMapping
    public List<Path> getAllPaths() {
        return pathService.getAllPaths();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Path> getPathById(@PathVariable Long id) {
        return pathService.getPathById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Path createPath(@RequestBody Path path) {
        return pathService.createPath(path);
    }

    @PutMapping("/{id}")
    public Path updatePath(@PathVariable Long id, @RequestBody Path path) {
        return pathService.updatePath(id, path);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePath(@PathVariable Long id) {
        try {
            pathService.deletePath(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error deleting path " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

