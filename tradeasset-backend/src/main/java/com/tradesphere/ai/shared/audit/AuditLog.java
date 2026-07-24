package com.tradesphere.ai.shared.audit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Audit log entry value object
 * Immutable record of an action performed in the system
 */
@Data
@Builder
public class AuditLog {

    private UUID id;
    private UUID tenantId;
    private String entityType;      // e.g., "Customer", "LetterOfCredit"
    private String entityId;        // UUID of the entity
    private String action;          // e.g., "CREATE", "UPDATE", "DELETE", "APPROVE"
    private String userId;          // User who performed the action
    private String userName;        // User's name for quick reference
    private Instant timestamp;
    private String ipAddress;
    private String userAgent;
    private Map<String, Object> changes;  // Old and new values (for updates)
    private String description;     // Human-readable description
    private Boolean success;        // Whether action succeeded
    private String errorMessage;    // Error message if action failed

    // Performance tracking
    private Long executionTimeMs;
}

