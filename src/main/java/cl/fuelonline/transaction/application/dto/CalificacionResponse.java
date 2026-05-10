package cl.fuelonline.transaction.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalificacionResponse(
        Long id,
        UUID usuarioId,
        String usuarioNombre,
        UUID bencineraId,
        String bencineraNombre,
        Integer puntaje,
        String comentario,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
