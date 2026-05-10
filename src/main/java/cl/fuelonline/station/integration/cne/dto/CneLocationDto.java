package cl.fuelonline.station.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CneLocationDto(
        @JsonProperty("nombre_region")  String regionName,
        @JsonProperty("codigo_region")  String regionCode,
        @JsonProperty("nombre_comuna")  String communeName,
        @JsonProperty("codigo_comuna")  String communeCode,
        String address,
        String latitude,
        String longitude
) {}
