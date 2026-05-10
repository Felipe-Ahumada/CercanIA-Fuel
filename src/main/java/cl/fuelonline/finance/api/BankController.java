package cl.fuelonline.finance.api;

import cl.fuelonline.finance.application.dto.BankCreateRequest;
import cl.fuelonline.finance.application.dto.BankResponse;
import cl.fuelonline.finance.application.dto.BankUpdateRequest;
import cl.fuelonline.finance.application.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bancos")
@RequiredArgsConstructor
@Tag(name = "Bancos", description = "Catalogo de bancos emisores de tarjetas")
public class BankController {

    private final BankService bancoService;

    @GetMapping
    @Operation(summary = "Listar bancos paginado")
    public Page<BankResponse> listar(@ParameterObject Pageable pageable) {
        return bancoService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener banco por ID")
    public BankResponse buscar(@PathVariable Integer id) {
        return bancoService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo banco")
    public ResponseEntity<BankResponse> crear(@Valid @RequestBody BankCreateRequest req,
                                               UriComponentsBuilder uriBuilder) {
        BankResponse creado = bancoService.crear(req);
        URI location = uriBuilder.path("/api/v1/bancos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar banco")
    public BankResponse actualizar(@PathVariable Integer id,
                                    @Valid @RequestBody BankUpdateRequest req) {
        return bancoService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del banco")
    public void eliminar(@PathVariable Integer id) {
        bancoService.eliminar(id);
    }
}
