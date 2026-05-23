package cl.fuelonline.security.application.service;

import cl.fuelonline.security.config.JwtProperties;
import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.domain.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    private static final String CLAIM_ROLE  = "role";
    private static final String CLAIM_EMAIL = "email";

    private final JwtProperties props;
    private final SecretKey key;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLE, user.getRole() != null ? user.getRole().getName() : "USER")
                .issuer(props.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.expirationSeconds())))
                .signWith(key)
                .compact();
    }

    public AuthenticatedUser parseUser(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!props.issuer().equals(claims.getIssuer())) {
            throw new JwtException("Token issuer mismatch");
        }

        return new AuthenticatedUser(
                UUID.fromString(claims.getSubject()),
                null,
                claims.get(CLAIM_EMAIL, String.class),
                claims.get(CLAIM_ROLE, String.class));
    }

    /** Peek at the issuer without verifying the signature. */
    public boolean isLocalToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            String json = new String(payload, StandardCharsets.UTF_8);
            return json.contains("\"iss\":\"" + props.issuer() + "\"");
        } catch (Exception e) {
            return false;
        }
    }
}
