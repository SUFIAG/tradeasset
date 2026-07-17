package com.tradesphere.ai.risk.domain.event;

import com.tradesphere.ai.risk.domain.model.RiskLevel;
import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: High Risk Detected
 * Published when risk assessment identifies high or very high risk
 */
@Getter
public class HighRiskDetected extends BaseDomainEvent {

    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final RiskLevel riskLevel;
    private final String reasoning;

    public HighRiskDetected(
            UUID riskAssessmentId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            RiskLevel riskLevel,
            String reasoning) {
        super(riskAssessmentId.toString(), "RiskAssessment");
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.riskLevel = riskLevel;
        this.reasoning = reasoning;
    }
}

