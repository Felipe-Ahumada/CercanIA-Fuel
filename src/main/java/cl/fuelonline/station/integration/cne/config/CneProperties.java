package cl.fuelonline.station.integration.cne.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration of the CNE API integration (api.cne.cl).
 *
 *  - enabled            : if false, neither the client nor the scheduler are instantiated
 *  - api-url            : CNE API base URL (no trailing slash)
 *  - stations-path    : relative path of the stations endpoint
 *  - token              : Bearer token (DO NOT commit, use the env var CNE_API_TOKEN)
 *  - timeout            : total HTTP timeout per request
 *  - scheduled-enabled  : si false, no corre @Scheduled (manual via POST sigue funcionando)
 *  - scheduled-cron     : Spring cron expression (6 fields)
 */
@ConfigurationProperties(prefix = "app.cne")
public record CneProperties(
        boolean enabled,
        String apiUrl,
        String stationsPath,
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
        if (timeout == null) timeout = Duration.ofSeconds(60);
        if (scheduledCron == null || scheduledCron.isBlank())
            scheduledCron = "0 0 */1 * * *";
    }

    public boolean tokenConfigured() {
        return token != null && !token.isBlank();
    }
}
