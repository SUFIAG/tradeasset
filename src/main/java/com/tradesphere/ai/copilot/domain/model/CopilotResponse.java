package com.tradesphere.ai.copilot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Response from Copilot Agent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopilotResponse {
    
    private String message;
    private IntentType intent;
    private String entity;
    private Float confidence;
    
    @Builder.Default
    private List<String> suggestions = new ArrayList<>();
    
    @Builder.Default
    private List<String> citations = new ArrayList<>();
    
    @Builder.Default
    private boolean requiresInput = false;
    
    private boolean success;
    private String errorMessage;
    
    /**
     * Create error response
     */
    public static CopilotResponse error(String message) {
        return CopilotResponse.builder()
            .message(message)
            .success(false)
            .errorMessage(message)
            .intent(IntentType.UNKNOWN)
            .build();
    }
    
    /**
     * Create success response
     */
    public static CopilotResponse success(String message) {
        return CopilotResponse.builder()
            .message(message)
            .success(true)
            .build();
    }
}

