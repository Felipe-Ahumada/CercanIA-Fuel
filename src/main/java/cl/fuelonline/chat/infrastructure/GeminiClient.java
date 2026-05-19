package cl.fuelonline.chat.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestClient restClient;
    private final String apiKey;

    public GeminiClient(
            RestClient.Builder builder,
            @Value("${app.gemini.api-key}") String apiKey) {
        this.restClient = builder.build();
        this.apiKey = apiKey;
    }

    /**
     * Calls Gemini. Retries up to 3 times with exponential backoff on transient
     * rate limits (RPM exceeded). If the quota is permanently 0 (billing issue),
     * the body contains "limit: 0" and we throw a non-retryable exception instead.
     */
    @Retryable(
            retryFor = GeminiRateLimitException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 20_000, multiplier = 2)
    )
    public String generate(String systemPrompt, String userMessage) {
        var body = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))
                ),
                "contents", List.of(
                        Map.of("role", "user",
                               "parts", List.of(Map.of("text", userMessage)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 1024
                )
        );

        try {
            var response = restClient.post()
                    .uri(API_URL + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return extractText(response);

        } catch (HttpClientErrorException e) {
            log.error("Gemini error {} — body: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            int status = e.getStatusCode().value();
            if (status == 429) {
                String body429 = e.getResponseBodyAsString();
                if (body429.contains("\"limit\": 0") || body429.contains("\"limit\":0") || body429.contains("limit: 0")) {
                    return "El asistente no está disponible por un problema de configuración. Contacta al administrador.";
                }
                throw new GeminiRateLimitException("Gemini RPM exceeded, will retry");
            }
            if (status == 401 || status == 403) {
                return "El asistente no está disponible (API key inválida). Contacta al administrador.";
            }
            if (status == 404) {
                return "El asistente no está disponible (modelo no encontrado). Contacta al administrador.";
            }
            return "El asistente no está disponible en este momento. Intenta más tarde.";
        }
    }

    @Recover
    public String recoverFromRateLimit(GeminiRateLimitException e, String systemPrompt, String userMessage) {
        return "El asistente está muy ocupado en este momento. Por favor intenta en unos minutos.";
    }

    private String extractText(Map<?, ?> response) {
        try {
            var candidates = (List<?>) response.get("candidates");
            var first = (Map<?, ?>) candidates.get(0);
            var content = (Map<?, ?>) first.get("content");
            var parts = (List<?>) content.get("parts");
            var part = (Map<?, ?>) parts.get(0);
            return (String) part.get("text");
        } catch (Exception e) {
            return "Lo siento, no pude procesar tu consulta en este momento.";
        }
    }

    /** Marker exception for retryable rate limit errors (RPM exceeded). */
    static class GeminiRateLimitException extends RuntimeException {
        GeminiRateLimitException(String message) { super(message); }
    }
}
