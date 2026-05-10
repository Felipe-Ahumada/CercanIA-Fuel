package cl.fuelonline.transaction.application.dto;

import java.util.UUID;

public record CalificacionResumenResponse(
        UUID bencineraId,
        Double promedio,
        Long total
) {}
