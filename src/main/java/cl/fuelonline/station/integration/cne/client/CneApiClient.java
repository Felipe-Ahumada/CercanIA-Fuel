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
 * Cliente HTTP para la API CNE. Solo se instancia si app.cne.enabled = true.
 *
 * Estrategia robusta: lee la respuesta como String y detecta si viene como
 * array directo "[ ... ]" o como wrapper "{ data: [...] }". Algunas APIs
 * (incluso la misma) cambian la forma segun version o errores.
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
     * GET /api/v4/estaciones — devuelve todas las estaciones con sus precios actuales.
     */
    public List<CneStationDto> obtenerEstaciones() {
        if (!props.tokenConfigurado()) {
            log.warn("CNE: token no configurado (app.cne.token vacio). Sync abortado.");
            return Collections.emptyList();
        }
        log.info("CNE: solicitando estaciones a {}{}", props.apiUrl(), props.estacionesPath());

        String body;
        try {
            body = restClient.get()
                    .uri(props.estacionesPath())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException ex) {
            log.error("CNE: fallo al consultar estaciones: {}", ex.getMessage());
            throw ex;
        }

        if (body == null || body.isBlank()) {
            log.warn("CNE: respuesta vacia");
            return Collections.emptyList();
        }

        body = body.trim();
        log.debug("CNE: respuesta tamano={} bytes, primeros 200 chars: {}",
                body.length(),
                body.length() > 200 ? body.substring(0, 200) : body);

        try {
            if (body.startsWith("[")) {
                List<CneStationDto> lista = mapper.readValue(body, LIST_TYPE);
                log.info("CNE: recibidas {} estaciones (array directo)", lista.size());
                return lista;
            }
            if (body.startsWith("{")) {
                JsonNode root = mapper.readTree(body);
                // Probar wrappers comunes: data, estaciones, results, items
                String[] posiblesCampos = {"data", "estaciones", "results", "items"};
                for (String campo : posiblesCampos) {
                    JsonNode arr = root.path(campo);
                    if (arr.isArray()) {
                        List<CneStationDto> lista = mapper.readerFor(LIST_TYPE).readValue(arr);
                        log.info("CNE: recibidas {} estaciones (wrapper '{}')", lista.size(), campo);
                        return lista;
                    }
                }
                // Si no encontramos array, mostramos el cuerpo para diagnosticar
                log.error("CNE: la respuesta es un objeto pero no contiene un array reconocible. "
                        + "Cuerpo: {}", body.length() > 500 ? body.substring(0, 500) + "..." : body);
                return Collections.emptyList();
            }
            log.error("CNE: respuesta con formato no reconocido. Primeros 200 chars: {}",
                    body.length() > 200 ? body.substring(0, 200) : body);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("CNE: error parseando respuesta: {}", ex.getMessage());
            throw new RuntimeException("Error parseando respuesta CNE", ex);
        }
    }
}
