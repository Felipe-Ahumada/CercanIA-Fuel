package cl.fuelonline.station.api;

import cl.fuelonline.station.application.dto.PrecioActualResponse;
import cl.fuelonline.station.application.dto.PrecioHistorialResponse;
import cl.fuelonline.station.application.dto.RegistrarPrecioRequest;
import cl.fuelonline.station.application.service.PrecioService;
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
@RequestMapping("/api/v1/bencineras/{bencineraId}/precios")
@RequiredArgsConstructor
@Validated
@Tag(name = "Precios", description = "Precios actuales e historial por bencinera y combustible")
public class PrecioController {

    private final PrecioService precioService;

    @GetMapping
    @Operation(summary = "Precios vigentes de todos los combustibles que vende la bencinera")
    public List<PrecioActualResponse> precios(@PathVariable UUID bencineraId) {
        return precioService.preciosActualesDe(bencineraId);
    }

    @GetMapping("/{tipoCombustibleId}")
    @Operation(summary = "Precio vigente de un combustible especifico")
    public PrecioActualResponse precioActual(@PathVariable UUID bencineraId,
                                             @PathVariable @Positive Integer tipoCombustibleId) {
        return precioService.precioActual(bencineraId, tipoCombustibleId);
    }

    @GetMapping("/{tipoCombustibleId}/historial")
    @Operation(summary = "Historial paginado de precios de un combustible")
    public Page<PrecioHistorialResponse> historial(@PathVariable UUID bencineraId,
                                                   @PathVariable @Positive Integer tipoCombustibleId,
                                                   @ParameterObject Pageable pageable) {
        return precioService.historial(bencineraId, tipoCombustibleId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un nuevo precio (uso administrativo o sync con CNE)")
    public PrecioHistorialResponse registrar(@PathVariable UUID bencineraId,
                                             @Valid @RequestBody RegistrarPrecioRequest req) {
        return precioService.registrar(bencineraId, req);
    }
}
