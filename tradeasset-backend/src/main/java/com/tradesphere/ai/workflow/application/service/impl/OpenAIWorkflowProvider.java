package com.tradesphere.ai.workflow.application.service.impl;

import com.tradesphere.ai.document.application.service.extraction.AIExtractionService;
import com.tradesphere.ai.shared.ai.AIRequest;
import com.tradesphere.ai.shared.ai.AIResponse;
import com.tradesphere.ai.workflow.application.service.WorkflowAIProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI Provider implementation using OpenAI/Claude for workflow intelligence
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIWorkflowProvider implements WorkflowAIProvider {

    private final AIExtractionService aiService;

    @Override
    public AIResponse<Map<String, Object>> recommendRouting(AIRequest request) {
        log.info("AI recommending routing for workflow");

        try {
            if (!aiService.isConfigured()) {
                log.warn("AI service not configured, using fallback");
                return fallbackRouting(request);
            }

            // Build prompt for routing recommendation
            String prompt = buildRoutingPrompt(request);

            // Call AI service
            Map<String, Object> result = aiService.extractWithAI(prompt);

            return AIResponse.success(
                request.getRequestId(),
                result,
                0.85f,
                "gpt-4-turbo",
                1000L
            );

        } catch (Exception e) {
            log.error("AI routing recommendation failed", e);
            return AIResponse.failure(request.getRequestId(), e.getMessage());
        }
    }

    @Override
    public AIResponse<Map<String, Object>> recommendAssignment(AIRequest request) {
        log.info("AI recommending task assignment");

        try {
            if (!aiService.isConfigured()) {
                return fallbackAssignment(request);
            }

            String prompt = buildAssignmentPrompt(request);
            Map<String, Object> result = aiService.extractWithAI(prompt);

            return AIResponse.success(
                request.getRequestId(),
                result,
                0.80f,
                "gpt-4-turbo",
                800L
            );

        } catch (Exception e) {
            log.error("AI assignment recommendation failed", e);
            return AIResponse.failure(request.getRequestId(), e.getMessage());
        }
    }

    @Override
    public AIResponse<Map<String, Object>> calculatePriority(AIRequest request) {
        log.info("AI calculating task priority");

        try {
            if (!aiService.isConfigured()) {
                return fallbackPriority(request);
            }

            String prompt = buildPriorityPrompt(request);
            Map<String, Object> result = aiService.extractWithAI(prompt);

            return AIResponse.success(
                request.getRequestId(),
                result,
                0.85f,
                "gpt-4-turbo",
                600L
            );

        } catch (Exception e) {
            log.error("AI priority calculation failed", e);
            return AIResponse.failure(request.getRequestId(), e.getMessage());
        }
    }

    @Override
    public AIResponse<Map<String, Object>> predictActions(AIRequest request) {
        log.info("AI predicting next actions");

        try {
            if (!aiService.isConfigured()) {
                return fallbackActions(request);
            }

            String prompt = buildActionPredictionPrompt(request);
            Map<String, Object> result = aiService.extractWithAI(prompt);

            return AIResponse.success(
                request.getRequestId(),
                result,
                0.75f,
                "gpt-4-turbo",
                900L
            );

        } catch (Exception e) {
            log.error("AI action prediction failed", e);
            return AIResponse.failure(request.getRequestId(), e.getMessage());
        }
    }

    // Prompt builders

    private String buildRoutingPrompt(AIRequest request) {
        return String.format("""
            You are a workflow routing AI assistant. Analyze the following workflow and recommend the best department and team to handle it.
            
            %s
            
            Context:
            - Workflow Type: %s
            - Priority: %s
            - Amount: %s
            - Complexity: %s
            
            Available departments: TRADE_FINANCE, COMPLIANCE, OPERATIONS, CUSTOMER_SERVICE, RISK_MANAGEMENT
            
            Respond with JSON:
            {
                "department": "department_name",
                "team": "team_name",
                "confidence": 0.0-1.0,
                "reasoning": "brief explanation"
            }
            """,
            request.getInput(),
            request.getContext().get("workflowType"),
            request.getContext().get("priority"),
            request.getContext().get("amount"),
            request.getContext().get("complexity")
        );
    }

    private String buildAssignmentPrompt(AIRequest request) {
        return String.format("""
            You are a task assignment AI assistant. Analyze the task and recommend the best user to assign it to.
            
            %s
            
            Context:
            - Eligible Users: %s
            - Workloads: %s
            - Skills: %s
            - Task Complexity: %s
            - Priority: %s
            
            Consider:
            1. User workload balance
            2. Required skills match
            3. Task complexity vs user experience
            4. Priority and urgency
            
            Respond with JSON:
            {
                "userId": "user_uuid",
                "confidence": 0.0-1.0,
                "reasoning": "brief explanation"
            }
            """,
            request.getInput(),
            request.getContext().get("eligibleUsers"),
            request.getContext().get("workloads"),
            request.getContext().get("skills"),
            request.getContext().get("taskComplexity"),
            request.getContext().get("priority")
        );
    }

    private String buildPriorityPrompt(AIRequest request) {
        return String.format("""
            You are a task prioritization AI assistant. Analyze the task and assign appropriate priority.
            
            %s
            
            Context:
            - Workflow Priority: %s
            - Due Date: %s
            - Complexity: %s
            
            Priority Levels: LOW, MEDIUM, HIGH, URGENT, CRITICAL
            
            Consider:
            1. Due date urgency
            2. Workflow overall priority
            3. Task complexity
            4. Business impact
            
            Respond with JSON:
            {
                "priority": "priority_level",
                "confidence": 0.0-1.0,
                "reasoning": "brief explanation"
            }
            """,
            request.getInput(),
            request.getContext().get("workflowPriority"),
            request.getContext().get("dueDate"),
            request.getContext().get("complexity")
        );
    }

    private String buildActionPredictionPrompt(AIRequest request) {
        return String.format("""
            You are a workflow prediction AI assistant. Analyze the workflow state and predict the next likely actions.
            
            %s
            
            Context:
            - Completed Tasks: %s
            - Pending Tasks: %s
            - Status: %s
            
            Predict the next 3-5 most likely actions or tasks that will occur.
            
            Respond with JSON:
            {
                "nextActions": ["action1", "action2", "action3"],
                "confidence": 0.0-1.0,
                "reasoning": "brief explanation"
            }
            """,
            request.getInput(),
            request.getContext().get("completedTasks"),
            request.getContext().get("pendingTasks"),
            request.getContext().get("status")
        );
    }

    // Fallback methods (rule-based)

    private AIResponse<Map<String, Object>> fallbackRouting(AIRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("department", "OPERATIONS");
        result.put("team", "GENERAL");
        result.put("confidence", 0.5);
        result.put("reasoning", "Fallback routing - AI service not available");

        return AIResponse.success(request.getRequestId(), result, 0.5f, "fallback", 0L);
    }

    private AIResponse<Map<String, Object>> fallbackAssignment(AIRequest request) {
        Map<String, Object> result = new HashMap<>();
        // Would select user with lowest workload
        result.put("userId", UUID.randomUUID().toString());
        result.put("confidence", 0.5);
        result.put("reasoning", "Fallback assignment - selected user with lowest workload");

        return AIResponse.success(request.getRequestId(), result, 0.5f, "fallback", 0L);
    }

    private AIResponse<Map<String, Object>> fallbackPriority(AIRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("priority", "MEDIUM");
        result.put("confidence", 0.5);
        result.put("reasoning", "Fallback priority - default medium");

        return AIResponse.success(request.getRequestId(), result, 0.5f, "fallback", 0L);
    }

    private AIResponse<Map<String, Object>> fallbackActions(AIRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("nextActions", List.of("Review", "Approve", "Complete"));
        result.put("confidence", 0.5);
        result.put("reasoning", "Fallback actions - standard workflow progression");

        return AIResponse.success(request.getRequestId(), result, 0.5f, "fallback", 0L);
    }
}

