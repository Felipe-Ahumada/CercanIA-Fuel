package cl.fuelonline.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.security.jwt")
public record JwtProperties(
        String secret,
        @DefaultValue("86400") long expirationSeconds,
        @DefaultValue("cercania-fuel") String issuer
) {}
