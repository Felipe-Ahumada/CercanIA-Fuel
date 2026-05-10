package cl.fuelonline.transaction.application.dto;

import cl.fuelonline.transaction.domain.model.TipoAlerta;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertaResponse(
        Long id,
        UUID usuarioId,
        UUID bencineraId,
        String bencineraNombre,
        TipoAlerta tipoAlerta,
        String titulo,
        String mensaje,
        Boolean leida,
        LocalDateTime leidaAt,
        LocalDateTime createdAt
) {}
