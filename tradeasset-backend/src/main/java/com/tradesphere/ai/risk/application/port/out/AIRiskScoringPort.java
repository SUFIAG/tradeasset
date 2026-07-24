package com.tradesphere.ai.risk.application.port.out;

import com.tradesphere.ai.risk.domain.model.RiskType;

/**
 * Outbound port for AI-powered risk scoring
 */
public interface AIRiskScoringPort {

    /**
     * Calculate credit risk score using AI
     */
    RiskScoringResult calculateCreditRisk(
            String entityName,
            String countryCode,
            Double transactionAmount,
            String customerType,
            String historicalData
    );

    /**
     * Calculate country risk score
     */
    Integer calculateCountryRisk(String countryCode);

    /**
     * Calculate transaction risk score
     */
    Integer calculateTransactionRisk(
            Double amount,
            String currency,
            String counterpartyCountry,
            String transactionType
    );

    /**
     * Perform comprehensive risk analysis with AI
     */
    RiskAnalysisResult analyzeWithAI(
            RiskType riskType,
            String entityName,
            String countryCode,
            String additionalContext
    );

    /**
     * Risk scoring result from AI
     */
    record RiskScoringResult(
            Integer creditScore,
            Integer paymentHistoryScore,
            Integer financialHealthScore,
            String reasoning
    ) {}

    /**
     * Risk analysis result from AI
     */
    record RiskAnalysisResult(
            Integer overallScore,
            String reasoning,
            String recommendation,
            java.util.List<String> riskFactors
    ) {}
}

