package cl.fuelonline.user.api;

import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.application.service.UserDiscountProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User discount profile", description = "Descuentos seleccionados por el usuario")
public class UserDiscountProfileController {

    private final UserDiscountProfileService service;

    @GetMapping("/api/v1/users/me/discounts")
    @Operation(summary = "Lista los descuentos seleccionados por el usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public List<DiscountResponse> getSelected(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.getSelected(user.userId());
    }

    @PutMapping("/api/v1/users/me/discounts")
    @Operation(summary = "Reemplaza la selección de descuentos del usuario (full overwrite)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public List<DiscountResponse> setSelected(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody List<Integer> discountIds) {
        return service.setSelected(user.userId(), discountIds);
    }

    @GetMapping("/api/v1/usuarios/{userId}/descuentos")
    @Operation(summary = "Descuentos seleccionados de un usuario — solo ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    public List<DiscountResponse> getSelectedByAdmin(
            @PathVariable UUID userId,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        if (!"ADMIN".equalsIgnoreCase(principal.roleName())) {
            throw new AccessDeniedException("Solo ADMIN puede ver descuentos de otros usuarios");
        }
        return service.getSelected(userId);
    }
}
