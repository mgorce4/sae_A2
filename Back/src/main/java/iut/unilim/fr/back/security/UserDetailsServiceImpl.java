package iut.unilim.fr.back.security;

import iut.unilim.fr.back.entity.AccessRight;
import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.repository.AccessRightRepository;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserSyncadiaRepository userRepository;

    @Autowired
    AccessRightRepository accessRightRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserSyncadia user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<AccessRight> accessRights = accessRightRepository.findByIdUser(user.getIdUser());

        return UserDetailsImpl.build(user, accessRights);
    }
}
