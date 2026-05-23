package cl.fuelonline.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security configuration loaded from application.yml under "app.security".
 *
 * Operating modes:
 *  - firebase.credentials-path con value: usa Firebase Admin SDK para validar ID Tokens
 *  - dev-mode = true: acepta header "X-Dev-User: email@dominio" para impersonar
 *    al user local (sin verify firma). Util para desarrollo sin Firebase.
 *
 * If both are off, all non-anonymous requests result in 401.
 */
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        Firebase firebase,
        boolean devMode,
        String devUserHeader
) {
    public SecurityProperties {
        if (firebase == null)        firebase = new Firebase(null);
        if (devUserHeader == null || devUserHeader.isBlank()) devUserHeader = "X-Dev-User";
    }

    public record Firebase(String credentialsPath) {
        public boolean enabled() {
            return credentialsPath != null && !credentialsPath.isBlank();
        }
    }
}
