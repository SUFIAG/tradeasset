package com.tradesphere.ai.risk.domain.event;

import com.tradesphere.ai.risk.domain.model.RiskLevel;
import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Risk Assessment Completed
 */
@Getter
public class RiskAssessmentCompleted extends BaseDomainEvent {
    
    private final UUID tenantId;
    private final String entityType;
    private final UUID entityId;
    private final Integer overallRiskScore;
    private final RiskLevel riskLevel;
    
    public RiskAssessmentCompleted(
            UUID riskAssessmentId,
            UUID tenantId,
            String entityType,
            UUID entityId,
            Integer overallRiskScore,
            RiskLevel riskLevel) {
        super(riskAssessmentId.toString(), "RiskAssessment");
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.overallRiskScore = overallRiskScore;
        this.riskLevel = riskLevel;
    }
}

