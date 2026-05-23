package cl.fuelonline.station.api;

import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.application.dto.PriceHistoryResponse;
import cl.fuelonline.station.application.dto.PriceRegisterRequest;
import cl.fuelonline.station.application.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bencineras/{stationId}/precios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Prices", description = "Current prices and price history per station and fuel")
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    @Operation(summary = "Current prices for every fuel sold by the station")
    public List<CurrentPriceResponse> prices(@PathVariable UUID stationId) {
        return priceService.currentPricesOf(stationId);
    }

    @GetMapping("/{fuelTypeId}")
    @Operation(summary = "Current price of a specific fuel")
    public CurrentPriceResponse currentPrice(@PathVariable UUID stationId,
                                             @PathVariable @Positive Integer fuelTypeId) {
        return priceService.currentPrice(stationId, fuelTypeId);
    }

    @GetMapping("/{fuelTypeId}/historial")
    @Operation(summary = "Paged price history for a fuel")
    public Page<PriceHistoryResponse> history(@PathVariable UUID stationId,
                                              @PathVariable @Positive Integer fuelTypeId,
                                              @ParameterObject Pageable pageable) {
        return priceService.history(stationId, fuelTypeId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new price (admin or CNE sync use)")
    public PriceHistoryResponse register(@PathVariable UUID stationId,
                                             @Valid @RequestBody PriceRegisterRequest req) {
        return priceService.register(stationId, req);
    }
}
