package cl.fuelonline.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion de seguridad cargada desde application.yml bajo "app.security".
 *
 * Modos de operacion:
 *  - firebase.credentials-path con valor: usa Firebase Admin SDK para validar ID Tokens
 *  - dev-mode = true: acepta header "X-Dev-User: email@dominio" para impersonar
 *    al usuario local (sin verificar firma). Util para desarrollo sin Firebase.
 *
 * Si ambos estan apagados, todas las requests no anonimas resultan en 401.
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
