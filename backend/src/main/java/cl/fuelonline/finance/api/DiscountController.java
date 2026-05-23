package cl.fuelonline.finance.api;

import cl.fuelonline.finance.application.dto.DiscountCreateRequest;
import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.finance.application.dto.DiscountUpdateRequest;
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
@Tag(name = "Discounts", description = "Promotions and calculation of the best applicable discount")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping("/catalogo")
    @Operation(summary = "Catálogo completo de descuentos activos para que el usuario elija los suyos")
    public List<DiscountResponse> catalogo() {
        return discountService.listAll();
    }

    @GetMapping("/all")
    @Operation(summary = "Todos los descuentos incluyendo inactivos — solo ADMIN")
    public List<DiscountResponse> listAllAdmin() {
        return discountService.listAllAdmin();
    }

    @GetMapping
    @Operation(summary = "Listar descuentos por brand")
    public List<DiscountResponse> listByBrand(@RequestParam @Positive Integer brandId) {
        return discountService.listByBrand(brandId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener discount por ID")
    public DiscountResponse find(@PathVariable Integer id) {
        return discountService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a discount")
    public ResponseEntity<DiscountResponse> create(@Valid @RequestBody DiscountCreateRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        DiscountResponse created = discountService.create(req);
        URI location = uriBuilder.path("/api/v1/descuentos/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar discount")
    public DiscountResponse update(@PathVariable Integer id,
                                        @Valid @RequestBody DiscountUpdateRequest req) {
        return discountService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del discount")
    public void delete(@PathVariable Integer id) {
        discountService.delete(id);
    }

}
