package cl.fuelonline.chat.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 2000) String prompt,
        Double latitude,
        Double longitude
) {}
