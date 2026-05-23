package cl.fuelonline.station.integration.cne.client;

import cl.fuelonline.station.integration.cne.config.CneProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages the short-lived CNE API token (1 hour expiry).
 *
 * Auth endpoint: POST https://api.cne.cl/api/login
 * Content-Type:  application/x-www-form-urlencoded
 * Body:          email=<email>&password=<password>
 *
 * The token is cached and refreshed automatically 5 minutes before expiry.
 * Thread-safe via ReentrantLock (prevents double refresh on concurrent calls).
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.cne", name = "enabled", havingValue = "true")
public class CneTokenProvider {

    /** Refresh the token this many seconds before it actually expires. */
    private static final long REFRESH_BUFFER_SECONDS = 300;

    private final CneProperties props;
    private final RestClient authClient;
    private final ReentrantLock lock = new ReentrantLock();

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public CneTokenProvider(CneProperties props, RestClient.Builder builder) {
        this.props = props;
        // Auth client points directly to the full auth URL (different host/path than /api/v4)
        this.authClient = builder.build();
    }

    /**
     * Returns a valid Bearer token.
     * - If credentials are configured: refreshes automatically when the cached
     *   token is about to expire (< 5 min remaining).
     * - If only a static token is configured: returns it as-is (no refresh).
     *
     * @throws IllegalStateException if neither credentials nor a static token are configured.
     */
    public String getToken() {
        if (!props.credentialsConfigured()) {
            if (props.tokenConfigured()) {
                return props.token();
            }
            throw new IllegalStateException(
                    "CNE: neither credentials (CNE_EMAIL/CNE_PASSWORD) nor a "
                    + "static token (CNE_API_TOKEN) are configured.");
        }

        if (isValid()) return cachedToken;

        lock.lock();
        try {
            if (isValid()) return cachedToken; // re-check after acquiring lock
            return refresh();
        } finally {
            lock.unlock();
        }
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private boolean isValid() {
        return cachedToken != null
                && Instant.now().isBefore(expiresAt.minusSeconds(REFRESH_BUFFER_SECONDS));
    }

    private String refresh() {
        log.info("CNE: requesting fresh token from {}", props.authUrl());

        // POST email=...&password=... (application/x-www-form-urlencoded)
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("email",    props.email());
        form.add("password", props.password());

        try {
            TokenResponse resp = authClient.post()
                    .uri(props.authUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);

            if (resp == null || resp.resolvedToken() == null) {
                throw new IllegalStateException("CNE auth returned empty token");
            }

            cachedToken = resp.resolvedToken();
            long ttl = resp.expiresIn() != null ? resp.expiresIn() : 3600L;
            expiresAt = Instant.now().plusSeconds(ttl);

            log.info("CNE: token obtained, valid for {} s", ttl);
            return cachedToken;

        } catch (RestClientException ex) {
            log.error("CNE: auth request failed: {}", ex.getMessage());
            throw new RuntimeException(
                    "CNE auth failed — check CNE_EMAIL / CNE_PASSWORD", ex);
        }
    }

    // ── response record ───────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(
            // The CNE API may return the token under different field names
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token")        String token,
            @JsonProperty("expires_in")   Long expiresIn
    ) {
        String resolvedToken() {
            return accessToken != null ? accessToken : token;
        }
    }
}
