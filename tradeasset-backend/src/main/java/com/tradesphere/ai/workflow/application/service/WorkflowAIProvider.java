package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.shared.ai.AIRequest;
import com.tradesphere.ai.shared.ai.AIResponse;

import java.util.Map;

/**
 * AI Provider interface for workflow intelligence
 * Abstracts AI service calls for workflow operations
 */
public interface WorkflowAIProvider {

    /**
     * Recommend workflow routing (department/team)
     */
    AIResponse<Map<String, Object>> recommendRouting(AIRequest request);

    /**
     * Recommend task assignment (user selection)
     */
    AIResponse<Map<String, Object>> recommendAssignment(AIRequest request);

    /**
     * Calculate task priority
     */
    AIResponse<Map<String, Object>> calculatePriority(AIRequest request);

    /**
     * Predict next actions in workflow
     */
    AIResponse<Map<String, Object>> predictActions(AIRequest request);
}

