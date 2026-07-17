package com.tradesphere.ai.copilot.application.service;

import com.tradesphere.ai.copilot.domain.model.ConversationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing conversation memory/context
 */
@Service
@Slf4j
public class ConversationMemoryService {

    private final Map<String, ConversationContext> contextStore = new ConcurrentHashMap<>();

    private static final int MAX_SESSION_AGE_HOURS = 24;

    /**
     * Get or create conversation context
     */
    public ConversationContext getContext(String sessionId) {
        return contextStore.computeIfAbsent(sessionId, id ->
            ConversationContext.builder()
                .sessionId(id)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build()
        );
    }

    /**
     * Add message to conversation
     */
    public void addMessage(String sessionId, String userMessage, String assistantMessage) {
        ConversationContext context = getContext(sessionId);
        context.addMessage("user", userMessage);
        context.addMessage("assistant", assistantMessage);
        log.debug("Added messages to session: {}", sessionId);
    }

    /**
     * Update context variables
     */
    public void updateContext(String sessionId, String key, Object value) {
        ConversationContext context = getContext(sessionId);
        context.setVariable(key, value);
    }

    /**
     * Clear old sessions
     */
    public void clearOldSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(MAX_SESSION_AGE_HOURS);
        contextStore.entrySet().removeIf(entry ->
            entry.getValue().getLastUpdatedAt().isBefore(cutoff)
        );
        log.info("Cleared old conversation sessions");
    }

    /**
     * Clear specific session
     */
    public void clearSession(String sessionId) {
        contextStore.remove(sessionId);
        log.debug("Cleared session: {}", sessionId);
    }
}

