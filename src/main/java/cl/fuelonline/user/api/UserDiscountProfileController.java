package cl.fuelonline.user.api;

import cl.fuelonline.finance.application.dto.DiscountResponse;
import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.application.service.UserDiscountProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/discounts")
@RequiredArgsConstructor
@Tag(name = "User discount profile", description = "Descuentos seleccionados por el usuario")
public class UserDiscountProfileController {

    private final UserDiscountProfileService service;

    @GetMapping
    @Operation(summary = "Lista los descuentos seleccionados por el usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public List<DiscountResponse> getSelected(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.getSelected(user.userId());
    }

    @PutMapping
    @Operation(summary = "Reemplaza la selección de descuentos del usuario (full overwrite)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public List<DiscountResponse> setSelected(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody List<Integer> discountIds) {
        return service.setSelected(user.userId(), discountIds);
    }
}
