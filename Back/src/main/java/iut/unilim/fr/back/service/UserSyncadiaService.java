package iut.unilim.fr.back.service;

import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserSyncadiaService {
    @Autowired
    private UserSyncadiaRepository userSyncadiaRepository;

    public List<UserSyncadia> getAllUsers() {
        return userSyncadiaRepository.findAll();
    }

    public Optional<UserSyncadia> getUserById(Long id) {
        return userSyncadiaRepository.findById(id);
    }

    public Optional<UserSyncadia> getUserByUsername(String username) {
        return userSyncadiaRepository.findByUsername(username);
    }

    public UserSyncadia createUser(UserSyncadia user) {
        return userSyncadiaRepository.save(user);
    }

    public UserSyncadia updateUser(Long id, UserSyncadia user) {
        user.setIdUser(id);
        return userSyncadiaRepository.save(user);
    }

    public void deleteUser(Long id) {
        userSyncadiaRepository.deleteById(id);
    }
}

