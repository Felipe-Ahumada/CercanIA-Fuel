package cl.fuelonline.station.integration.cne.client;

import cl.fuelonline.station.integration.cne.config.CneProperties;
import cl.fuelonline.station.integration.cne.dto.CneStationDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

/**
 * HTTP client for the CNE API. Only instantiated when app.cne.enabled = true.
 *
 * Robust strategy: reads the response as a String and detects whether it arrives as
 * a direct array "[ ... ]" or as a wrapper "{ data: [...] }". Some APIs
 * (incluso la misma) cambian la forma segun version o errors.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.cne", name = "enabled", havingValue = "true")
public class CneApiClient {

    private static final TypeReference<List<CneStationDto>> LIST_TYPE = new TypeReference<>() {};

    private final RestClient restClient;
    private final CneProperties props;
    private final ObjectMapper mapper;

    public CneApiClient(@Qualifier("cneRestClient") RestClient restClient,
                        CneProperties props,
                        ObjectMapper mapper) {
        this.restClient = restClient;
        this.props = props;
        this.mapper = mapper;
    }

    /**
     * GET /api/v4/stations — devuelve todas las stations con sus prices actuales.
     */
    public List<CneStationDto> getStations() {
        if (!props.tokenConfigured()) {
            log.warn("CNE: token not configured (app.cne.token empty). Sync aborted.");
            return Collections.emptyList();
        }
        log.info("CNE: requesting stations from {}{}", props.apiUrl(), props.stationsPath());

        String body;
        try {
            body = restClient.get()
                    .uri(props.stationsPath())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException ex) {
            log.error("CNE: failed to query stations: {}", ex.getMessage());
            throw ex;
        }

        if (body == null || body.isBlank()) {
            log.warn("CNE: empty response");
            return Collections.emptyList();
        }

        body = body.trim();
        log.debug("CNE: response size={} bytes, first 200 chars: {}",
                body.length(),
                body.length() > 200 ? body.substring(0, 200) : body);

        try {
            if (body.startsWith("[")) {
                List<CneStationDto> list = mapper.readValue(body, LIST_TYPE);
                log.info("CNE: recibidas {} stations (array directo)", list.size());
                return list;
            }
            if (body.startsWith("{")) {
                JsonNode root = mapper.readTree(body);
                // Probar wrappers comunes: data, stations, results, items
                String[] possibleFields = {"data", "stations", "results", "items"};
                for (String field : possibleFields) {
                    JsonNode arr = root.path(field);
                    if (arr.isArray()) {
                        List<CneStationDto> list = mapper.readerFor(LIST_TYPE).readValue(arr);
                        log.info("CNE: recibidas {} stations (wrapper '{}')", list.size(), field);
                        return list;
                    }
                }
                // If no array is found, log the body for diagnostics
                log.error("CNE: response is an object but does not contain a recognizable array. "
                        + "Body: {}", body.length() > 500 ? body.substring(0, 500) + "..." : body);
                return Collections.emptyList();
            }
            log.error("CNE: unrecognized response format. First 200 chars: {}",
                    body.length() > 200 ? body.substring(0, 200) : body);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("CNE: error parsing response: {}", ex.getMessage());
            throw new RuntimeException("Error parsing CNE response", ex);
        }
    }
}
