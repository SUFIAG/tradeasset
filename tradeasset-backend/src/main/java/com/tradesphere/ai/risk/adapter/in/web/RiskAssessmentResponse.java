package com.tradesphere.ai.risk.adapter.in.web;

import com.tradesphere.ai.risk.domain.model.*;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for risk assessments
 */
@Value
@Builder
public class RiskAssessmentResponse {
    UUID id;
    UUID tenantId;
    String assessmentNumber;
    RiskType riskType;
    String entityType;
    UUID entityId;
    String entityName;
    RiskAssessmentStatus status;
    Instant initiatedAt;
    Instant completedAt;
    UUID initiatedBy;
    List<RiskFactor> riskFactors;
    Integer overallRiskScore;
    RiskLevel overallRiskLevel;
    String riskSummary;
    String aiReasoning;
    String recommendation;
    boolean requiresApproval;
    boolean autoRejected;
    boolean highRisk;

    public static RiskAssessmentResponse from(RiskAssessment assessment) {
        return RiskAssessmentResponse.builder()
                .id(assessment.getId().getValue())
                .tenantId(assessment.getTenantId())
                .assessmentNumber(assessment.getAssessmentNumber())
                .riskType(assessment.getRiskType())
                .entityType(assessment.getEntityType())
                .entityId(assessment.getEntityId())
                .entityName(assessment.getEntityName())
                .status(assessment.getStatus())
                .initiatedAt(assessment.getInitiatedAt())
                .completedAt(assessment.getCompletedAt())
                .initiatedBy(assessment.getInitiatedBy())
                .riskFactors(assessment.getRiskFactors())
                .overallRiskScore(assessment.getOverallRiskScore())
                .overallRiskLevel(assessment.getOverallRiskLevel())
                .riskSummary(assessment.getRiskSummary())
                .aiReasoning(assessment.getAiReasoning())
                .recommendation(assessment.getRecommendation())
                .requiresApproval(assessment.isRequiresApproval())
                .autoRejected(assessment.isAutoRejected())
                .highRisk(assessment.isHighRisk())
                .build();
    }
}

