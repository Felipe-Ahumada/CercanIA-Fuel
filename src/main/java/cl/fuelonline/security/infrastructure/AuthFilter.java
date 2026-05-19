package cl.fuelonline.security.infrastructure;

import cl.fuelonline.security.application.exception.AuthenticationFailedException;
import cl.fuelonline.security.application.service.AuthenticationService;
import cl.fuelonline.security.application.service.FirebaseTokenService;
import cl.fuelonline.security.application.service.JwtService;
import cl.fuelonline.security.config.SecurityProperties;
import cl.fuelonline.security.domain.AuthenticatedUser;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Maneja dos tipos de token en Authorization: Bearer:
 *  1. JWT local (issuer = "cercania-fuel"): login con email/password nativo.
 *  2. Firebase ID token: Google Sign-In.
 * También acepta X-Dev-User para impersonación en dev.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final SecurityProperties props;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private FirebaseTokenService firebaseTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            AuthenticatedUser user = resolveUser(request);
            if (user != null) {
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.authorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (AuthenticationFailedException ex) {
            SecurityContextHolder.clearContext();
            log.debug("Auth failed: {}", ex.getMessage());
            writeUnauthorized(response, ex.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }

    private AuthenticatedUser resolveUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER)) {
            String token = header.substring(BEARER.length()).trim();
            return jwtService.isLocalToken(token)
                    ? resolveLocalJwt(token)
                    : resolveFirebaseToken(token);
        }
        return resolveDevHeader(request);
    }

    private AuthenticatedUser resolveLocalJwt(String token) {
        try {
            return jwtService.parseUser(token);
        } catch (JwtException ex) {
            throw new AuthenticationFailedException("Token inválido o expirado: " + ex.getMessage());
        }
    }

    private AuthenticatedUser resolveFirebaseToken(String token) {
        if (firebaseTokenService == null) {
            throw new AuthenticationFailedException("Firebase no está configurado en este entorno.");
        }
        try {
            FirebaseToken firebaseToken = firebaseTokenService.verify(token);
            try {
                return authenticationService.resolveFromFirebase(
                        firebaseToken.getUid(), firebaseToken.getEmail());
            } catch (AuthenticationFailedException ex) {
                // The Firebase token is cryptographically valid, but no backend
                // user exists yet (new Google Sign-In before completing profile).
                // Return null so the request proceeds as anonymous — permitAll()
                // endpoints like POST /api/v1/usuarios will accept it; protected
                // endpoints will get the standard Spring Security 401.
                log.debug("Firebase user not in backend yet: {}", ex.getMessage());
                return null;
            }
        } catch (FirebaseAuthException ex) {
            throw new AuthenticationFailedException("Token Firebase inválido o expirado", ex);
        }
    }

    private AuthenticatedUser resolveDevHeader(HttpServletRequest request) {
        if (!props.devMode()) return null;
        String email = request.getHeader(props.devUserHeader());
        if (email == null || email.isBlank()) return null;
        return authenticationService.resolveFromEmail(email);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":401,\"error\":\"Unauthorized\",\"detail\":\""
                + message.replace("\"", "'") + "\"}");
    }
}
