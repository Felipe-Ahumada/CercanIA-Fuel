package cl.fuelonline.finance.api;

import cl.fuelonline.finance.application.dto.*;
import cl.fuelonline.finance.application.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/descuentos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Descuentos", description = "Promociones y calculo del mejor descuento aplicable")
public class DiscountController {

    private final DiscountService descuentoService;

    @GetMapping
    @Operation(summary = "Listar descuentos por marca")
    public List<DiscountResponse> listarPorMarca(@RequestParam @Positive Integer marcaId) {
        return descuentoService.listarPorMarca(marcaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener descuento por ID")
    public DiscountResponse buscar(@PathVariable Integer id) {
        return descuentoService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un descuento")
    public ResponseEntity<DiscountResponse> crear(@Valid @RequestBody DiscountCreateRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        DiscountResponse creado = descuentoService.crear(req);
        URI location = uriBuilder.path("/api/v1/descuentos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar descuento")
    public DiscountResponse actualizar(@PathVariable Integer id,
                                        @Valid @RequestBody DiscountUpdateRequest req) {
        return descuentoService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del descuento")
    public void eliminar(@PathVariable Integer id) {
        descuentoService.eliminar(id);
    }

    @PostMapping("/calcular")
    @Operation(summary = "Calcular el mejor descuento aplicable a una compra",
               description = "Recibe marca, combustible, monto bruto y tarjetas del usuario. " +
                             "Devuelve el descuento que entrega el mayor ahorro.")
    public CalculatedDiscountResponse calcular(@Valid @RequestBody CalculateDiscountRequest req) {
        return descuentoService.calcularMejorDescuento(req);
    }
}
