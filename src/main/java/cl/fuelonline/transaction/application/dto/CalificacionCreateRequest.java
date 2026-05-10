package cl.fuelonline.transaction.application.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CalificacionCreateRequest(

        @NotNull UUID usuarioId,

        @NotNull UUID bencineraId,

        @NotNull @Min(1) @Max(5) Integer puntaje,

        @Size(max = 500) String comentario
) {}
