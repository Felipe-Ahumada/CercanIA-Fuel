package cl.fuelonline.user.api;

import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.application.dto.CompleteProfileRequest;
import cl.fuelonline.user.application.dto.AdminUserResponse;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management for the Fuel Online platform")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List users with stats (paged) — solo ADMIN")
    public Page<AdminUserResponse> list(@ParameterObject Pageable pageable) {
        return userService.listAdmin(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User encontrado"),
            @ApiResponse(responseCode = "404", description = "User no existe", content = @Content)
    })
    public UserResponse find(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        UserResponse created = userService.create(req);
        URI location = uriBuilder.path("/api/v1/usuarios/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/complete-profile")
    @Operation(summary = "Complete profile for a Google new user (public endpoint)")
    public UserResponse completeProfile(@Valid @RequestBody CompleteProfileRequest req) {
        return userService.completeProfile(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un user")
    public UserResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UserUpdateRequest req,
                               @AuthenticationPrincipal AuthenticatedUser principal) {
        if (!principal.userId().equals(id) && !"ADMIN".equalsIgnoreCase(principal.roleName())) {
            throw new AccessDeniedException("No autorizado para modificar este usuario");
        }
        return userService.update(id, req);
    }

    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activar o desactivar un usuario — solo ADMIN")
    public void setActive(@PathVariable UUID id,
                          @RequestParam boolean active,
                          @AuthenticationPrincipal AuthenticatedUser principal) {
        if (!"ADMIN".equalsIgnoreCase(principal.roleName())) {
            throw new AccessDeniedException("Solo ADMIN puede cambiar el estado de un usuario");
        }
        userService.setActive(id, active);
    }

}
