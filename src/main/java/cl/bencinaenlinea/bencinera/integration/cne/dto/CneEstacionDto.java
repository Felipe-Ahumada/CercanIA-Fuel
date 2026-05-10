package cl.bencinaenlinea.bencinera.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Representa una estacion devuelta por GET /api/v4/estaciones.
 * Solo mapeamos los campos que usamos; el resto se ignora.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CneEstacionDto(
        String                            codigo,
        @JsonProperty("en_mantenimiento") Integer enMantenimiento,
        @JsonProperty("razon_social")     String razonSocial,
        @JsonProperty("horario_atencion") String horarioAtencion,
        CneDistribuidorDto                distribuidor,
        CneUbicacionDto                   ubicacion,
        /** Llaves dinamicas: "93", "95", "97", "DI", "GLP", "KE", etc. */
        Map<String, CnePrecioDto>         precios
) {

    public boolean enMantenimientoBool() {
        return enMantenimiento != null && enMantenimiento == 1;
    }
}
