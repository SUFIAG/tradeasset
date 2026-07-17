package com.tradesphere.ai.shared.ai;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * AI Request - unified request format for all AI operations
 * Supports various AI intents and contexts
 */
@Value
@Builder
public class AIRequest {

    @Builder.Default
    String requestId = UUID.randomUUID().toString();

    AIIntent intent;

    String input;

    Map<String, Object> context;

    String tenantId;

    String userId;

    @Builder.Default
    Instant timestamp = Instant.now();

    // Model preferences
    String preferredModel;

    Float temperature;

    Integer maxTokens;

    // Timeout in milliseconds
    @Builder.Default
    Long timeoutMs = 60000L;

    public static class AIRequestBuilder {
        public AIRequestBuilder addContext(String key, Object value) {
            if (this.context == null) {
                this.context = new java.util.HashMap<>();
            }
            this.context.put(key, value);
            return this;
        }
    }
}

