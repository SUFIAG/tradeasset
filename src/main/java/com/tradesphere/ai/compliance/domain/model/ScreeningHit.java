package com.tradesphere.ai.compliance.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Builder;
import lombok.Value;

/**
 * Screening Hit Value Object
 * Represents a match found during sanctions, PEP, or AML screening
 */
@Value
@Builder
public class ScreeningHit implements ValueObject {

    /**
     * Type of hit (SANCTIONS, PEP, AML)
     */
    String hitType;

    /**
     * Matched name
     */
    String matchedName;

    /**
     * Match score (0-100)
     */
    Integer matchScore;

    /**
     * Source list name
     */
    String sourceList;

    /**
     * Source list provider (e.g., OFAC, UN, EU, World-Check)
     */
    String listProvider;

    /**
     * Country codes associated with the hit
     */
    String countryCodes;

    /**
     * Additional information about the hit
     */
    String additionalInfo;

    /**
     * AI confidence score (0.0 to 1.0)
     */
    Double aiConfidence;

    /**
     * Whether AI assessed this as a false positive
     */
    Boolean assessedAsFalsePositive;

    /**
     * Check if this is a high confidence hit (match score >= 90 or AI confidence >= 0.9)
     */
    public boolean isHighConfidence() {
        return (matchScore != null && matchScore >= 90) ||
               (aiConfidence != null && aiConfidence >= 0.9);
    }

    /**
     * Check if likely a false positive (AI confidence low or explicitly assessed)
     */
    public boolean isLikelyFalsePositive() {
        return Boolean.TRUE.equals(assessedAsFalsePositive) ||
               (aiConfidence != null && aiConfidence < 0.3);
    }
}

