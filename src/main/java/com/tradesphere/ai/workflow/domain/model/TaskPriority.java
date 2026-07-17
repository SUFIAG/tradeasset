package com.tradesphere.ai.workflow.domain.model;

/**
 * Task priority levels
 * Used for task ordering and SLA calculation
 */
public enum TaskPriority {
    
    LOW(72),        // 72 hours
    NORMAL(48),     // 48 hours
    HIGH(24),       // 24 hours
    URGENT(8),      // 8 hours
    CRITICAL(4);    // 4 hours
    
    private final int defaultSlaHours;
    
    TaskPriority(int defaultSlaHours) {
        this.defaultSlaHours = defaultSlaHours;
    }
    
    public int getDefaultSlaHours() {
        return defaultSlaHours;
    }
    
    public boolean requiresEscalation() {
        return this == URGENT || this == CRITICAL;
    }
}

