package com.tradesphere.ai.copilot.domain.model;

import java.util.UUID;

/**
 * Conversation ID Value Object
 */
public record ConversationId(UUID value) {
    
    public ConversationId {
        if (value == null) {
            throw new IllegalArgumentException("ConversationId cannot be null");
        }
    }

    public static ConversationId of(UUID value) {
        return new ConversationId(value);
    }

    public static ConversationId of(String value) {
        return new ConversationId(UUID.fromString(value));
    }

    public static ConversationId generate() {
        return new ConversationId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

