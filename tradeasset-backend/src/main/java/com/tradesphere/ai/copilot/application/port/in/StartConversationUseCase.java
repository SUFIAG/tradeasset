package com.tradesphere.ai.copilot.application.port.in;

import com.tradesphere.ai.copilot.domain.model.Conversation;

import java.util.UUID;

/**
 * Use case for starting a new conversation
 */
public interface StartConversationUseCase {
    Conversation startConversation(UUID userId, UUID tenantId);
}

