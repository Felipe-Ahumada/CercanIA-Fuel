package cl.fuelonline.finance.api;

import cl.fuelonline.finance.application.dto.BancoCreateRequest;
import cl.fuelonline.finance.application.dto.BancoResponse;
import cl.fuelonline.finance.application.dto.BancoUpdateRequest;
import cl.fuelonline.finance.application.service.BancoService;
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
public class BancoController {

    private final BancoService bancoService;

    @GetMapping
    @Operation(summary = "Listar bancos paginado")
    public Page<BancoResponse> listar(@ParameterObject Pageable pageable) {
        return bancoService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener banco por ID")
    public BancoResponse buscar(@PathVariable Integer id) {
        return bancoService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo banco")
    public ResponseEntity<BancoResponse> crear(@Valid @RequestBody BancoCreateRequest req,
                                               UriComponentsBuilder uriBuilder) {
        BancoResponse creado = bancoService.crear(req);
        URI location = uriBuilder.path("/api/v1/bancos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar banco")
    public BancoResponse actualizar(@PathVariable Integer id,
                                    @Valid @RequestBody BancoUpdateRequest req) {
        return bancoService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico del banco")
    public void eliminar(@PathVariable Integer id) {
        bancoService.eliminar(id);
    }
}
