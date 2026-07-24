package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.workflow.domain.model.WorkflowType;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow created
 */
@Getter
public class WorkflowCreatedEvent extends BaseDomainEvent {

    private final UUID workflowId;
    private final UUID tenantId;
    private final WorkflowType workflowType;
    private final String entityType;
    private final UUID entityId;

    public WorkflowCreatedEvent(
            UUID workflowId,
            UUID tenantId,
            WorkflowType workflowType,
            String entityType,
            UUID entityId) {

        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.workflowType = workflowType;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}


