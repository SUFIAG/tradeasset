package com.tradesphere.ai.risk.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import com.tradesphere.ai.risk.domain.event.*;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Risk Assessment Aggregate Root
 *
 * Manages comprehensive risk assessment for customers, transactions, and counterparties
 * Combines multiple risk factors to provide overall risk rating
 *
 * Business Rules:
 * - Risk assessment required before credit limit approval
 * - High risk transactions require additional approval
 * - Country risk automatically factors into overall score
 * - Risk rating updated automatically when factors change
 */
@Getter
public class RiskAssessment extends AggregateRoot<RiskAssessmentId> {

    // Identity
    private UUID tenantId;
    private String assessmentNumber;

    // Entity being assessed
    private RiskType riskType;
    private String entityType; // CUSTOMER, TRANSACTION, COUNTRY, COUNTERPARTY
    private UUID entityId;
    private String entityName;

    // Assessment status
    private RiskAssessmentStatus status;
    private Instant initiatedAt;
    private Instant completedAt;
    private UUID initiatedBy;

    // Risk factors
    private List<RiskFactor> riskFactors;

    // Overall risk
    private Integer overallRiskScore; // 0-100
    private RiskLevel overallRiskLevel;
    private String riskSummary;
    private String aiReasoning;

    // Country-specific
    private String countryCode;
    private Integer countryRiskScore;

    // Customer-specific
    private Integer creditRiskScore;
    private Integer paymentHistoryScore;
    private Integer financialHealthScore;

    // Transaction-specific
    private Integer transactionRiskScore;
    private Integer documentaryRiskScore;

    // Recommendation
    private String recommendation;
    private boolean requiresApproval;
    private boolean autoRejected;

    /**
     * Private constructor
     */
    private RiskAssessment(RiskAssessmentId id, UUID tenantId) {
        super(id);
        this.tenantId = tenantId;
        this.riskFactors = new ArrayList<>();
        this.status = RiskAssessmentStatus.PENDING;
        this.requiresApproval = false;
        this.autoRejected = false;
    }

    /**
     * Create new risk assessment
     */
    public static RiskAssessment create(
            UUID tenantId,
            RiskType riskType,
            String entityType,
            UUID entityId,
            String entityName,
            UUID initiatedBy) {

        if (tenantId == null) throw new RiskException("Tenant ID is required");
        if (riskType == null) throw new RiskException("Risk type is required");
        if (entityId == null) throw new RiskException("Entity ID is required");

        RiskAssessment assessment = new RiskAssessment(RiskAssessmentId.generate(), tenantId);
        assessment.riskType = riskType;
        assessment.entityType = entityType;
        assessment.entityId = entityId;
        assessment.entityName = entityName;
        assessment.initiatedBy = initiatedBy;
        assessment.initiatedAt = Instant.now();
        assessment.assessmentNumber = generateAssessmentNumber();

        assessment.registerEvent(new RiskAssessmentInitiated(
                assessment.getId().getValue(),
                tenantId,
                riskType,
                entityType,
                entityId
        ));

        return assessment;
    }

    /**
     * Add risk factor
     */
    public void addRiskFactor(RiskFactor factor) {
        if (status == RiskAssessmentStatus.COMPLETED) {
            throw new RiskException("Cannot add factors to completed assessment");
        }
        this.riskFactors.add(factor);
    }

    /**
     * Set country risk
     */
    public void setCountryRisk(String countryCode, Integer score) {
        this.countryCode = countryCode;
        this.countryRiskScore = score;

        addRiskFactor(RiskFactor.builder()
                .factorName("COUNTRY_RISK")
                .category("Country")
                .score(score)
                .weight(0.15)
                .description("Country risk for " + countryCode)
                .build());
    }

    /**
     * Set credit risk
     */
    public void setCreditRisk(Integer score, String reasoning) {
        this.creditRiskScore = score;

        addRiskFactor(RiskFactor.builder()
                .factorName("CREDIT_RISK")
                .category("Credit")
                .score(score)
                .weight(0.30)
                .description("Credit worthiness assessment")
                .aiReasoning(reasoning)
                .build());
    }

    /**
     * Set payment history risk
     */
    public void setPaymentHistoryRisk(Integer score) {
        this.paymentHistoryScore = score;

        addRiskFactor(RiskFactor.builder()
                .factorName("PAYMENT_HISTORY")
                .category("Credit")
                .score(score)
                .weight(0.20)
                .description("Historical payment performance")
                .build());
    }

    /**
     * Set financial health risk
     */
    public void setFinancialHealthRisk(Integer score) {
        this.financialHealthScore = score;

        addRiskFactor(RiskFactor.builder()
                .factorName("FINANCIAL_HEALTH")
                .category("Credit")
                .score(score)
                .weight(0.15)
                .description("Financial statements analysis")
                .build());
    }

    /**
     * Set transaction risk
     */
    public void setTransactionRisk(Integer score) {
        this.transactionRiskScore = score;

        addRiskFactor(RiskFactor.builder()
                .factorName("TRANSACTION_RISK")
                .category("Transaction")
                .score(score)
                .weight(0.25)
                .description("Transaction pattern and amount analysis")
                .build());
    }

    /**
     * Complete risk assessment with AI analysis
     */
    public void completeAssessment(String aiReasoning, String recommendation) {
        this.status = RiskAssessmentStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.aiReasoning = aiReasoning;
        this.recommendation = recommendation;

        // Calculate overall risk score
        calculateOverallRisk();

        // Determine if requires approval or auto-reject
        if (overallRiskLevel == RiskLevel.VERY_HIGH) {
            this.autoRejected = true;
            this.requiresApproval = false;
        } else if (overallRiskLevel == RiskLevel.HIGH || overallRiskLevel == RiskLevel.MEDIUM) {
            this.requiresApproval = true;
        }

        registerEvent(new RiskAssessmentCompleted(
                getId().getValue(),
                tenantId,
                entityType,
                entityId,
                overallRiskScore,
                overallRiskLevel
        ));

        if (overallRiskLevel == RiskLevel.HIGH || overallRiskLevel == RiskLevel.VERY_HIGH) {
            registerEvent(new HighRiskDetected(
                    getId().getValue(),
                    tenantId,
                    entityType,
                    entityId,
                    overallRiskLevel,
                    aiReasoning
            ));
        }
    }

    /**
     * Calculate overall risk score from factors
     */
    private void calculateOverallRisk() {
        if (riskFactors.isEmpty()) {
            this.overallRiskScore = 50; // Default medium risk
        } else {
            double weightedSum = riskFactors.stream()
                    .mapToDouble(RiskFactor::getWeightedScore)
                    .sum();

            double totalWeight = riskFactors.stream()
                    .mapToDouble(RiskFactor::getWeight)
                    .sum();

            this.overallRiskScore = (int) Math.round(weightedSum / totalWeight);
        }

        this.overallRiskLevel = RiskLevel.fromScore(overallRiskScore);
        this.riskSummary = generateRiskSummary();
    }

    /**
     * Generate risk summary
     */
    private String generateRiskSummary() {
        return String.format("Overall Risk: %s (%d/100). %d factors analyzed. %s",
                overallRiskLevel.name(),
                overallRiskScore,
                riskFactors.size(),
                overallRiskLevel.isAcceptable() ? "Acceptable risk" :
                overallRiskLevel.requiresApproval() ? "Requires approval" :
                "High risk - recommend rejection");
    }

    /**
     * Get high risk factors
     */
    public List<RiskFactor> getHighRiskFactors() {
        return riskFactors.stream()
                .filter(RiskFactor::isHighRisk)
                .toList();
    }

    /**
     * Check if acceptable risk
     */
    public boolean isAcceptableRisk() {
        return overallRiskLevel != null && overallRiskLevel.isAcceptable();
    }

    /**
     * Check if high risk
     */
    public boolean isHighRisk() {
        return overallRiskLevel == RiskLevel.HIGH || overallRiskLevel == RiskLevel.VERY_HIGH;
    }

    private static String generateAssessmentNumber() {
        return "RISK-" + System.currentTimeMillis();
    }
}

