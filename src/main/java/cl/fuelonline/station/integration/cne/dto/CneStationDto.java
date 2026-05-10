package cl.fuelonline.station.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Representa una station devuelta por GET /api/v4/stations.
 * Solo mapeamos los fields que usamos; el resto se ignora.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CneStationDto(
        String                            code,
        @JsonProperty("en_mantenimiento") Integer inMaintenance,
        @JsonProperty("razon_social")     String legalName,
        @JsonProperty("horario_atencion") String businessHours,
        CneDistributorDto                distributor,
        CneLocationDto                   location,
        /** Llaves dinamicas: "93", "95", "97", "DI", "GLP", "KE", etc. */
        Map<String, CnePriceDto>         prices
) {

    public boolean inMaintenanceBool() {
        return inMaintenance != null && inMaintenance == 1;
    }
}
