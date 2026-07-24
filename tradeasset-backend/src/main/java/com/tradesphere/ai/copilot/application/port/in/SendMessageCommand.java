package com.tradesphere.ai.copilot.application.port.in;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Command to send a message
 */
@Value
@Builder
public class SendMessageCommand {
    String sessionId;
    String message;
    UUID userId;
    UUID tenantId;
}

