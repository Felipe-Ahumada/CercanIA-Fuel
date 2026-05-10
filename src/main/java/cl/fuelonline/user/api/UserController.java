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
@Tag(name = "Users", description = "User management for the Fuel Online platform")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List users (paged)")
    public Page<UserResponse> list(@ParameterObject Pageable pageable) {
        return userService.list(pageable);
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

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un user")
    public UserResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody UserUpdateRequest req) {
        return userService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete: marks the user as inactive")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
