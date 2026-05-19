package cl.fuelonline.station.integration.cne.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration of the CNE API integration (api.cne.cl).
 *
 *  - enabled            : if false, neither the client nor the scheduler are instantiated
 *  - api-url            : CNE API base URL for station data (no trailing slash)
 *  - stations-path      : relative path of the stations endpoint
 *  - auth-url           : full URL of the CNE login endpoint
 *  - email              : CNE account email (env: CNE_EMAIL)
 *  - password           : CNE account password (env: CNE_PASSWORD)
 *  - token              : static token fallback — only used if email/password are absent
 *  - timeout            : total HTTP timeout per request
 *  - scheduled-enabled  : si false, no corre @Scheduled (manual via POST sigue funcionando)
 *  - scheduled-cron     : Spring cron expression (6 fields)
 */
@ConfigurationProperties(prefix = "app.cne")
public record CneProperties(
        boolean enabled,
        String apiUrl,
        String stationsPath,
        String authUrl,
        String email,
        String password,
        String token,
        Duration timeout,
        boolean scheduledEnabled,
        String scheduledCron
) {
    public CneProperties {
        if (apiUrl == null || apiUrl.isBlank())
            apiUrl = "https://api.cne.cl/api/v4";
        if (stationsPath == null || stationsPath.isBlank())
            stationsPath = "/estaciones";
        if (authUrl == null || authUrl.isBlank())
            authUrl = "https://api.cne.cl/api/login";
        if (timeout == null) timeout = Duration.ofSeconds(60);
        if (scheduledCron == null || scheduledCron.isBlank())
            scheduledCron = "0 0 4 * * *";
    }

    /** Credential-based auth is preferred when email+password are present. */
    public boolean credentialsConfigured() {
        return email != null && !email.isBlank()
                && password != null && !password.isBlank();
    }

    /** Fallback: static token (backwards compat). */
    public boolean tokenConfigured() {
        return token != null && !token.isBlank();
    }
}
