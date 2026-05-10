package cl.bencinaenlinea.bencinera.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CneUbicacionDto(
        @JsonProperty("nombre_region")  String nombreRegion,
        @JsonProperty("codigo_region")  String codigoRegion,
        @JsonProperty("nombre_comuna")  String nombreComuna,
        @JsonProperty("codigo_comuna")  String codigoComuna,
        String direccion,
        String latitud,
        String longitud
) {}
