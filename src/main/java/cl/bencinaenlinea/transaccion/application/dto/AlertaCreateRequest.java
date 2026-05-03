package cl.bencinaenlinea.transaccion.application.dto;

import cl.bencinaenlinea.transaccion.domain.model.TipoAlerta;
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
