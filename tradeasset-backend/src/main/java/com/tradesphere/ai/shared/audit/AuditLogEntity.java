package com.tradesphere.ai.shared.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA Entity for audit logs
 * Optimized for write-heavy workload with minimal indexes
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_tenant_timestamp", columnList = "tenant_id, timestamp"),
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_user", columnList = "user_id, timestamp"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "user_name", length = 200)
    private String userName;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // Store JSON changes in JSONB for PostgreSQL (high performance)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> changes;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean success;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        if (success == null) {
            success = true;
        }
    }
}

