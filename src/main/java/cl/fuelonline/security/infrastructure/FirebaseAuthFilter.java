package cl.fuelonline.security.infrastructure;

import cl.fuelonline.security.application.exception.AuthenticationFailedException;
import cl.fuelonline.security.application.service.AuthenticationService;
import cl.fuelonline.security.application.service.FirebaseTokenService;
import cl.fuelonline.security.config.SecurityProperties;
import cl.fuelonline.security.domain.AuthenticatedUser;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que extrae el token del header Authorization y autentica al usuario.
 *
 * Flujo:
 *  1. Si hay header "Authorization: Bearer <id-token>" y Firebase esta configurado,
 *     verifica el token con Firebase Admin SDK y resuelve al Usuario local.
 *  2. Si no, y dev-mode esta activo, busca el header X-Dev-User con el email
 *     del usuario a impersonar.
 *  3. Si nada de lo anterior, deja la request sin autenticar (puede ser endpoint publico).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final SecurityProperties props;
    private final AuthenticationService authenticationService;
    /** Inyeccion opcional via setter para que el filtro funcione sin Firebase configurado. */
    private FirebaseTokenService firebaseTokenService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setFirebaseTokenService(FirebaseTokenService firebaseTokenService) {
        this.firebaseTokenService = firebaseTokenService;
    }

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
            AuthenticatedUser user = autenticarBearer(request);
            if (user == null) {
                user = autenticarDev(request);
            }
            if (user != null) {
                AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user, null, user.authorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (AuthenticationFailedException ex) {
            SecurityContextHolder.clearContext();
            log.debug("Autenticacion fallida: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"detail\":\""
                    + ex.getMessage().replace("\"", "'") + "\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private AuthenticatedUser autenticarBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER)) return null;

        if (firebaseTokenService == null) {
            throw new AuthenticationFailedException(
                    "Firebase no esta configurado en este ambiente.");
        }

        String idToken = header.substring(BEARER.length()).trim();
        try {
            FirebaseToken token = firebaseTokenService.verificar(idToken);
            return authenticationService.resolverDesdeFirebase(token.getUid(), token.getEmail());
        } catch (FirebaseAuthException ex) {
            throw new AuthenticationFailedException("Token invalido o expirado", ex);
        }
    }

    private AuthenticatedUser autenticarDev(HttpServletRequest request) {
        if (!props.devMode()) return null;
        String email = request.getHeader(props.devUserHeader());
        if (email == null || email.isBlank()) return null;
        return authenticationService.resolverDesdeEmail(email);
    }
}
