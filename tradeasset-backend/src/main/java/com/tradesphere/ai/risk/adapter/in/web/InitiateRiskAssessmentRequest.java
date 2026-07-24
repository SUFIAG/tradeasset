package com.tradesphere.ai.risk.adapter.in.web;

import com.tradesphere.ai.risk.domain.model.RiskType;
import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for initiating risk assessments
 */
@Data
public class InitiateRiskAssessmentRequest {
    private RiskType riskType;
    private String entityType;
    private UUID entityId;
    private String entityName;
    private UUID initiatedBy;
    private String countryCode;
    private Double transactionAmount;
    private String customerType;
}

