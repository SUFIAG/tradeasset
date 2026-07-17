package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow completed
 */
@Getter
public class WorkflowCompletedEvent extends BaseDomainEvent {
    
    private final UUID workflowId;
    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final String outcome;
    
    public WorkflowCompletedEvent(
            UUID workflowId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            String outcome) {
        
        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.outcome = outcome;
    }
}


