package cl.fuelonline.user.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BankConvenioItem(
        @JsonProperty("bank")          @NotBlank @Size(max = 100) String bank,
        @JsonProperty("cardType")      @NotBlank @Size(max = 50)  String cardType,
        @JsonProperty("cardProductId")                            Integer cardProductId
) {}
