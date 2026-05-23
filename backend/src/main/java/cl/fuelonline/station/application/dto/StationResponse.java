package cl.fuelonline.station.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Detailed view of a station")
public record StationResponse(
        UUID id,
        String apiCode,
        Integer brandId,
        String brandName,
        Integer communeId,
        String communeName,
        Integer regionId,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        String phone,
        String email,
        Boolean inMaintenance,
        Boolean active,
        LocalDateTime syncAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CurrentPriceResponse> prices
) {}
