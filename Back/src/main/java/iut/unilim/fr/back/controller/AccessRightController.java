package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.AccessRight;
import iut.unilim.fr.back.service.AccessRightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/access-rights")
@CrossOrigin(origins = "*")
public class AccessRightController {
    @Autowired
    private AccessRightService accessRightService;

    @GetMapping
    public List<AccessRight> getAllAccessRights() {
        return accessRightService.getAllAccessRights();
    }

    @GetMapping("/user/{userId}")
    public List<AccessRight> getAccessRightsByUserId(@PathVariable Long userId) {
        return accessRightService.getAccessRightsByUserId(userId);
    }

    @PostMapping
    public AccessRight createAccessRight(@RequestBody AccessRight accessRight) {
        return accessRightService.createAccessRight(accessRight);
    }

    @DeleteMapping("/{accessRight}/{userId}")
    public ResponseEntity<Void> deleteAccessRight(@PathVariable Integer accessRight, @PathVariable Long userId) {
        accessRightService.deleteAccessRight(new AccessRight.AccessRightId(accessRight, userId));
        return ResponseEntity.noContent().build();
    }
}

