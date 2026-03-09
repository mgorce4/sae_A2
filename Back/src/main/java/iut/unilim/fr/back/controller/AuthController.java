package iut.unilim.fr.back.controller;

import iut.unilim.fr.back.entity.UserSyncadia;
import iut.unilim.fr.back.payload.request.LoginRequest;
import iut.unilim.fr.back.payload.response.JwtResponse;
import iut.unilim.fr.back.repository.UserSyncadiaRepository;
import iut.unilim.fr.back.security.JwtUtils;
import iut.unilim.fr.back.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserSyncadiaRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * POST /api/auth/signin
     * Authenticates the user and returns a JWT token along with user info and access rights.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UserSyncadia user = userRepository.findById(userDetails.getId()).orElseThrow();

            // Générer le JWT avec toutes les infos utilisateur embarquées dans le payload
            String jwt = jwtUtils.generateJwtToken(authentication, user);

            List<Integer> accessRights = userDetails.getAuthorities().stream()
                    .map(item -> Integer.parseInt(item.getAuthority().replace("ROLE_", "")))
                    .collect(Collectors.toList());


            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    user.getFirstname(),
                    user.getLastname(),
                    accessRights,
                    user.getInstitution().getIdInstitution(),
                    user.getInstitution().getName(),
                    user.getInstitution().getLocation()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Identifiant ou mot de passe incorrect"));
        }
    }
}
