package com.tradesphere.ai.compliance.domain.event;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheckStatus;
import com.tradesphere.ai.compliance.domain.model.ComplianceDecision;
import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Compliance Screening Completed
 * Published when compliance screening is completed (with or without hits)
 */
@Getter
public class ComplianceScreeningCompleted extends BaseDomainEvent {

    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final ComplianceCheckStatus status;
    private final ComplianceDecision decision;

    public ComplianceScreeningCompleted(
            UUID complianceCheckId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            ComplianceCheckStatus status,
            ComplianceDecision decision) {
        super(complianceCheckId.toString(), "ComplianceCheck");
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.status = status;
        this.decision = decision;
    }
}

