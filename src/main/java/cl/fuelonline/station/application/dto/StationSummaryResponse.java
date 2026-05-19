package cl.fuelonline.station.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Vista compacta de station para listados y mapa")
public record StationSummaryResponse(
        UUID id,
        String name,
        Integer brandId,
        String brand,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean inMaintenance,
        @Schema(description = "Distance to the search point in km. Null if the query did not specify an origin.")
        Double distanciaKm,
        @Schema(description = "Current prices per fuel type. Empty list if no prices have been synced yet.")
        List<CurrentPriceResponse> prices
) {}
