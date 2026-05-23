package cl.fuelonline.chat.api;

import cl.fuelonline.chat.application.dto.ChatRequest;
import cl.fuelonline.chat.application.dto.ChatResponse;
import cl.fuelonline.chat.application.service.ChatService;
import cl.fuelonline.security.domain.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "AI assistant powered by Gemini")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Send a message to the AI assistant",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(user.userId(), request));
    }
}
