package com.tradesphere.ai.risk.domain.event;

import com.tradesphere.ai.risk.domain.model.RiskType;
import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Risk Assessment Initiated
 */
@Getter
public class RiskAssessmentInitiated extends BaseDomainEvent {

    private final UUID tenantId;
    private final RiskType riskType;
    private final String entityType;
    private final UUID entityId;

    public RiskAssessmentInitiated(
            UUID riskAssessmentId,
            UUID tenantId,
            RiskType riskType,
            String entityType,
            UUID entityId) {
        super(riskAssessmentId.toString(), "RiskAssessment");
        this.tenantId = tenantId;
        this.riskType = riskType;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}

