package cl.fuelonline.finance.api;

import cl.fuelonline.finance.application.dto.CardProductCreateRequest;
import cl.fuelonline.finance.application.dto.CardProductResponse;
import cl.fuelonline.finance.application.dto.CardProductUpdateRequest;
import cl.fuelonline.finance.application.service.CardProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tarjetas-producto")
@RequiredArgsConstructor
@Tag(name = "Productos de tarjeta", description = "Productos de tarjeta por banco (ej: Visa Platinum Scotiabank)")
public class CardProductController {

    private final CardProductService tarjetaService;

    @GetMapping
    @Operation(summary = "Listar productos de tarjeta. Filtra por bancoId si se provee.")
    public List<CardProductResponse> listar(@RequestParam(required = false) Integer bancoId) {
        return bancoId != null
                ? tarjetaService.listarPorBanco(bancoId)
                : tarjetaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto de tarjeta por ID")
    public CardProductResponse buscar(@PathVariable Integer id) {
        return tarjetaService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un producto de tarjeta")
    public ResponseEntity<CardProductResponse> crear(@Valid @RequestBody CardProductCreateRequest req,
                                                         UriComponentsBuilder uriBuilder) {
        CardProductResponse creado = tarjetaService.crear(req);
        URI location = uriBuilder.path("/api/v1/tarjetas-producto/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto de tarjeta")
    public CardProductResponse actualizar(@PathVariable Integer id,
                                              @Valid @RequestBody CardProductUpdateRequest req) {
        return tarjetaService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del producto de tarjeta")
    public void eliminar(@PathVariable Integer id) {
        tarjetaService.eliminar(id);
    }
}
