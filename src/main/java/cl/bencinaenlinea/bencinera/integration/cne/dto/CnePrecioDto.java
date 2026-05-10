package cl.bencinaenlinea.bencinera.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CnePrecioDto(
        @JsonProperty("unidad_cobro")        String unidadCobro,
        String                                precio,
        @JsonProperty("fecha_actualizacion") String fechaActualizacion,
        @JsonProperty("hora_actualizacion")  String horaActualizacion,
        @JsonProperty("tipo_atencion")       String tipoAtencion
) {}
