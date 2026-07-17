package com.tradesphere.ai.shared.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for audit logs
 * Optimized for high-throughput writes and efficient reads
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    /**
     * Find audit logs for a specific tenant with pagination
     */
    Page<AuditLogEntity> findByTenantIdOrderByTimestampDesc(UUID tenantId, Pageable pageable);

    /**
     * Find audit logs for a specific entity
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE a.tenantId = :tenantId " +
           "AND a.entityType = :entityType AND a.entityId = :entityId " +
           "ORDER BY a.timestamp DESC")
    List<AuditLogEntity> findByEntity(
            @Param("tenantId") UUID tenantId,
            @Param("entityType") String entityType,
            @Param("entityId") String entityId
    );

    /**
     * Find audit logs by user
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE a.tenantId = :tenantId " +
           "AND a.userId = :userId AND a.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.timestamp DESC")
    Page<AuditLogEntity> findByUser(
            @Param("tenantId") UUID tenantId,
            @Param("userId") String userId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );

    /**
     * Find audit logs by action type
     */
    @Query("SELECT a FROM AuditLogEntity a WHERE a.tenantId = :tenantId " +
           "AND a.action = :action AND a.timestamp BETWEEN :startTime AND :endTime " +
           "ORDER BY a.timestamp DESC")
    Page<AuditLogEntity> findByAction(
            @Param("tenantId") UUID tenantId,
            @Param("action") String action,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );

    /**
     * Count failed actions for monitoring
     */
    @Query("SELECT COUNT(a) FROM AuditLogEntity a WHERE a.tenantId = :tenantId " +
           "AND a.success = false AND a.timestamp > :since")
    long countFailedActions(
            @Param("tenantId") UUID tenantId,
            @Param("since") Instant since
    );
}

