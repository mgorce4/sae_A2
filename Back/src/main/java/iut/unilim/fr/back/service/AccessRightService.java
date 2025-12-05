package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.AccessRight;
import iut.unilim.fr.back.repository.AccessRightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccessRightService {
    @Autowired
    private AccessRightRepository accessRightRepository;

    public List<AccessRight> getAllAccessRights() {
        return accessRightRepository.findAll();
    }

    public Optional<AccessRight> getAccessRightById(AccessRight.AccessRightId id) {
        return accessRightRepository.findById(id);
    }

    public List<AccessRight> getAccessRightsByUserId(Long userId) {
        return accessRightRepository.findByIdUser(userId);
    }

    public AccessRight createAccessRight(AccessRight accessRight) {
        return accessRightRepository.save(accessRight);
    }

    public void deleteAccessRight(AccessRight.AccessRightId id) {
        accessRightRepository.deleteById(id);
    }
}

