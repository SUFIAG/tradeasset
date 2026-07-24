package com.tradesphere.ai.customer.domain.model;

/**
 * Risk Rating Enumeration
 * Categorizes customer risk level
 */
public enum RiskRating {
    /**
     * Low risk - minimal monitoring required
     */
    LOW(1),

    /**
     * Medium risk - standard monitoring
     */
    MEDIUM(2),

    /**
     * High risk - enhanced monitoring required
     */
    HIGH(3),

    /**
     * Critical risk - maximum scrutiny, senior approval required
     */
    CRITICAL(4),

    /**
     * Not yet assessed
     */
    NOT_ASSESSED(0);

    private final int level;

    RiskRating(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(RiskRating other) {
        return this.level > other.level;
    }

    public boolean requiresEnhancedDueDiligence() {
        return this == HIGH || this == CRITICAL;
    }

    public boolean requiresSeniorApproval() {
        return this == CRITICAL;
    }

    public boolean isAcceptable() {
        return this == LOW || this == MEDIUM;
    }
}

