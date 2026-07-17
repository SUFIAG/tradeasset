package com.tradesphere.ai.copilot.adapter.in.rest;

import com.tradesphere.ai.copilot.application.port.in.*;
import com.tradesphere.ai.copilot.domain.model.Conversation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Copilot Agent - NLP Interface
 */
@RestController
@RequestMapping("/api/v1/copilot")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Copilot Agent", description = "AI-powered conversational interface")
public class CopilotController {

    private final StartConversationUseCase startConversationUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final EndConversationUseCase endConversationUseCase;

    @PostMapping("/conversations")
    @Operation(summary = "Start new conversation", description = "Initialize a new chat session with the copilot")
    public ResponseEntity<ConversationResponse> startConversation(
            @RequestParam UUID tenantId,
            Authentication authentication) {

        log.info("Starting new conversation for tenant: {}", tenantId);
        UUID userId = getUserId(authentication);

        Conversation conversation = startConversationUseCase.startConversation(userId, tenantId);

        return ResponseEntity.ok(ConversationResponse.from(conversation));
    }

    @PostMapping("/conversations/{sessionId}/messages")
    @Operation(summary = "Send message", description = "Send a message to the copilot and receive response")
    public ResponseEntity<CopilotResponse> sendMessage(
            @PathVariable String sessionId,
            @RequestBody SendMessageRequest request,
            @RequestParam UUID tenantId,
            Authentication authentication) {

        log.info("Processing message for session: {}", sessionId);
        UUID userId = getUserId(authentication);

        SendMessageCommand command = SendMessageCommand.builder()
                .sessionId(sessionId)
                .message(request.getMessage())
                .userId(userId)
                .tenantId(tenantId)
                .build();

        CopilotResponse response = sendMessageUseCase.sendMessage(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/conversations/{sessionId}")
    @Operation(summary = "End conversation", description = "End and close a conversation session")
    public ResponseEntity<Void> endConversation(@PathVariable String sessionId) {
        log.info("Ending conversation: {}", sessionId);

        endConversationUseCase.endConversation(sessionId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if copilot service is healthy")
    public ResponseEntity<HealthResponse> healthCheck() {
        return ResponseEntity.ok(new HealthResponse("Copilot service is healthy", true));
    }

    private UUID getUserId(Authentication authentication) {
        // Extract user ID from authentication
        // For now, return a default UUID - implement proper extraction from JWT/Security context
        return UUID.randomUUID();
    }

    // DTOs

    public record SendMessageRequest(String message) {}

    public record ConversationResponse(
        String sessionId,
        UUID userId,
        UUID tenantId,
        String status,
        String startedAt
    ) {
        public static ConversationResponse from(Conversation conversation) {
            return new ConversationResponse(
                conversation.getSessionId(),
                conversation.getUserId(),
                conversation.getTenantId(),
                conversation.getStatus().name(),
                conversation.getStartedAt().toString()
            );
        }
    }

    public record HealthResponse(String message, boolean healthy) {}
}

