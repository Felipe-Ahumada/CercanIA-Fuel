package cl.fuelonline.user.api;

import cl.fuelonline.user.application.dto.UsuarioCreateRequest;
import cl.fuelonline.user.application.dto.UsuarioResponse;
import cl.fuelonline.user.application.dto.UsuarioUpdateRequest;
import cl.fuelonline.user.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestion de usuarios de la plataforma Bencina en Linea")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuarios paginado")
    public Page<UsuarioResponse> listar(@ParameterObject Pageable pageable) {
        return usuarioService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe", content = @Content)
    })
    public UsuarioResponse buscar(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCreateRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        UsuarioResponse creado = usuarioService.crear(req);
        URI location = uriBuilder.path("/api/v1/usuarios/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un usuario")
    public UsuarioResponse actualizar(@PathVariable UUID id,
                                      @Valid @RequestBody UsuarioUpdateRequest req) {
        return usuarioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico: marca el usuario como inactivo")
    public void eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
    }
}
