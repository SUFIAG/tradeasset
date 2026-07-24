package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow rejected
 */
@Getter
public class WorkflowRejectedEvent extends BaseDomainEvent {
    
    private final UUID workflowId;
    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final UUID rejectedBy;
    private final String reason;
    
    public WorkflowRejectedEvent(
            UUID workflowId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            UUID rejectedBy,
            String reason) {
        
        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.rejectedBy = rejectedBy;
        this.reason = reason;
    }
}


