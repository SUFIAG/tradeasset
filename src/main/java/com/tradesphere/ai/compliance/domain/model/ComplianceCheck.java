package com.tradesphere.ai.compliance.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import com.tradesphere.ai.compliance.domain.event.*;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Compliance Check Aggregate Root
 *
 * Manages sanctions, AML, and PEP screening for customers and transactions
 * Uses AI to reduce false positives and improve screening accuracy
 *
 * Business Rules:
 * - All customers must pass compliance screening before approval
 * - Sanctions hits block transactions immediately
 * - PEP customers require enhanced due diligence
 * - False positives can be cleared by compliance officers
 */
@Getter
public class ComplianceCheck extends AggregateRoot<ComplianceCheckId> {

    // Identity
    private UUID tenantId;
    private String checkNumber;

    // Entity being checked
    private String entityType; // CUSTOMER, TRANSACTION, DOCUMENT
    private UUID entityId;
    private String entityName;

    // Check details
    private ComplianceCheckType checkType;
    private ComplianceCheckStatus status;
    private Instant initiatedAt;
    private Instant completedAt;
    private UUID initiatedBy;

    // Screening results
    private List<ScreeningHit> sanctionsHits;
    private List<ScreeningHit> pepHits;
    private List<ScreeningHit> amlHits;

    // AI analysis
    private Double aiConfidenceScore; // 0.0 to 1.0
    private Integer falsePositiveProbability; // 0 to 100
    private String aiReasoning;

    // Decision
    private ComplianceDecision decision;
    private String decisionReason;
    private UUID decidedBy;
    private Instant decidedAt;

    // Review
    private boolean requiresManualReview;
    private String reviewComments;

    /**
     * Private constructor
     */
    private ComplianceCheck(ComplianceCheckId id, UUID tenantId) {
        super(id);
        this.tenantId = tenantId;
        this.sanctionsHits = new ArrayList<>();
        this.pepHits = new ArrayList<>();
        this.amlHits = new ArrayList<>();
        this.status = ComplianceCheckStatus.PENDING;
        this.requiresManualReview = false;
    }

    /**
     * Create new compliance check
     */
    public static ComplianceCheck create(
            UUID tenantId,
            ComplianceCheckType checkType,
            String entityType,
            UUID entityId,
            String entityName,
            UUID initiatedBy) {

        if (tenantId == null) throw new ComplianceException("Tenant ID is required");
        if (checkType == null) throw new ComplianceException("Check type is required");
        if (entityId == null) throw new ComplianceException("Entity ID is required");

        ComplianceCheck check = new ComplianceCheck(ComplianceCheckId.generate(), tenantId);
        check.checkType = checkType;
        check.entityType = entityType;
        check.entityId = entityId;
        check.entityName = entityName;
        check.initiatedBy = initiatedBy;
        check.initiatedAt = Instant.now();
        check.checkNumber = generateCheckNumber();

        check.registerEvent(new ComplianceCheckInitiated(
                check.getId().getValue(),
                tenantId,
                checkType,
                entityType,
                entityId
        ));

        return check;
    }

    /**
     * Add sanctions screening hit
     */
    public void addSanctionsHit(ScreeningHit hit) {
        this.sanctionsHits.add(hit);
        if (hit.isHighConfidence()) {
            this.requiresManualReview = true;
        }
    }

    /**
     * Add PEP screening hit
     */
    public void addPEPHit(ScreeningHit hit) {
        this.pepHits.add(hit);
        if (hit.isHighConfidence()) {
            this.requiresManualReview = true;
        }
    }

    /**
     * Add AML screening hit
     */
    public void addAMLHit(ScreeningHit hit) {
        this.amlHits.add(hit);
    }

    /**
     * Complete screening with AI analysis
     */
    public void completeScreening(
            Double aiConfidenceScore,
            Integer falsePositiveProbability,
            String aiReasoning) {

        this.status = ComplianceCheckStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.aiConfidenceScore = aiConfidenceScore;
        this.falsePositiveProbability = falsePositiveProbability;
        this.aiReasoning = aiReasoning;

        // Auto-decide if AI is confident
        if (hasNoHits()) {
            autoApprove("No hits found");
        } else if (hasHighConfidenceHit() && falsePositiveProbability < 20) {
            autoReject("High confidence sanctions/PEP hit");
        } else {
            requireManualReview("Potential matches found, manual review required");
        }

        registerEvent(new ComplianceScreeningCompleted(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                status,
                decision
        ));
    }

    /**
     * Approve compliance check
     */
    public void approve(UUID decidedBy, String reason) {
        if (status != ComplianceCheckStatus.COMPLETED) {
            throw new ComplianceException("Can only approve completed checks");
        }

        this.decision = ComplianceDecision.APPROVED;
        this.decisionReason = reason;
        this.decidedBy = decidedBy;
        this.decidedAt = Instant.now();
        this.requiresManualReview = false;

        registerEvent(new ComplianceCheckApproved(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                decidedBy
        ));
    }

    /**
     * Reject compliance check
     */
    public void reject(UUID decidedBy, String reason) {
        if (status != ComplianceCheckStatus.COMPLETED) {
            throw new ComplianceException("Can only reject completed checks");
        }

        this.decision = ComplianceDecision.REJECTED;
        this.decisionReason = reason;
        this.decidedBy = decidedBy;
        this.decidedAt = Instant.now();
        this.requiresManualReview = false;

        registerEvent(new ComplianceCheckRejected(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                decidedBy,
                reason
        ));
    }

    /**
     * Clear as false positive
     */
    public void clearAsFalsePositive(UUID decidedBy, String reason) {
        this.decision = ComplianceDecision.FALSE_POSITIVE;
        this.decisionReason = reason;
        this.decidedBy = decidedBy;
        this.decidedAt = Instant.now();
        this.requiresManualReview = false;

        registerEvent(new ComplianceCheckClearedAsFalsePositive(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                decidedBy
        ));
    }

    /**
     * Add review comments
     */
    public void addReviewComments(String comments) {
        this.reviewComments = comments;
    }

    // Private helper methods

    private void autoApprove(String reason) {
        this.decision = ComplianceDecision.AUTO_APPROVED;
        this.decisionReason = reason;
        this.decidedAt = Instant.now();
    }

    private void autoReject(String reason) {
        this.decision = ComplianceDecision.AUTO_REJECTED;
        this.decisionReason = reason;
        this.decidedAt = Instant.now();
    }

    private void requireManualReview(String reason) {
        this.decision = ComplianceDecision.MANUAL_REVIEW_REQUIRED;
        this.decisionReason = reason;
        this.requiresManualReview = true;
    }

    private boolean hasNoHits() {
        return sanctionsHits.isEmpty() && pepHits.isEmpty() && amlHits.isEmpty();
    }

    private boolean hasHighConfidenceHit() {
        return sanctionsHits.stream().anyMatch(ScreeningHit::isHighConfidence) ||
               pepHits.stream().anyMatch(ScreeningHit::isHighConfidence);
    }

    private static String generateCheckNumber() {
        return "COMP-" + System.currentTimeMillis();
    }

    /**
     * Get total hits count
     */
    public int getTotalHitsCount() {
        return sanctionsHits.size() + pepHits.size() + amlHits.size();
    }

    /**
     * Check if has any hits
     */
    public boolean hasHits() {
        return !hasNoHits();
    }

    /**
     * Check if approved
     */
    public boolean isApproved() {
        return decision == ComplianceDecision.APPROVED ||
               decision == ComplianceDecision.AUTO_APPROVED;
    }

    /**
     * Check if rejected
     */
    public boolean isRejected() {
        return decision == ComplianceDecision.REJECTED ||
               decision == ComplianceDecision.AUTO_REJECTED;
    }
}

