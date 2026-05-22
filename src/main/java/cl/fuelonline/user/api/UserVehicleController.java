package cl.fuelonline.user.api;

import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.application.dto.VehicleCreateRequest;
import cl.fuelonline.user.application.dto.VehicleResponse;
import cl.fuelonline.user.application.service.UserVehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "My Vehicles", description = "CRUD for the authenticated user's vehicles")
public class UserVehicleController {

    private final UserVehicleService service;

    @GetMapping("/api/v1/usuarios/me/vehiculos")
    public List<VehicleResponse> list(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.getMyVehicles(user.userId());
    }

    @GetMapping("/api/v1/usuarios/{userId}/vehiculos")
    @Operation(summary = "Vehículos de un usuario — solo ADMIN")
    public List<VehicleResponse> listByUser(@PathVariable UUID userId,
                                             @AuthenticationPrincipal AuthenticatedUser principal) {
        if (!"ADMIN".equalsIgnoreCase(principal.roleName())) {
            throw new AccessDeniedException("Solo ADMIN puede ver vehículos de otros usuarios");
        }
        return service.getMyVehicles(userId);
    }

    @PostMapping("/api/v1/usuarios/me/vehiculos")
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse add(@AuthenticationPrincipal AuthenticatedUser user,
                               @Valid @RequestBody VehicleCreateRequest req) {
        return service.addVehicle(user.userId(), req);
    }

    @DeleteMapping("/api/v1/usuarios/me/vehiculos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser user,
                       @PathVariable UUID id) {
        service.deleteVehicle(user.userId(), id);
    }
}
