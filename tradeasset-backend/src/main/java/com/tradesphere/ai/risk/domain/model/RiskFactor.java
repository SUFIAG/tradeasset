package com.tradesphere.ai.risk.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Builder;
import lombok.Value;

/**
 * Risk Factor Value Object
 * Represents an individual risk factor with its score and contribution
 */
@Value
@Builder
public class RiskFactor implements ValueObject {
    /**
     * Factor name/type
     */
    String factorName;

    /**
     * Factor category
     */
    String category;

    /**
     * Factor score (0-100)
     */
    Integer score;

    /**
     * Weight in overall risk (0.0-1.0)
     */
    Double weight;

    /**
     * Factor description
     */
    String description;

    /**
     * AI reasoning for this factor
     */
    String aiReasoning;

    /**
     * Recommendation for this factor
     */
    String recommendation;

    /**
     * Calculate weighted contribution to overall risk
     */
    public double getWeightedScore() {
        return score * weight;
    }

    /**
     * Check if this is a high-risk factor
     */
    public boolean isHighRisk() {
        return score >= 70;
    }
}

