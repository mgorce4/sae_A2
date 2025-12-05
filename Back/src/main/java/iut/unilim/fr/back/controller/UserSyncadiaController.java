package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.service.UserSyncadiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserSyncadiaController {
    @Autowired
    private UserSyncadiaService userSyncadiaService;

    @GetMapping
    public List<UserSyncadia> getAllUsers() {
        return userSyncadiaService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSyncadia> getUserById(@PathVariable Long id) {
        return userSyncadiaService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserSyncadia> getUserByUsername(@PathVariable String username) {
        return userSyncadiaService.getUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserSyncadia createUser(@RequestBody UserSyncadia user) {
        return userSyncadiaService.createUser(user);
    }

    @PutMapping("/{id}")
    public UserSyncadia updateUser(@PathVariable Long id, @RequestBody UserSyncadia user) {
        return userSyncadiaService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userSyncadiaService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

