package com.tradesphere.ai.workflow.adapter.in.rest.dto;

import com.tradesphere.ai.workflow.domain.model.TaskPriority;
import com.tradesphere.ai.workflow.domain.model.WorkflowType;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

/**
 * REST DTO for creating a workflow
 */
@Data
public class CreateWorkflowRequest {

    @NotNull(message = "Workflow type is required")
    private WorkflowType type;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    @NotNull(message = "Entity ID is required")
    private UUID entityId;

    @NotBlank(message = "Initiator name is required")
    private String initiatorName;

    @Min(value = 1, message = "SLA hours must be at least 1")
    @Max(value = 720, message = "SLA hours cannot exceed 720 (30 days)")
    private int slaHours = 24; // Default 24 hours

    @NotEmpty(message = "At least one task is required")
    @Valid
    private List<TaskDefinitionRequest> tasks;

    @Data
    public static class TaskDefinitionRequest {

        @NotBlank(message = "Task name is required")
        private String taskName;

        private String description;

        @NotNull(message = "Task priority is required")
        private TaskPriority priority;

        @Min(value = 1, message = "Sequence order must be at least 1")
        private int sequenceOrder;

        @Min(value = 1, message = "Task SLA hours must be at least 1")
        private int taskSlaHours = 24;

        private UUID assignedTo;
        private String assignedToName;
    }
}


