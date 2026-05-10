package cl.fuelonline.station.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CneDistribuidorDto(
        String marca,
        String logo
) {}
