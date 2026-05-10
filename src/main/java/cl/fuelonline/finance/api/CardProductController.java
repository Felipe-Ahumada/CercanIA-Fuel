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
@Tag(name = "Productos de tarjeta", description = "Productos de tarjeta por bank (ej: Visa Platinum Scotiabank)")
public class CardProductController {

    private final CardProductService cardProductService;

    @GetMapping
    @Operation(summary = "Listar productos de tarjeta. Filtra por bankId si se provee.")
    public List<CardProductResponse> list(@RequestParam(required = false) Integer bankId) {
        return bankId != null
                ? cardProductService.listByBank(bankId)
                : cardProductService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto de tarjeta por ID")
    public CardProductResponse find(@PathVariable Integer id) {
        return cardProductService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Crear un producto de tarjeta")
    public ResponseEntity<CardProductResponse> create(@Valid @RequestBody CardProductCreateRequest req,
                                                         UriComponentsBuilder uriBuilder) {
        CardProductResponse creado = cardProductService.create(req);
        URI location = uriBuilder.path("/api/v1/tarjetas-producto/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto de tarjeta")
    public CardProductResponse update(@PathVariable Integer id,
                                              @Valid @RequestBody CardProductUpdateRequest req) {
        return cardProductService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del producto de tarjeta")
    public void delete(@PathVariable Integer id) {
        cardProductService.delete(id);
    }
}
