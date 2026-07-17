package com.tradesphere.ai.copilot.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Conversation Aggregate Root
 * Represents a chat conversation between user and copilot
 */
@Getter
public class Conversation extends AggregateRoot<ConversationId> {

    private UUID userId;
    private UUID tenantId;
    private String sessionId;
    private ConversationStatus status;
    private List<Message> messages;
    private Map<String, Object> context;
    private Instant startedAt;
    private Instant lastMessageAt;
    private Instant endedAt;

    /**
     * Private constructor - use factory methods
     */
    private Conversation(ConversationId id, UUID userId, UUID tenantId, String sessionId) {
        super(id);
        this.userId = userId;
        this.tenantId = tenantId;
        this.sessionId = sessionId;
        this.status = ConversationStatus.ACTIVE;
        this.messages = new ArrayList<>();
        this.context = new HashMap<>();
        this.startedAt = Instant.now();
        this.lastMessageAt = Instant.now();
    }

    /**
     * Factory method to start a new conversation
     */
    public static Conversation start(UUID userId, UUID tenantId, String sessionId) {
        if (userId == null) {
            throw new ConversationException("User ID cannot be null");
        }
        if (tenantId == null) {
            throw new ConversationException("Tenant ID cannot be null");
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new ConversationException("Session ID cannot be empty");
        }

        return new Conversation(ConversationId.generate(), userId, tenantId, sessionId);
    }

    /**
     * Add a user message to the conversation
     */
    public Message addUserMessage(String content) {
        validateActive();
        validateMessageContent(content);

        Message message = Message.userMessage(content);
        messages.add(message);
        this.lastMessageAt = Instant.now();
        return message;
    }

    /**
     * Add an assistant (copilot) message
     */
    public Message addAssistantMessage(String content, Intent intent, float confidence) {
        validateActive();
        validateMessageContent(content);

        Message message = Message.assistantMessage(content, intent, confidence);
        messages.add(message);
        this.lastMessageAt = Instant.now();
        return message;
    }

    /**
     * Set context variable
     */
    public void setContextVariable(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new ConversationException("Context key cannot be empty");
        }
        this.context.put(key, value);
    }

    /**
     * Get context variable
     */
    public Object getContextVariable(String key) {
        return this.context.get(key);
    }

    /**
     * End the conversation
     */
    public void end() {
        if (status == ConversationStatus.ENDED) {
            throw new ConversationException("Conversation is already ended");
        }
        this.status = ConversationStatus.ENDED;
        this.endedAt = Instant.now();
    }

    /**
     * Get recent messages (last N)
     */
    public List<Message> getRecentMessages(int count) {
        if (count <= 0 || messages.isEmpty()) {
            return new ArrayList<>();
        }
        int startIndex = Math.max(0, messages.size() - count);
        return new ArrayList<>(messages.subList(startIndex, messages.size()));
    }

    /**
     * Get conversation history as string
     */
    public String getHistory() {
        StringBuilder history = new StringBuilder();
        for (Message msg : messages) {
            history.append(msg.getRole().name())
                   .append(": ")
                   .append(msg.getContent())
                   .append("\n");
        }
        return history.toString();
    }

    /**
     * Check if conversation is active
     */
    public boolean isActive() {
        return status == ConversationStatus.ACTIVE;
    }

    private void validateActive() {
        if (!isActive()) {
            throw new ConversationException("Cannot modify ended conversation");
        }
    }

    private void validateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ConversationException("Message content cannot be empty");
        }
        if (content.length() > 10000) {
            throw new ConversationException("Message content too long (max 10000 characters)");
        }
    }

    // Builder for persistence reconstruction
    public static ConversationBuilder builder() {
        return new ConversationBuilder();
    }

    public static class ConversationBuilder {
        private Conversation conversation;

        public ConversationBuilder id(UUID id) {
            this.conversation = new Conversation(
                ConversationId.of(id),
                null, null, null
            );
            return this;
        }

        public ConversationBuilder userId(UUID userId) {
            conversation.userId = userId;
            return this;
        }

        public ConversationBuilder tenantId(UUID tenantId) {
            conversation.tenantId = tenantId;
            return this;
        }

        public ConversationBuilder sessionId(String sessionId) {
            conversation.sessionId = sessionId;
            return this;
        }

        public ConversationBuilder status(ConversationStatus status) {
            conversation.status = status;
            return this;
        }

        public ConversationBuilder messages(List<Message> messages) {
            conversation.messages = messages != null ? new ArrayList<>(messages) : new ArrayList<>();
            return this;
        }

        public ConversationBuilder context(Map<String, Object> context) {
            conversation.context = context != null ? new HashMap<>(context) : new HashMap<>();
            return this;
        }

        public ConversationBuilder startedAt(Instant startedAt) {
            conversation.startedAt = startedAt;
            return this;
        }

        public ConversationBuilder lastMessageAt(Instant lastMessageAt) {
            conversation.lastMessageAt = lastMessageAt;
            return this;
        }

        public ConversationBuilder endedAt(Instant endedAt) {
            conversation.endedAt = endedAt;
            return this;
        }

        public ConversationBuilder createdBy(UUID createdBy) {
            conversation.createdBy = createdBy;
            return this;
        }

        public ConversationBuilder createdAt(Instant createdAt) {
            conversation.createdAt = createdAt;
            return this;
        }

        public ConversationBuilder updatedBy(UUID updatedBy) {
            conversation.updatedBy = updatedBy;
            return this;
        }

        public ConversationBuilder updatedAt(Instant updatedAt) {
            conversation.updatedAt = updatedAt;
            return this;
        }

        public ConversationBuilder version(Long version) {
            conversation.version = version;
            return this;
        }

        public Conversation build() {
            return conversation;
        }
    }
}

