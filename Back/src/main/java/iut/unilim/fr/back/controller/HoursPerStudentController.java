package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.HoursPerStudent;
import iut.unilim.fr.back.service.HoursPerStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hours-per-student")
@CrossOrigin(origins = "*")
public class HoursPerStudentController {
    @Autowired
    private HoursPerStudentService hoursPerStudentService;

    @GetMapping
    public List<HoursPerStudent> getAllHoursPerStudent() {
        return hoursPerStudentService.getAllHoursPerStudent();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HoursPerStudent> getHoursPerStudentById(@PathVariable Long id) {
        return hoursPerStudentService.getHoursPerStudentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    public List<HoursPerStudent> getHoursPerStudentByResourceId(@PathVariable Long resourceId) {
        return hoursPerStudentService.getHoursPerStudentByResourceId(resourceId);
    }

    @PostMapping
    public HoursPerStudent createHoursPerStudent(@RequestBody HoursPerStudent hoursPerStudent) {
        return hoursPerStudentService.createHoursPerStudent(hoursPerStudent);
    }

    @PutMapping("/{id}")
    public HoursPerStudent updateHoursPerStudent(@PathVariable Long id, @RequestBody HoursPerStudent hoursPerStudent) {
        return hoursPerStudentService.updateHoursPerStudent(id, hoursPerStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoursPerStudent(@PathVariable Long id) {
        hoursPerStudentService.deleteHoursPerStudent(id);
        return ResponseEntity.noContent().build();
    }
}

