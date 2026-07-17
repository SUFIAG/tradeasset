package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.shared.ai.AIIntent;
import com.tradesphere.ai.shared.ai.AIRequest;
import com.tradesphere.ai.shared.ai.AIResponse;
import com.tradesphere.ai.workflow.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-powered Workflow Routing Service
 * Uses AI to intelligently route tasks, assign users, and prioritize work
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIWorkflowRoutingService {

    private final WorkflowAIProvider aiProvider;
    private final UserWorkloadService workloadService;
    private final UserSkillService skillService;

    /**
     * Intelligently route workflow to appropriate department/team
     */
    public WorkflowRoutingDecision routeWorkflow(Workflow workflow) {
        log.info("AI routing workflow: id={}, type={}", workflow.getId(), workflow.getType());

        try {
            // Build AI request with workflow context
            AIRequest request = AIRequest.builder()
                .intent(AIIntent.RECOMMEND_WORKFLOW)
                .input(buildWorkflowContext(workflow))
                .addContext("workflowType", workflow.getType().name())
                .addContext("priority", workflow.getPriority().name())
                .addContext("amount", getWorkflowAmount(workflow))
                .addContext("complexity", assessComplexity(workflow))
                .tenantId(workflow.getTenantId().toString())
                .build();

            // Get AI recommendation
            AIResponse<Map<String, Object>> response = aiProvider.recommendRouting(request);

            if (response.isSuccess()) {
                return parseRoutingDecision(response.getResult(), workflow);
            } else {
                log.warn("AI routing failed, using fallback: {}", response.getErrorMessage());
                return fallbackRouting(workflow);
            }

        } catch (Exception e) {
            log.error("AI routing error, using fallback", e);
            return fallbackRouting(workflow);
        }
    }

    /**
     * Auto-assign task to best available user using AI
     */
    public UUID assignTask(WorkflowTask task, Workflow workflow, List<UUID> eligibleUsers) {
        log.info("AI assigning task: taskId={}, type={}", task.getId(), task.getTaskName());

        if (eligibleUsers == null || eligibleUsers.isEmpty()) {
            log.warn("No eligible users for task assignment");
            return null;
        }

        try {
            // Get user workloads
            Map<UUID, Integer> workloads = workloadService.getCurrentWorkloads(eligibleUsers);

            // Get user skills
            Map<UUID, Set<String>> skills = skillService.getUserSkills(eligibleUsers);

            // Build AI request
            AIRequest request = AIRequest.builder()
                .intent(AIIntent.AUTO_ASSIGN)
                .input(buildTaskContext(task, workflow))
                .addContext("eligibleUsers", eligibleUsers.size())
                .addContext("taskComplexity", task.getComplexity())
                .addContext("priority", task.getPriority().name())
                .addContext("workloads", workloads)
                .addContext("skills", skills)
                .tenantId(workflow.getTenantId().toString())
                .build();

            // Get AI recommendation
            AIResponse<Map<String, Object>> response = aiProvider.recommendAssignment(request);

            if (response.isSuccess()) {
                String userId = (String) response.getResult().get("userId");
                Float confidence = response.getConfidence();

                log.info("AI assigned task to user: {} (confidence: {})", userId, confidence);

                if (confidence > 0.7f) {
                    return UUID.fromString(userId);
                }
            }

            // Fallback to workload-based assignment
            return assignByWorkload(eligibleUsers, workloads);

        } catch (Exception e) {
            log.error("AI assignment error, using fallback", e);
            return eligibleUsers.get(0); // Simple fallback
        }
    }

    /**
     * Calculate task priority using AI
     */
    public TaskPriority calculatePriority(WorkflowTask task, Workflow workflow) {
        log.debug("AI calculating task priority: taskId={}", task.getId());

        try {
            AIRequest request = AIRequest.builder()
                .intent(AIIntent.PRIORITIZE_TASKS)
                .input(buildTaskContext(task, workflow))
                .addContext("workflowPriority", workflow.getPriority())
                .addContext("dueDate", task.getDueDate())
                .addContext("complexity", task.getComplexity())
                .tenantId(workflow.getTenantId().toString())
                .build();

            AIResponse<Map<String, Object>> response = aiProvider.calculatePriority(request);

            if (response.isSuccess()) {
                String priorityStr = (String) response.getResult().get("priority");
                return TaskPriority.valueOf(priorityStr);
            }

        } catch (Exception e) {
            log.error("AI priority calculation error", e);
        }

        // Fallback priority logic
        return calculateFallbackPriority(task, workflow);
    }

    /**
     * Predict next action in workflow using AI
     */
    public List<String> predictNextActions(Workflow workflow) {
        log.debug("AI predicting next actions: workflowId={}", workflow.getId());

        try {
            AIRequest request = AIRequest.builder()
                .intent(AIIntent.PREDICT_NEXT_ACTION)
                .input(buildWorkflowContext(workflow))
                .addContext("completedTasks", workflow.getCompletedTasks().size())
                .addContext("pendingTasks", workflow.getPendingTasks().size())
                .addContext("status", workflow.getStatus().name())
                .tenantId(workflow.getTenantId().toString())
                .build();

            AIResponse<Map<String, Object>> response = aiProvider.predictActions(request);

            if (response.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<String> actions = (List<String>) response.getResult().get("nextActions");
                return actions;
            }

        } catch (Exception e) {
            log.error("AI prediction error", e);
        }

        return Collections.emptyList();
    }

    /**
     * Recommend workflow optimizations
     */
    public List<WorkflowOptimization> recommendOptimizations(Workflow workflow) {
        log.info("AI analyzing workflow for optimizations: workflowId={}", workflow.getId());

        List<WorkflowOptimization> optimizations = new ArrayList<>();

        // Check for bottlenecks
        if (hasBottleneck(workflow)) {
            optimizations.add(new WorkflowOptimization(
                "BOTTLENECK_DETECTED",
                "Task assignment bottleneck detected",
                "Consider parallel task execution or additional resources",
                WorkflowOptimization.Severity.MEDIUM
            ));
        }

        // Check for overdue tasks
        long overdueTasks = workflow.getPendingTasks().stream()
            .filter(WorkflowTask::isOverdue)
            .count();

        if (overdueTasks > 0) {
            optimizations.add(new WorkflowOptimization(
                "OVERDUE_TASKS",
                overdueTasks + " tasks are overdue",
                "Reassign tasks or escalate to supervisors",
                WorkflowOptimization.Severity.HIGH
            ));
        }

        // Check for unbalanced workload
        if (hasUnbalancedWorkload(workflow)) {
            optimizations.add(new WorkflowOptimization(
                "WORKLOAD_IMBALANCE",
                "Task distribution is uneven",
                "Redistribute tasks across team members",
                WorkflowOptimization.Severity.LOW
            ));
        }

        return optimizations;
    }

    // Helper methods

    private String buildWorkflowContext(Workflow workflow) {
        return String.format(
            "Workflow Type: %s\n" +
            "Priority: %s\n" +
            "Status: %s\n" +
            "Total Tasks: %d\n" +
            "Pending Tasks: %d\n" +
            "Completed Tasks: %d\n" +
            "Created: %s\n" +
            "Due: %s",
            workflow.getType(),
            workflow.getPriority(),
            workflow.getStatus(),
            workflow.getTasks().size(),
            workflow.getPendingTasks().size(),
            workflow.getCompletedTasks().size(),
            workflow.getCreatedAt(),
            workflow.getDueDate()
        );
    }

    private String buildTaskContext(WorkflowTask task, Workflow workflow) {
        return String.format(
            "Task: %s\n" +
            "Workflow Type: %s\n" +
            "Priority: %s\n" +
            "Complexity: %s\n" +
            "Due Date: %s\n" +
            "Description: %s",
            task.getTaskName(),
            workflow.getType(),
            task.getPriority(),
            task.getComplexity(),
            task.getDueDate(),
            task.getTaskDescription()
        );
    }

    private Double getWorkflowAmount(Workflow workflow) {
        // Extract amount from workflow context if available
        return 0.0; // Placeholder
    }

    private String assessComplexity(Workflow workflow) {
        int taskCount = workflow.getTasks().size();
        if (taskCount > 10) return "HIGH";
        if (taskCount > 5) return "MEDIUM";
        return "LOW";
    }

    private WorkflowRoutingDecision parseRoutingDecision(
        Map<String, Object> aiResult,
        Workflow workflow
    ) {
        String department = (String) aiResult.getOrDefault("department", "OPERATIONS");
        String team = (String) aiResult.getOrDefault("team", "GENERAL");
        Float confidence = ((Number) aiResult.getOrDefault("confidence", 0.8)).floatValue();

        return new WorkflowRoutingDecision(department, team, confidence);
    }

    private WorkflowRoutingDecision fallbackRouting(Workflow workflow) {
        // Simple rule-based routing
        String department = switch (workflow.getType()) {
            case LC_APPROVAL -> "TRADE_FINANCE";
            case CUSTOMER_APPROVAL -> "CUSTOMER_SERVICE";
            case COMPLIANCE_REVIEW -> "COMPLIANCE";
            case DOCUMENT_VERIFICATION -> "OPERATIONS";
            case LC_AMENDMENT -> "TRADE_FINANCE";
            case PAYMENT_AUTHORIZATION -> "FINANCE";
            case RISK_ASSESSMENT -> "RISK_MANAGEMENT";
            case CREDIT_LIMIT_APPROVAL -> "CREDIT";
            default -> "OPERATIONS";
        };

        return new WorkflowRoutingDecision(department, "GENERAL", 0.6f);
    }

    private UUID assignByWorkload(List<UUID> users, Map<UUID, Integer> workloads) {
        return users.stream()
            .min(Comparator.comparingInt(u -> workloads.getOrDefault(u, 0)))
            .orElse(users.get(0));
    }

    private TaskPriority calculateFallbackPriority(WorkflowTask task, Workflow workflow) {
        // Rule-based priority
        if (workflow.getPriority().ordinal() >= 3) { // HIGH or URGENT or CRITICAL
            return TaskPriority.HIGH;
        }
        if (task.isOverdue()) {
            return TaskPriority.HIGH;
        }
        return TaskPriority.NORMAL;
    }

    private boolean hasBottleneck(Workflow workflow) {
        // Simple bottleneck detection
        long stuckTasks = workflow.getPendingTasks().stream()
            .filter(t -> t.getCreatedAt().isBefore(
                java.time.Instant.now().minus(2, java.time.temporal.ChronoUnit.DAYS)
            ))
            .count();
        return stuckTasks > workflow.getTasks().size() / 3;
    }

    private boolean hasUnbalancedWorkload(Workflow workflow) {
        Map<UUID, Long> taskCounts = workflow.getPendingTasks().stream()
            .filter(t -> t.getAssignedTo() != null)
            .collect(Collectors.groupingBy(WorkflowTask::getAssignedTo, Collectors.counting()));

        if (taskCounts.isEmpty()) return false;

        long max = Collections.max(taskCounts.values());
        long min = Collections.min(taskCounts.values());

        return max > min * 2; // More than 2x difference
    }

    /**
     * Routing decision result
     */
    public record WorkflowRoutingDecision(
        String department,
        String team,
        Float confidence
    ) {}

    /**
     * Workflow optimization recommendation
     */
    public record WorkflowOptimization(
        String type,
        String issue,
        String recommendation,
        Severity severity
    ) {
        public enum Severity {
            LOW, MEDIUM, HIGH, CRITICAL
        }
    }
}

