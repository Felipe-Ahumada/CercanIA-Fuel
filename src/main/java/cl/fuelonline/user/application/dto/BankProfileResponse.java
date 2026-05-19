package cl.fuelonline.user.application.dto;

import java.util.List;
import java.util.UUID;

public record BankProfileResponse(
        UUID userId,
        List<BankConvenioItem> convenios
) {}
