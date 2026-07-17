package com.tradesphere.ai.shared.ai;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * AI Response - unified response format for all AI operations
 * Includes result, confidence, and metadata
 */
@Value
@Builder
public class AIResponse<T> {

    String requestId;

    T result;

    boolean success;

    Float confidence;

    String model;

    Long latencyMs;

    @Builder.Default
    Instant timestamp = Instant.now();

    String errorMessage;

    Map<String, Object> metadata;

    public static <T> AIResponse<T> success(String requestId, T result, Float confidence, String model, Long latency) {
        return AIResponse.<T>builder()
            .requestId(requestId)
            .result(result)
            .success(true)
            .confidence(confidence)
            .model(model)
            .latencyMs(latency)
            .build();
    }

    public static <T> AIResponse<T> failure(String requestId, String errorMessage) {
        return AIResponse.<T>builder()
            .requestId(requestId)
            .success(false)
            .errorMessage(errorMessage)
            .build();
    }
}

