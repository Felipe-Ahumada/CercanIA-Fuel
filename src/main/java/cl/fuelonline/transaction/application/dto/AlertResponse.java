package cl.fuelonline.transaction.application.dto;

import cl.fuelonline.transaction.domain.model.AlertType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlertResponse(
        Long id,
        UUID usuarioId,
        UUID bencineraId,
        String bencineraNombre,
        AlertType tipoAlerta,
        String titulo,
        String mensaje,
        Boolean leida,
        LocalDateTime leidaAt,
        LocalDateTime createdAt
) {}
