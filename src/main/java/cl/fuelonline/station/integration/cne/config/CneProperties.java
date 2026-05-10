package cl.fuelonline.station.integration.cne.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuracion de la integracion con la API de la CNE (api.bencinaenlinea.cl).
 *
 *  - enabled            : si false, ni el cliente ni el scheduler se instancian
 *  - api-url            : base URL de la API CNE (sin slash final)
 *  - stations-path    : path relativo del endpoint de stations
 *  - token              : Bearer token (NO commitear, usar env var CNE_API_TOKEN)
 *  - timeout            : timeout total HTTP por request
 *  - scheduled-enabled  : si false, no corre @Scheduled (manual via POST sigue funcionando)
 *  - scheduled-cron     : expresion cron Spring (6 fields)
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
