package cl.fuelonline.user.api;

import cl.fuelonline.security.domain.AuthenticatedUser;
import cl.fuelonline.user.application.dto.BankProfileRequest;
import cl.fuelonline.user.application.dto.BankProfileResponse;
import cl.fuelonline.user.application.service.UserBankProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/bank-profile")
@RequiredArgsConstructor
@Tag(name = "User bank profile", description = "Stores the signed-in user's preferred bank and card type for discount calculations")
public class UserBankProfileController {

    private final UserBankProfileService service;

    @GetMapping
    @Operation(summary = "Get the current user's bank profile",
               security = @SecurityRequirement(name = "bearerAuth"))
    public BankProfileResponse getProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return service.getProfile(user.userId());
    }

    @PostMapping
    @Operation(summary = "Replace the current user's bank profile (full overwrite)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public BankProfileResponse saveProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody BankProfileRequest req) {
        return service.saveProfile(user.userId(), req);
    }
}
