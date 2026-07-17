package com.tradesphere.ai.compliance.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Compliance Check Cleared as False Positive
 * Published when a compliance check hit is cleared as a false positive
 */
@Getter
public class ComplianceCheckClearedAsFalsePositive extends BaseDomainEvent {

    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final UUID clearedBy;

    public ComplianceCheckClearedAsFalsePositive(
            UUID complianceCheckId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            UUID clearedBy) {
        super(complianceCheckId.toString(), "ComplianceCheck", clearedBy);
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.clearedBy = clearedBy;
    }
}

