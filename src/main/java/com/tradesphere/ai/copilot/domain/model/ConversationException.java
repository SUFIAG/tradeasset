package com.tradesphere.ai.copilot.domain.model;

/**
 * Conversation Domain Exception
 */
public class ConversationException extends RuntimeException {
    
    public ConversationException(String message) {
        super(message);
    }

    public ConversationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ConversationException notFound(String conversationId) {
        return new ConversationException("Conversation not found: " + conversationId);
    }

    public static ConversationException invalidSession(String sessionId) {
        return new ConversationException("Invalid session: " + sessionId);
    }
}

