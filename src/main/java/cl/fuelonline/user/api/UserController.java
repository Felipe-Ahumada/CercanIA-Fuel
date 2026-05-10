package cl.fuelonline.user.api;

import cl.fuelonline.user.application.dto.UserCreateRequest;
import cl.fuelonline.user.application.dto.UserResponse;
import cl.fuelonline.user.application.dto.UserUpdateRequest;
import cl.fuelonline.user.application.service.UserService;
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
public class UserController {

    private final UserService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuarios paginado")
    public Page<UserResponse> listar(@ParameterObject Pageable pageable) {
        return usuarioService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User encontrado"),
            @ApiResponse(responseCode = "404", description = "User no existe", content = @Content)
    })
    public UserResponse buscar(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    public ResponseEntity<UserResponse> crear(@Valid @RequestBody UserCreateRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        UserResponse creado = usuarioService.crear(req);
        URI location = uriBuilder.path("/api/v1/usuarios/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un usuario")
    public UserResponse actualizar(@PathVariable UUID id,
                                      @Valid @RequestBody UserUpdateRequest req) {
        return usuarioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrado logico: marca el usuario como inactivo")
    public void eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
    }
}
