package com.tradesphere.ai.risk.domain.model;

/**
 * Risk Level Enumeration
 * Overall risk rating for an assessment
 */
public enum RiskLevel {
    /**
     * Very low risk - < 10% probability
     */
    VERY_LOW(1, 0, 10),

    /**
     * Low risk - 10-25% probability
     */
    LOW(2, 10, 25),

    /**
     * Medium risk - 25-50% probability
     */
    MEDIUM(3, 25, 50),

    /**
     * High risk - 50-75% probability
     */
    HIGH(4, 50, 75),

    /**
     * Very high risk - > 75% probability
     */
    VERY_HIGH(5, 75, 100);

    private final int level;
    private final int minScore;
    private final int maxScore;

    RiskLevel(int level, int minScore, int maxScore) {
        this.level = level;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public int getLevel() {
        return level;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Determine risk level from risk score
     */
    public static RiskLevel fromScore(int score) {
        if (score < 10) return VERY_LOW;
        if (score < 25) return LOW;
        if (score < 50) return MEDIUM;
        if (score < 75) return HIGH;
        return VERY_HIGH;
    }

    /**
     * Check if this risk level is acceptable
     */
    public boolean isAcceptable() {
        return this == VERY_LOW || this == LOW;
    }

    /**
     * Check if this risk level requires approval
     */
    public boolean requiresApproval() {
        return this == MEDIUM || this == HIGH;
    }

    /**
     * Check if this risk level should be rejected
     */
    public boolean shouldReject() {
        return this == VERY_HIGH;
    }
}

