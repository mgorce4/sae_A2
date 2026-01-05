package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.TeacherHours;
import iut.unilim.fr.back.service.TeacherHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-hours")
@CrossOrigin(origins = "*")
public class TeacherHoursController {
    @Autowired
    private TeacherHoursService teacherHoursService;

    @GetMapping
    public List<TeacherHours> getAllTeacherHours() {
        return teacherHoursService.getAllTeacherHours();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherHours> getTeacherHoursById(@PathVariable Long id) {
        return teacherHoursService.getTeacherHoursById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource-sheet/{idResourceSheet}")
    public List<TeacherHours> getTeacherHoursByResourceSheetId(@PathVariable Long idResourceSheet) {
        return teacherHoursService.getTeacherHoursByResourceSheetId(idResourceSheet);
    }

    @PostMapping
    public TeacherHours createTeacherHours(@RequestBody TeacherHours teacherHours) {
        return teacherHoursService.createTeacherHours(teacherHours);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherHours> updateTeacherHours(@PathVariable Long id, @RequestBody TeacherHours teacherHoursDetails) {
        try {
            TeacherHours updatedTeacherHours = teacherHoursService.updateTeacherHours(id, teacherHoursDetails);
            return ResponseEntity.ok(updatedTeacherHours);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacherHours(@PathVariable Long id) {
        teacherHoursService.deleteTeacherHours(id);
        return ResponseEntity.noContent().build();
    }
}

