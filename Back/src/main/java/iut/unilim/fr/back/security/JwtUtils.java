package iut.unilim.fr.back.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${syncadia.app.jwtSecret}")
    private String jwtSecret;

    @Value("${syncadia.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication, iut.unilim.fr.back.entity.UserSyncadia user) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        List<Integer> roles = userPrincipal.getAuthorities().stream()
                .map(a -> Integer.parseInt(a.getAuthority().replace("ROLE_", "")))
                .collect(Collectors.toList());

        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles)
                .claim("id", userPrincipal.getId())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs));

        if (user != null) {
            builder.claim("firstname", user.getFirstname())
                   .claim("lastname", user.getLastname())
                   .claim("idInstitution", user.getInstitution() != null ? user.getInstitution().getIdInstitution() : null)
                   .claim("institutionName", user.getInstitution() != null ? user.getInstitution().getName() : null)
                   .claim("institutionLocation", user.getInstitution() != null ? user.getInstitution().getLocation() : null);
        }

        return builder.signWith(key()).compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
