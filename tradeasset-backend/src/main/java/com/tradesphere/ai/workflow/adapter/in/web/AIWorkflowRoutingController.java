package com.tradesphere.ai.workflow.adapter.in.web;

import com.tradesphere.ai.workflow.application.service.AIWorkflowRoutingService;
import com.tradesphere.ai.workflow.application.service.AIWorkflowRoutingService.WorkflowOptimization;
import com.tradesphere.ai.workflow.application.service.AIWorkflowRoutingService.WorkflowRoutingDecision;
import com.tradesphere.ai.workflow.application.service.UserSkillService;
import com.tradesphere.ai.workflow.application.service.UserWorkloadService;
import com.tradesphere.ai.workflow.application.service.UserWorkloadService.WorkloadStatistics;
import com.tradesphere.ai.workflow.application.port.out.WorkflowRepository;
import com.tradesphere.ai.workflow.domain.model.TaskPriority;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;
import com.tradesphere.ai.workflow.domain.model.WorkflowTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * REST API for AI-powered workflow routing and assignment
 */
@RestController
@RequestMapping("/api/v1/workflow/ai")
@Tag(name = "AI Workflow Routing", description = "AI-powered intelligent workflow routing and task assignment")
@RequiredArgsConstructor
@Slf4j
public class AIWorkflowRoutingController {

    private final AIWorkflowRoutingService routingService;
    private final UserWorkloadService workloadService;
    private final UserSkillService skillService;
    private final WorkflowRepository workflowRepository;

    /**
     * Route workflow using AI
     */
    @PostMapping("/workflows/{workflowId}/route")
    @Operation(summary = "AI route workflow", description = "Use AI to route workflow to appropriate department/team")
    public ResponseEntity<WorkflowRoutingDecision> routeWorkflow(
        @PathVariable UUID workflowId,
        @RequestParam UUID tenantId
    ) {
        log.info("AI routing request for workflow: {}", workflowId);

        Workflow workflow = workflowRepository.findById(
            WorkflowId.of(workflowId), tenantId
        ).orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowRoutingDecision decision = routingService.routeWorkflow(workflow);

        return ResponseEntity.ok(decision);
    }

    /**
     * Auto-assign task using AI
     */
    @PostMapping("/workflows/{workflowId}/tasks/{taskId}/auto-assign")
    @Operation(summary = "AI auto-assign task", description = "Use AI to assign task to best available user")
    public ResponseEntity<TaskAssignmentResponse> autoAssignTask(
        @PathVariable UUID workflowId,
        @PathVariable UUID taskId,
        @RequestParam UUID tenantId,
        @RequestBody AutoAssignRequest request
    ) {
        log.info("AI auto-assign request for task: {}", taskId);

        Workflow workflow = workflowRepository.findById(
            WorkflowId.of(workflowId), tenantId
        ).orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowTask task = workflow.getTasks().stream()
            .filter(t -> t.getId().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Task not found"));

        UUID assignedUserId = routingService.assignTask(task, workflow, request.eligibleUsers());

        if (assignedUserId != null) {
            workloadService.incrementWorkload(assignedUserId);
        }

        return ResponseEntity.ok(new TaskAssignmentResponse(assignedUserId, "AI assignment successful"));
    }

    /**
     * Calculate task priority using AI
     */
    @PostMapping("/workflows/{workflowId}/tasks/{taskId}/calculate-priority")
    @Operation(summary = "AI calculate priority", description = "Use AI to calculate appropriate task priority")
    public ResponseEntity<TaskPriority> calculatePriority(
        @PathVariable UUID workflowId,
        @PathVariable UUID taskId,
        @RequestParam UUID tenantId
    ) {
        log.info("AI priority calculation for task: {}", taskId);

        Workflow workflow = workflowRepository.findById(
            WorkflowId.of(workflowId), tenantId
        ).orElseThrow(() -> new RuntimeException("Workflow not found"));

        WorkflowTask task = workflow.getTasks().stream()
            .filter(t -> t.getId().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskPriority priority = routingService.calculatePriority(task, workflow);

        return ResponseEntity.ok(priority);
    }

    /**
     * Predict next actions using AI
     */
    @GetMapping("/workflows/{workflowId}/predict-actions")
    @Operation(summary = "AI predict actions", description = "Use AI to predict next likely actions in workflow")
    public ResponseEntity<List<String>> predictNextActions(
        @PathVariable UUID workflowId,
        @RequestParam UUID tenantId
    ) {
        log.info("AI action prediction for workflow: {}", workflowId);

        Workflow workflow = workflowRepository.findById(
            WorkflowId.of(workflowId), tenantId
        ).orElseThrow(() -> new RuntimeException("Workflow not found"));

        List<String> actions = routingService.predictNextActions(workflow);

        return ResponseEntity.ok(actions);
    }

    /**
     * Get workflow optimization recommendations
     */
    @GetMapping("/workflows/{workflowId}/optimizations")
    @Operation(summary = "Get optimization recommendations", description = "Get AI-powered optimization recommendations for workflow")
    public ResponseEntity<List<WorkflowOptimization>> getOptimizations(
        @PathVariable UUID workflowId,
        @RequestParam UUID tenantId
    ) {
        log.info("Getting optimization recommendations for workflow: {}", workflowId);

        Workflow workflow = workflowRepository.findById(
            WorkflowId.of(workflowId), tenantId
        ).orElseThrow(() -> new RuntimeException("Workflow not found"));

        List<WorkflowOptimization> optimizations = routingService.recommendOptimizations(workflow);

        return ResponseEntity.ok(optimizations);
    }

    /**
     * Get user workload statistics
     */
    @GetMapping("/workload/statistics")
    @Operation(summary = "Get workload statistics", description = "Get current workload statistics across all users")
    public ResponseEntity<WorkloadStatistics> getWorkloadStatistics() {
        WorkloadStatistics stats = workloadService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get user skills
     */
    @GetMapping("/users/{userId}/skills")
    @Operation(summary = "Get user skills", description = "Get skills for a specific user")
    public ResponseEntity<Set<String>> getUserSkills(
        @PathVariable UUID userId
    ) {
        Set<String> skills = skillService.getSkills(userId);
        return ResponseEntity.ok(skills);
    }

    /**
     * Add skill to user
     */
    @PostMapping("/users/{userId}/skills")
    @Operation(summary = "Add user skill", description = "Add a skill to user profile")
    public ResponseEntity<Void> addUserSkill(
        @PathVariable UUID userId,
        @RequestBody AddSkillRequest request
    ) {
        skillService.addSkill(userId, request.skill(), request.level());
        return ResponseEntity.ok().build();
    }

    // DTOs

    public record AutoAssignRequest(
        List<UUID> eligibleUsers
    ) {}

    public record TaskAssignmentResponse(
        UUID assignedUserId,
        String message
    ) {}

    public record AddSkillRequest(
        String skill,
        UserSkillService.SkillLevel level
    ) {}
}

