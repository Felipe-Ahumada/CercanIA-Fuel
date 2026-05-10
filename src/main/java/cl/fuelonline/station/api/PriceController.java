package cl.fuelonline.station.api;

import cl.fuelonline.station.application.dto.CurrentPriceResponse;
import cl.fuelonline.station.application.dto.PriceHistoryResponse;
import cl.fuelonline.station.application.dto.RegistrarPrecioRequest;
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
@Tag(name = "Precios", description = "Precios actuales e historial por station y combustible")
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    @Operation(summary = "Precios vigentes de todos los combustibles que vende la station")
    public List<CurrentPriceResponse> prices(@PathVariable UUID stationId) {
        return priceService.preciosActualesDe(stationId);
    }

    @GetMapping("/{fuelTypeId}")
    @Operation(summary = "Precio vigente de un combustible especifico")
    public CurrentPriceResponse precioActual(@PathVariable UUID stationId,
                                             @PathVariable @Positive Integer fuelTypeId) {
        return priceService.precioActual(stationId, fuelTypeId);
    }

    @GetMapping("/{fuelTypeId}/historial")
    @Operation(summary = "Historial paginado de prices de un combustible")
    public Page<PriceHistoryResponse> historial(@PathVariable UUID stationId,
                                                   @PathVariable @Positive Integer fuelTypeId,
                                                   @ParameterObject Pageable pageable) {
        return priceService.historial(stationId, fuelTypeId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un nuevo price (uso administrativo o sync con CNE)")
    public PriceHistoryResponse register(@PathVariable UUID stationId,
                                             @Valid @RequestBody RegistrarPrecioRequest req) {
        return priceService.register(stationId, req);
    }
}
