package com.tradesphere.ai.risk.application.port.in;

import com.tradesphere.ai.risk.domain.model.RiskType;
import lombok.Value;

import java.util.UUID;

/**
 * Command to initiate a risk assessment
 */
@Value
public class InitiateRiskAssessmentCommand {
    UUID tenantId;
    RiskType riskType;
    String entityType;
    UUID entityId;
    String entityName;
    UUID initiatedBy;

    // Optional context data
    String countryCode;
    Double transactionAmount;
    String customerType;
}

