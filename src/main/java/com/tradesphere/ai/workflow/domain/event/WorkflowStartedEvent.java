package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow started
 */
@Getter
public class WorkflowStartedEvent extends BaseDomainEvent {
    
    private final UUID workflowId;
    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    
    public WorkflowStartedEvent(
            UUID workflowId,
            UUID tenantId,
            String entityType,
            UUID entityId) {
        
        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}


