package cl.fuelonline.transaction.application.dto;

import jakarta.validation.constraints.*;

public record CalificacionUpdateRequest(

        @Min(1) @Max(5) Integer puntaje,

        @Size(max = 500) String comentario
) {}
