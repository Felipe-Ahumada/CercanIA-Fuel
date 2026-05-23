package cl.fuelonline.security.application.dto;

import java.util.Collection;
import java.util.UUID;

public record MeResponse(
        UUID userId,
        String email,
        String firebaseUid,
        String role,
        Collection<String> authorities
) {}
