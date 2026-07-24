package com.tradesphere.ai.copilot.application.port.in;

/**
 * Use case for ending a conversation
 */
public interface EndConversationUseCase {
    void endConversation(String sessionId);
}

