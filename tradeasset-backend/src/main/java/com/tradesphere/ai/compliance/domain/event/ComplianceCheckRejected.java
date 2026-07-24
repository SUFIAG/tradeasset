package com.tradesphere.ai.compliance.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Compliance Check Rejected
 * Published when a compliance check is rejected by a compliance officer
 */
@Getter
public class ComplianceCheckRejected extends BaseDomainEvent {

    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final UUID rejectedBy;
    private final String reason;

    public ComplianceCheckRejected(
            UUID complianceCheckId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            UUID rejectedBy,
            String reason) {
        super(complianceCheckId.toString(), "ComplianceCheck", rejectedBy);
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.rejectedBy = rejectedBy;
        this.reason = reason;
    }
}

