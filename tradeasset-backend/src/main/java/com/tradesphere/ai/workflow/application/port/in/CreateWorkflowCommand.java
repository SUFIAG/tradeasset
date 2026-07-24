package com.tradesphere.ai.workflow.application.port.in;

import com.tradesphere.ai.workflow.domain.model.TaskPriority;
import com.tradesphere.ai.workflow.domain.model.WorkflowType;
import lombok.Value;

import java.util.List;
import java.util.UUID;

/**
 * Command to create a new workflow
 */
@Value
public class CreateWorkflowCommand {
    UUID tenantId;
    WorkflowType type;
    String entityType;
    UUID entityId;
    UUID initiatedBy;
    String initiatorName;
    int slaHours;
    List<TaskDefinition> tasks;

    @Value
    public static class TaskDefinition {
        String taskName;
        String description;
        TaskPriority priority;
        int sequenceOrder;
        int taskSlaHours;
        UUID assignedTo;
        String assignedToName;
    }
}


