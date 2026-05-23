package cl.fuelonline.station.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CneDistributorDto(
        @JsonProperty("marca") String brand,
        @JsonProperty("logo")  String logo
) {}
