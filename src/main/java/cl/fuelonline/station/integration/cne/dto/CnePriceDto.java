package cl.fuelonline.station.integration.cne.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CnePriceDto(
        @JsonProperty("charge_unit")        String chargeUnit,
        String                                price,
        @JsonProperty("fecha_actualizacion") String updateDate,
        @JsonProperty("hora_actualizacion")  String updateTime,
        @JsonProperty("attention_type")       String attentionType
) {}
