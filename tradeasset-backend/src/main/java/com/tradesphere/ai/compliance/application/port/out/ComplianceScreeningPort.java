package com.tradesphere.ai.compliance.application.port.out;

import com.tradesphere.ai.compliance.domain.model.ScreeningHit;

import java.util.List;

/**
 * Outbound port for external compliance screening services
 * Integrates with sanctions lists, PEP databases, and AML screening
 */
public interface ComplianceScreeningPort {

    /**
     * Perform sanctions screening
     */
    List<ScreeningHit> screenSanctions(String name, String country, String dateOfBirth);

    /**
     * Perform PEP (Politically Exposed Person) screening
     */
    List<ScreeningHit> screenPEP(String name, String country, String position);

    /**
     * Perform AML (Anti-Money Laundering) screening
     */
    List<ScreeningHit> screenAML(String name, String country, String transactionDetails);

    /**
     * Analyze screening results with AI to detect false positives
     */
    ScreeningAnalysisResult analyzeWithAI(
            String entityName,
            List<ScreeningHit> sanctionsHits,
            List<ScreeningHit> pepHits,
            List<ScreeningHit> amlHits);

    /**
     * Screening analysis result from AI
     */
    record ScreeningAnalysisResult(
            Double aiConfidenceScore,
            Integer falsePositiveProbability,
            String reasoning,
            List<ScreeningHit> confirmedHits,
            List<ScreeningHit> likelyFalsePositives) {
    }
}

