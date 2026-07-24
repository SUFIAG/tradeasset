package com.tradesphere.ai.copilot.application.port.in;

import java.util.UUID;

/**
 * Use case for sending a message in a conversation
 */
public interface SendMessageUseCase {
    CopilotResponse sendMessage(SendMessageCommand command);
}

