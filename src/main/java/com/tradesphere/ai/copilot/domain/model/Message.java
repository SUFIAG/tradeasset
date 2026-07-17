package com.tradesphere.ai.copilot.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Message Entity - Represents a single message in a conversation
 */
@Getter
public class Message {
    
    private final UUID id;
    private final MessageRole role;
    private final String content;
    private final Intent intent;
    private final float confidence;
    private final Instant timestamp;

    private Message(MessageRole role, String content, Intent intent, float confidence) {
        this.id = UUID.randomUUID();
        this.role = role;
        this.content = content;
        this.intent = intent;
        this.confidence = confidence;
        this.timestamp = Instant.now();
    }

    /**
     * Create a user message
     */
    public static Message userMessage(String content) {
        return new Message(MessageRole.USER, content, Intent.UNKNOWN, 0f);
    }

    /**
     * Create an assistant message
     */
    public static Message assistantMessage(String content, Intent intent, float confidence) {
        return new Message(MessageRole.ASSISTANT, content, intent, confidence);
    }

    /**
     * Create a system message
     */
    public static Message systemMessage(String content) {
        return new Message(MessageRole.SYSTEM, content, Intent.SYSTEM, 1.0f);
    }

    // For persistence reconstruction
    public static Message reconstruct(UUID id, MessageRole role, String content, 
                                     Intent intent, float confidence, Instant timestamp) {
        Message message = new Message(role, content, intent, confidence);
        return message;
    }
}

