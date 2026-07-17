package com.tradesphere.ai.copilot.application.port.out;

import com.tradesphere.ai.copilot.domain.model.Conversation;
import com.tradesphere.ai.copilot.domain.model.ConversationId;

import java.util.Optional;
import java.util.UUID;

/**
 * Port for conversation persistence
 */
public interface ConversationRepository {
    Conversation save(Conversation conversation);
    Optional<Conversation> findById(ConversationId id);
    Optional<Conversation> findBySessionId(String sessionId);
    Optional<Conversation> findActiveByUserId(UUID userId);
    void delete(ConversationId id);
}

