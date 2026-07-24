package com.tradesphere.ai.copilot.application.port.in;

import com.tradesphere.ai.copilot.domain.model.Intent;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;

/**
 * Response from copilot
 */
@Value
@Builder
public class CopilotResponse {
    String sessionId;
    String message;
    Intent intent;
    String entity;
    Map<String, Object> data;
    List<String> suggestions;
    List<String> citations;
    boolean requiresInput;
    float confidence;
}

