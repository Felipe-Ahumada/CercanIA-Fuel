package cl.fuelonline.transaction.application.dto;

import cl.fuelonline.transaction.domain.model.TipoAlerta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record AlertaCreateRequest(

        @NotNull UUID usuarioId,

        @Schema(description = "Bencinera asociada (opcional)")
        UUID bencineraId,

        @NotNull TipoAlerta tipoAlerta,

        @NotBlank @Size(max = 150) String titulo,

        @NotBlank @Size(max = 500) String mensaje
) {}
