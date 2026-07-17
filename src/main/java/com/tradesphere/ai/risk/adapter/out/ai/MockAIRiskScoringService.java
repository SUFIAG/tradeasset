package com.tradesphere.ai.risk.adapter.out.ai;

import com.tradesphere.ai.risk.application.port.out.AIRiskScoringPort;
import com.tradesphere.ai.risk.domain.model.RiskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Mock AI Risk Scoring Service
 *
 * In production, this would integrate with:
 * - Credit bureaus (Equifax, Experian, TransUnion)
 * - Country risk databases (World Bank, IMF)
 * - Financial data providers (Bloomberg, Reuters)
 * - AI models for risk prediction
 */
@Service
@Slf4j
public class MockAIRiskScoringService implements AIRiskScoringPort {

    private final Random random = new Random();
    private final Map<String, Integer> countryRiskScores = initializeCountryRisks();

    @Override
    public RiskScoringResult calculateCreditRisk(
            String entityName,
            String countryCode,
            Double transactionAmount,
            String customerType,
            String historicalData) {

        log.info("Calculating credit risk: entity={}, country={}, amount={}",
                entityName, countryCode, transactionAmount);

        // Mock credit scoring logic
        int baseScore = 50;

        // Adjust based on country
        if (countryCode != null) {
            int countryRisk = calculateCountryRisk(countryCode);
            baseScore += (100 - countryRisk) / 4; // Lower country risk = better credit
        }

        // Adjust based on transaction size
        if (transactionAmount != null) {
            if (transactionAmount > 1000000) baseScore -= 10; // Large transactions riskier
            else if (transactionAmount < 50000) baseScore += 5;
        }

        // Add some randomness
        int creditScore = Math.min(100, Math.max(0, baseScore + random.nextInt(21) - 10));
        int paymentHistoryScore = Math.min(100, Math.max(0, baseScore + random.nextInt(16) - 8));
        int financialHealthScore = Math.min(100, Math.max(0, baseScore + random.nextInt(16) - 8));

        String reasoning = String.format(
                "Credit assessment based on: Country risk (%s), Transaction size ($%.2f), Customer type (%s). " +
                "Credit score: %d/100, Payment history: %d/100, Financial health: %d/100.",
                countryCode, transactionAmount != null ? transactionAmount : 0.0,
                customerType, creditScore, paymentHistoryScore, financialHealthScore
        );

        return new RiskScoringResult(creditScore, paymentHistoryScore, financialHealthScore, reasoning);
    }

    @Override
    public Integer calculateCountryRisk(String countryCode) {
        if (countryCode == null) return 50;

        Integer score = countryRiskScores.get(countryCode.toUpperCase());
        if (score != null) {
            log.debug("Country risk for {}: {}/100", countryCode, score);
            return score;
        }

        // Default medium risk
        return 50;
    }

    @Override
    public Integer calculateTransactionRisk(
            Double amount,
            String currency,
            String counterpartyCountry,
            String transactionType) {

        log.info("Calculating transaction risk: amount={}, currency={}, country={}, type={}",
                amount, currency, counterpartyCountry, transactionType);

        int baseScore = 30;

        // Large transactions are riskier
        if (amount != null) {
            if (amount > 5000000) baseScore += 30;
            else if (amount > 1000000) baseScore += 20;
            else if (amount > 500000) baseScore += 10;
        }

        // Country risk factor
        if (counterpartyCountry != null) {
            int countryRisk = calculateCountryRisk(counterpartyCountry);
            baseScore += countryRisk / 5; // 20% weight
        }

        // Add randomness
        return Math.min(100, Math.max(0, baseScore + random.nextInt(11) - 5));
    }

    @Override
    public RiskAnalysisResult analyzeWithAI(
            RiskType riskType,
            String entityName,
            String countryCode,
            String additionalContext) {

        log.info("Performing AI risk analysis: type={}, entity={}, country={}",
                riskType, entityName, countryCode);

        // Mock AI analysis
        int baseScore = 40;

        // Risk type specific adjustments
        switch (riskType) {
            case CREDIT_RISK -> baseScore += random.nextInt(31) + 10; // 10-40
            case COUNTRY_RISK -> baseScore = calculateCountryRisk(countryCode);
            case TRANSACTION_RISK -> baseScore += random.nextInt(21) + 15; // 15-35
            case COMPLIANCE_RISK -> baseScore += random.nextInt(16) + 5; // 5-20
            case OPERATIONAL_RISK -> baseScore += random.nextInt(21); // 0-20
            default -> baseScore += random.nextInt(21); // 0-20
        }

        int overallScore = Math.min(100, Math.max(0, baseScore));

        List<String> riskFactors = Arrays.asList(
                "Country risk: " + countryCode,
                "Entity type: " + entityName,
                "Risk category: " + riskType,
                "Historical patterns: Normal",
                "Market conditions: Stable"
        );

        String reasoning = String.format(
                "AI Risk Analysis: Overall risk score %d/100. " +
                "Primary risk factors: %s risk in %s. " +
                "Risk level: %s. Analyzed %d risk factors.",
                overallScore,
                riskType,
                countryCode,
                overallScore < 30 ? "LOW" : overallScore < 60 ? "MEDIUM" : "HIGH",
                riskFactors.size()
        );

        String recommendation = overallScore < 30 ? "APPROVE - Low risk, proceed with standard terms" :
                overallScore < 60 ? "REVIEW - Medium risk, additional approval recommended" :
                overallScore < 80 ? "CAUTION - High risk, enhanced due diligence required" :
                "REJECT - Very high risk, recommend declining";

        return new RiskAnalysisResult(overallScore, reasoning, recommendation, riskFactors);
    }

    /**
     * Initialize country risk scores (sample data)
     * In production, this would come from external APIs
     */
    private Map<String, Integer> initializeCountryRisks() {
        Map<String, Integer> risks = new HashMap<>();

        // Very Low Risk (0-20)
        risks.put("US", 10);
        risks.put("GB", 12);
        risks.put("DE", 15);
        risks.put("CH", 8);
        risks.put("SG", 18);

        // Low Risk (20-40)
        risks.put("AE", 25);
        risks.put("JP", 22);
        risks.put("FR", 20);
        risks.put("CA", 18);
        risks.put("AU", 19);

        // Medium Risk (40-60)
        risks.put("CN", 45);
        risks.put("IN", 50);
        risks.put("BR", 55);
        risks.put("MX", 48);
        risks.put("TR", 52);

        // High Risk (60-80)
        risks.put("PK", 65);
        risks.put("BD", 68);
        risks.put("NG", 70);
        risks.put("VE", 75);

        // Very High Risk (80-100)
        risks.put("IR", 85);
        risks.put("KP", 95);
        risks.put("SY", 90);
        risks.put("AF", 88);

        return risks;
    }
}

