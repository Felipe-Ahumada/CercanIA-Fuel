package cl.fuelonline.station.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Represents a station returned by GET /api/v4/estaciones.
 * Only the fields we use are mapped; the rest is ignored.
 * The CNE API returns the keys in Spanish, so each Java field declares
 * its corresponding @JsonProperty.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CneStationDto(
        @JsonProperty("codigo")           String code,
        @JsonProperty("en_mantenimiento") Integer inMaintenance,
        @JsonProperty("razon_social")     String legalName,
        @JsonProperty("horario_atencion") String businessHours,
        @JsonProperty("distribuidor")     CneDistributorDto distributor,
        @JsonProperty("ubicacion")        CneLocationDto    location,
        /** Dynamic keys: "93", "95", "97", "DI", "GLP", "KE", etc. */
        @JsonProperty("precios")          Map<String, CnePriceDto> prices
) {

    public boolean inMaintenanceBool() {
        return inMaintenance != null && inMaintenance == 1;
    }
}
