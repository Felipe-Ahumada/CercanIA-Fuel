package cl.fuelonline.shared.security;

import cl.fuelonline.security.config.SecurityProperties;
import cl.fuelonline.security.infrastructure.FirebaseAuthFilter;
import cl.fuelonline.security.infrastructure.RestAccessDeniedHandler;
import cl.fuelonline.security.infrastructure.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuracion central de Spring Security.
 *
 * - Sesiones stateless (la API es REST con tokens, no usa sesiones HTTP).
 * - CSRF deshabilitado: no hay formularios server-side.
 * - CORS abierto (ajustar por ambiente cuando definamos dominios del frontend).
 * - FirebaseAuthFilter se intercala antes del UsernamePasswordAuthenticationFilter.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProperties.class)
@EnableMethodSecurity
public class SecurityConfig {

    private final FirebaseAuthFilter firebaseAuthFilter;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(eh -> eh
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                // Documentacion y health publicos
                .requestMatchers(
                        "/swagger-ui/**", "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health", "/actuator/info",
                        "/h2-console/**",
                        "/error").permitAll()

                // Autenticacion: el filtro decide si la peticion trae token; /me requiere auth
                .requestMatchers("/api/v1/auth/me").authenticated()

                // Registro publico de usuario
                .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()

                // Catalogos de bencineras (lectura publica)
                .requestMatchers(HttpMethod.GET,
                        "/api/v1/bencineras/**",
                        "/api/v1/regiones/**",
                        "/api/v1/comunas/**",
                        "/api/v1/marcas/**",
                        "/api/v1/tipos-combustible/**",
                        "/api/v1/precios/**").permitAll()

                // Lectura de descuentos publica; calculo y mutaciones requieren auth
                .requestMatchers(HttpMethod.GET, "/api/v1/descuentos/**").permitAll()

                // Endpoints administrativos solo ADMIN
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // Mutaciones de catalogos solo admin
                .requestMatchers(HttpMethod.POST,   "/api/v1/bancos/**",
                                                   "/api/v1/tarjetas-producto/**",
                                                   "/api/v1/descuentos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/bancos/**",
                                                   "/api/v1/tarjetas-producto/**",
                                                   "/api/v1/descuentos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/bancos/**",
                                                   "/api/v1/tarjetas-producto/**",
                                                   "/api/v1/descuentos/**").hasRole("ADMIN")

                // Resto requiere autenticacion
                .anyRequest().authenticated())
            .headers(h -> h.frameOptions(f -> f.sameOrigin())) // permite consola H2 en dev
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Location"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
