package com.tradesphere.ai.document.application.service.extraction;

import com.tradesphere.ai.document.domain.model.DocumentType;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

/**
 * Result of document extraction
 * Immutable value object
 */
@Value
@Builder
public class ExtractionResult {
    Map<String, Object> extractedData;
    Float confidence;
    String model;
    boolean successful;
    String errorMessage;

    public static ExtractionResult success(Map<String, Object> data, Float confidence, String model) {
        return ExtractionResult.builder()
            .extractedData(data)
            .confidence(confidence)
            .model(model)
            .successful(true)
            .build();
    }

    public static ExtractionResult failure(String errorMessage) {
        return ExtractionResult.builder()
            .successful(false)
            .errorMessage(errorMessage)
            .build();
    }
}

