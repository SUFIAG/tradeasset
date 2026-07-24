package com.tradesphere.ai.compliance.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Compliance Check Approved
 * Published when a compliance check is approved by a compliance officer
 */
@Getter
public class ComplianceCheckApproved extends BaseDomainEvent {

    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final UUID approvedBy;

    public ComplianceCheckApproved(
            UUID complianceCheckId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            UUID approvedBy) {
        super(complianceCheckId.toString(), "ComplianceCheck", approvedBy);
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.approvedBy = approvedBy;
    }
}

