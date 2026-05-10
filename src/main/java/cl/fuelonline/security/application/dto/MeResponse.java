package cl.fuelonline.security.application.dto;

import java.util.Collection;
import java.util.UUID;

public record MeResponse(
        UUID usuarioId,
        String email,
        String firebaseUid,
        String rol,
        Collection<String> authorities
) {}
