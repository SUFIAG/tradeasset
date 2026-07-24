package com.tradesphere.ai.workflow.adapter.in.rest;

import com.tradesphere.ai.workflow.adapter.in.rest.dto.*;
import com.tradesphere.ai.workflow.application.port.in.*;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for workflow operations
 * Provides APIs for creating, managing, and querying workflows
 */
@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Workflow", description = "Workflow management APIs")
public class WorkflowController {

    private final CreateWorkflowUseCase createWorkflowUseCase;
    private final ApproveTaskUseCase approveTaskUseCase;
    private final RejectTaskUseCase rejectTaskUseCase;
    private final GetWorkflowUseCase getWorkflowUseCase;

    /**
     * Create and start a new workflow
     */
    @PostMapping
    @Operation(summary = "Create workflow", description = "Create and start a new approval workflow")
    public ResponseEntity<WorkflowResponse> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Creating workflow: type={}, entityType={}, tenantId={}",
                request.getType(), request.getEntityType(), tenantId);

        // Map to command
        CreateWorkflowCommand command = new CreateWorkflowCommand(
                tenantId,
                request.getType(),
                request.getEntityType(),
                request.getEntityId(),
                userId,
                request.getInitiatorName(),
                request.getSlaHours(),
                request.getTasks().stream()
                        .map(taskReq -> new CreateWorkflowCommand.TaskDefinition(
                                taskReq.getTaskName(),
                                taskReq.getDescription(),
                                taskReq.getPriority(),
                                taskReq.getSequenceOrder(),
                                taskReq.getTaskSlaHours(),
                                taskReq.getAssignedTo(),
                                taskReq.getAssignedToName()
                        ))
                        .toList()
        );

        // Execute use case
        WorkflowId workflowId = createWorkflowUseCase.createWorkflow(command);

        // Get and return created workflow
        Workflow workflow = getWorkflowUseCase.getWorkflow(workflowId, tenantId)
                .orElseThrow(() -> new RuntimeException("Workflow not found after creation"));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WorkflowResponse.fromDomain(workflow));
    }

    /**
     * Get workflow by ID
     */
    @GetMapping("/{workflowId}")
    @Operation(summary = "Get workflow", description = "Get workflow by ID")
    public ResponseEntity<WorkflowResponse> getWorkflow(
            @PathVariable UUID workflowId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.debug("Getting workflow: id={}, tenantId={}", workflowId, tenantId);

        return getWorkflowUseCase.getWorkflow(WorkflowId.of(workflowId), tenantId)
                .map(WorkflowResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get workflow by entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get workflow by entity", description = "Get workflow for a specific entity")
    public ResponseEntity<WorkflowResponse> getWorkflowByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        log.debug("Getting workflow by entity: type={}, id={}, tenantId={}",
                entityType, entityId, tenantId);

        return getWorkflowUseCase.getWorkflowByEntity(entityType, entityId, tenantId)
                .map(WorkflowResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Approve a workflow task
     */
    @PostMapping("/{workflowId}/tasks/approve")
    @Operation(summary = "Approve task", description = "Approve a workflow task")
    public ResponseEntity<Void> approveTask(
            @PathVariable UUID workflowId,
            @Valid @RequestBody ApproveTaskRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Approving task: workflowId={}, taskId={}, userId={}",
                workflowId, request.getTaskId(), userId);

        ApproveTaskCommand command = new ApproveTaskCommand(
                workflowId,
                request.getTaskId(),
                userId,
                request.getComments(),
                tenantId
        );

        approveTaskUseCase.approveTask(command);

        return ResponseEntity.ok().build();
    }

    /**
     * Reject a workflow task
     */
    @PostMapping("/{workflowId}/tasks/reject")
    @Operation(summary = "Reject task", description = "Reject a workflow task (fails entire workflow)")
    public ResponseEntity<Void> rejectTask(
            @PathVariable UUID workflowId,
            @Valid @RequestBody RejectTaskRequest request,
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-User-ID") UUID userId) {

        log.info("Rejecting task: workflowId={}, taskId={}, userId={}",
                workflowId, request.getTaskId(), userId);

        RejectTaskCommand command = new RejectTaskCommand(
                workflowId,
                request.getTaskId(),
                userId,
                request.getReason(),
                tenantId
        );

        rejectTaskUseCase.rejectTask(command);

        return ResponseEntity.ok().build();
    }
}


