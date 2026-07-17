package com.tradesphere.ai.shared.notification;

/**
 * Notification priority levels
 */
public enum NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT,
    CRITICAL;

    public boolean isUrgent() {
        return this == URGENT || this == CRITICAL;
    }

    public boolean requiresImmediate() {
        return this == CRITICAL;
    }
}

