package cl.fuelonline.shared.security;

import cl.fuelonline.security.config.JwtProperties;
import cl.fuelonline.security.config.SecurityProperties;
import cl.fuelonline.security.infrastructure.AuthFilter;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({SecurityProperties.class, JwtProperties.class})
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(eh -> eh
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/swagger-ui/**", "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health", "/actuator/info",
                        "/h2-console/**",
                        "/error").permitAll()

                .requestMatchers("/api/v1/auth/me").authenticated()

                .requestMatchers(HttpMethod.POST,
                        "/api/v1/auth/register",
                        "/api/v1/auth/login",
                        "/api/v1/usuarios").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/usuarios/complete-profile").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/v1/vehiculos/**").permitAll()

                .requestMatchers(HttpMethod.GET,
                        "/api/v1/bencineras/**",
                        "/api/v1/regiones/**",
                        "/api/v1/comunas/**",
                        "/api/v1/marcas/**",
                        "/api/v1/tipos-combustible/**",
                        "/api/v1/precios/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/v1/descuentos/catalogo").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/descuentos/**").permitAll()

                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/usuarios").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST,   "/api/v1/bancos/**",
                                                   "/api/v1/tarjetas-producto/**",
                                                   "/api/v1/descuentos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/bancos/**",
                                                   "/api/v1/tarjetas-producto/**",
                                                   "/api/v1/descuentos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/bancos/**",
                                                   "/api/v1/tarjetas-producto/**",
                                                   "/api/v1/descuentos/**").hasRole("ADMIN")

                .anyRequest().authenticated())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
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
