package com.tradesphere.ai.workflow.domain.model;

/**
 * Approval level enumeration
 * Defines hierarchical approval levels
 */
public enum ApprovalLevel {

    /**
     * Level 1 - Junior/Operations approval
     * First line approval for routine transactions
     */
    LEVEL_1("Junior Officer", 1),

    /**
     * Level 2 - Senior/Credit approval
     * Second line approval for moderate risk transactions
     */
    LEVEL_2("Senior Officer", 2),

    /**
     * Level 3 - Manager approval
     * Management approval for high value transactions
     */
    LEVEL_3("Manager", 3),

    /**
     * Level 4 - Senior Manager approval
     * Senior management approval for critical transactions
     */
    LEVEL_4("Senior Manager", 4),

    /**
     * Level 5 - Executive approval
     * Executive approval for exceptional cases
     */
    LEVEL_5("Executive", 5);

    private final String displayName;
    private final int level;

    ApprovalLevel(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Check if this level is higher than another
     */
    public boolean isHigherThan(ApprovalLevel other) {
        return this.level > other.level;
    }

    /**
     * Check if this level is lower than another
     */
    public boolean isLowerThan(ApprovalLevel other) {
        return this.level < other.level;
    }

    /**
     * Get next approval level
     */
    public ApprovalLevel getNextLevel() {
        return switch (this) {
            case LEVEL_1 -> LEVEL_2;
            case LEVEL_2 -> LEVEL_3;
            case LEVEL_3 -> LEVEL_4;
            case LEVEL_4 -> LEVEL_5;
            case LEVEL_5 -> throw new IllegalStateException("No higher approval level");
        };
    }

    /**
     * Check if this is the final approval level
     */
    public boolean isFinalLevel() {
        return this == LEVEL_5;
    }

    /**
     * Get approval level by number
     */
    public static ApprovalLevel fromLevel(int level) {
        for (ApprovalLevel approvalLevel : values()) {
            if (approvalLevel.level == level) {
                return approvalLevel;
            }
        }
        throw new IllegalArgumentException("Invalid approval level: " + level);
    }
}


