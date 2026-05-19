package cl.fuelonline.user.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BankProfileRequest(
        @JsonProperty("convenios") @NotNull @Valid List<BankConvenioItem> convenios
) {}
