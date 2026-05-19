package cl.fuelonline.user.api;

import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.application.dto.VehicleCreateRequest;
import cl.fuelonline.user.application.dto.VehicleResponse;
import cl.fuelonline.user.application.service.UserVehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios/me/vehiculos")
@RequiredArgsConstructor
@Tag(name = "My Vehicles", description = "CRUD for the authenticated user's vehicles")
public class UserVehicleController {

    private final UserVehicleService service;

    @GetMapping
    public List<VehicleResponse> list(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.getMyVehicles(user.userId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse add(@AuthenticationPrincipal AuthenticatedUser user,
                               @Valid @RequestBody VehicleCreateRequest req) {
        return service.addVehicle(user.userId(), req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser user,
                       @PathVariable UUID id) {
        service.deleteVehicle(user.userId(), id);
    }
}
