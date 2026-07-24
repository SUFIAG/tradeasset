package com.tradesphere.ai.compliance.domain.event;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheckType;
import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Compliance Check Initiated
 * Published when a new compliance check is created
 */
@Getter
public class ComplianceCheckInitiated extends BaseDomainEvent {

    private final UUID tenantId;
    private final ComplianceCheckType checkType;
    private final String entityType;
    private final UUID entityId;

    public ComplianceCheckInitiated(
            UUID complianceCheckId,
            UUID tenantId,
            ComplianceCheckType checkType,
            String entityType,
            UUID entityId) {
        super(complianceCheckId.toString(), "ComplianceCheck");
        this.tenantId = tenantId;
        this.checkType = checkType;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}

