package cl.fuelonline.security.api;

import cl.fuelonline.security.application.dto.AuthResponse;
import cl.fuelonline.security.application.dto.ChangePasswordRequest;
import cl.fuelonline.security.application.dto.ForgotPasswordRequest;
import cl.fuelonline.security.application.dto.LoginRequest;
import cl.fuelonline.security.application.dto.MeResponse;
import cl.fuelonline.security.application.dto.RegisterRequest;
import cl.fuelonline.security.application.dto.ResetPasswordRequest;
import cl.fuelonline.security.application.service.LocalAuthService;
import cl.fuelonline.security.domain.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final LocalAuthService localAuthService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registro con email y contraseña (auth local)")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return localAuthService.register(req);
    }

    @PostMapping("/login")
    @Operation(summary = "Login con email y contraseña, devuelve JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return localAuthService.login(req);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cambia la contraseña del usuario LOCAL autenticado")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest req,
                               @AuthenticationPrincipal AuthenticatedUser principal) {
        localAuthService.changePassword(principal.email(), req);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Solicita un OTP de 6 dígitos por email para restablecer contraseña (solo usuarios LOCAL)")
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        localAuthService.requestPasswordReset(req);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Restablece la contraseña usando el OTP recibido por email")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        localAuthService.resetPassword(req);
    }

    @GetMapping("/me")
    @Operation(summary = "Perfil del usuario autenticado",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser user,
                                          Authentication auth) {
        if (user == null) return ResponseEntity.status(401).build();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity.ok(new MeResponse(
                user.userId(),
                user.email(),
                user.firebaseUid(),
                user.roleName(),
                authorities));
    }
}
