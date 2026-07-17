package com.tradesphere.ai.copilot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversation context for multi-turn dialogues
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {

    private String sessionId;
    private String userId;
    private String tenantId;

    @Builder.Default
    private List<ConversationMessage> messages = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    private IntentType lastIntent;
    private String lastEntity;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    /**
     * Add message to context
     */
    public void addMessage(String role, String content) {
        messages.add(ConversationMessage.builder()
            .role(role)
            .content(content)
            .timestamp(LocalDateTime.now())
            .build());
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Get recent messages
     */
    public List<ConversationMessage> getRecentMessages(int count) {
        int size = messages.size();
        int start = Math.max(0, size - count);
        return messages.subList(start, size);
    }

    /**
     * Set context variable
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * Get context variable
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationMessage {
        private String role; // user, assistant, system
        private String content;
        private LocalDateTime timestamp;
    }
}

