package cl.fuelonline.security.api;

import cl.fuelonline.security.application.dto.MeResponse;
import cl.fuelonline.security.domain.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacion", description = "Identidad del usuario actual")
public class AuthController {

    @GetMapping("/me")
    @Operation(summary = "Devuelve el perfil del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser user,
                                         Authentication auth) {
        if (user == null) return ResponseEntity.status(401).build();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity.ok(new MeResponse(
                user.usuarioId(),
                user.email(),
                user.firebaseUid(),
                user.rolNombre(),
                authorities));
    }
}
