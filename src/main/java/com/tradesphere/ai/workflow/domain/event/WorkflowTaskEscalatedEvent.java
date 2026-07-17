package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow task escalated
 */
@Getter
public class WorkflowTaskEscalatedEvent extends BaseDomainEvent {

    private final UUID workflowId;
    private final UUID tenantId;
    private final UUID taskId;
    private final UUID escalatedTo;

    public WorkflowTaskEscalatedEvent(
            UUID workflowId,
            UUID tenantId,
            UUID taskId,
            UUID escalatedTo) {

        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.taskId = taskId;
        this.escalatedTo = escalatedTo;
    }
}


