package com.tradesphere.ai.compliance.adapter.in.web;

import com.tradesphere.ai.compliance.domain.model.*;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for compliance checks
 */
@Value
@Builder
public class ComplianceCheckResponse {
    UUID id;
    UUID tenantId;
    String checkNumber;
    String entityType;
    UUID entityId;
    String entityName;
    ComplianceCheckType checkType;
    ComplianceCheckStatus status;
    Instant initiatedAt;
    Instant completedAt;
    UUID initiatedBy;
    List<ScreeningHit> sanctionsHits;
    List<ScreeningHit> pepHits;
    List<ScreeningHit> amlHits;
    Double aiConfidenceScore;
    Integer falsePositiveProbability;
    String aiReasoning;
    ComplianceDecision decision;
    String decisionReason;
    UUID decidedBy;
    Instant decidedAt;
    boolean requiresManualReview;
    String reviewComments;
    int totalHitsCount;

    public static ComplianceCheckResponse from(ComplianceCheck check) {
        return ComplianceCheckResponse.builder()
                .id(check.getId().getValue())
                .tenantId(check.getTenantId())
                .checkNumber(check.getCheckNumber())
                .entityType(check.getEntityType())
                .entityId(check.getEntityId())
                .entityName(check.getEntityName())
                .checkType(check.getCheckType())
                .status(check.getStatus())
                .initiatedAt(check.getInitiatedAt())
                .completedAt(check.getCompletedAt())
                .initiatedBy(check.getInitiatedBy())
                .sanctionsHits(check.getSanctionsHits())
                .pepHits(check.getPepHits())
                .amlHits(check.getAmlHits())
                .aiConfidenceScore(check.getAiConfidenceScore())
                .falsePositiveProbability(check.getFalsePositiveProbability())
                .aiReasoning(check.getAiReasoning())
                .decision(check.getDecision())
                .decisionReason(check.getDecisionReason())
                .decidedBy(check.getDecidedBy())
                .decidedAt(check.getDecidedAt())
                .requiresManualReview(check.isRequiresManualReview())
                .reviewComments(check.getReviewComments())
                .totalHitsCount(check.getTotalHitsCount())
                .build();
    }
}

