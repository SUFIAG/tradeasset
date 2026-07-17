package com.tradesphere.ai.shared.audit;

import com.tradesphere.ai.shared.multitenancy.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for audit logging
 * Optimized for high-throughput with async writes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log an action asynchronously to avoid blocking business operations
     * Uses separate transaction to ensure audit log is saved even if main transaction fails
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAsync(String entityType, String entityId, String action,
                         UUID userId, String userName, Map<String, Object> changes,
                         String description) {
        try {
            AuditLogEntity auditLog = createAuditLog(
                entityType, entityId, action, userId, userName,
                changes, description, true, null, null
            );
            auditLogRepository.save(auditLog);
            log.trace("Audit log saved: {} {} on {}", action, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to save audit log for {} {} on {}", action, entityType, entityId, e);
            // Don't throw exception to avoid disrupting main flow
        }
    }

    /**
     * Log an action with execution time
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAsyncWithTiming(String entityType, String entityId, String action,
                                   UUID userId, String userName, Map<String, Object> changes,
                                   String description, long executionTimeMs) {
        try {
            AuditLogEntity auditLog = createAuditLog(
                entityType, entityId, action, userId, userName,
                changes, description, true, null, executionTimeMs
            );
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    /**
     * Log a failed action
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String entityType, String entityId, String action,
                          UUID userId, String userName, String errorMessage) {
        try {
            AuditLogEntity auditLog = createAuditLog(
                entityType, entityId, action, userId, userName,
                null, null, false, errorMessage, null
            );
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log for failure", e);
        }
    }

    /**
     * Get audit logs for an entity
     */
    @Transactional(readOnly = true)
    public List<AuditLogEntity> getEntityAuditLogs(String entityType, String entityId) {
        UUID tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByEntity(tenantId, entityType, entityId);
    }

    /**
     * Get audit logs with pagination
     */
    @Transactional(readOnly = true)
    public Page<AuditLogEntity> getAuditLogs(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByTenantIdOrderByTimestampDesc(tenantId, pageable);
    }

    /**
     * Get audit logs by user
     */
    @Transactional(readOnly = true)
    public Page<AuditLogEntity> getUserAuditLogs(String userId, Instant startTime,
                                                  Instant endTime, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByUser(tenantId, userId, startTime, endTime, pageable);
    }

    /**
     * Get audit logs by action
     */
    @Transactional(readOnly = true)
    public Page<AuditLogEntity> getActionAuditLogs(String action, Instant startTime,
                                                    Instant endTime, Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        return auditLogRepository.findByAction(tenantId, action, startTime, endTime, pageable);
    }

    /**
     * Count failed actions for monitoring
     */
    @Transactional(readOnly = true)
    public long countRecentFailures(Instant since) {
        UUID tenantId = TenantContext.getTenantId();
        return auditLogRepository.countFailedActions(tenantId, since);
    }

    // Helper method to create audit log entity
    private AuditLogEntity createAuditLog(String entityType, String entityId, String action,
                                         UUID userId, String userName, Map<String, Object> changes,
                                         String description, Boolean success, String errorMessage,
                                         Long executionTimeMs) {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setId(UUID.randomUUID());
        auditLog.setTenantId(TenantContext.getTenantIdOrNull());
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setUserId(userId != null ? userId.toString() : null);
        auditLog.setUserName(userName);
        auditLog.setTimestamp(Instant.now());
        auditLog.setChanges(changes);
        auditLog.setDescription(description);
        auditLog.setSuccess(success);
        auditLog.setErrorMessage(errorMessage);
        auditLog.setExecutionTimeMs(executionTimeMs);

        // Try to get HTTP request info
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                auditLog.setIpAddress(getClientIp(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            // Ignore - may not be in request context (async, scheduled tasks, etc.)
        }

        return auditLog;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Take first IP if multiple IPs in X-Forwarded-For
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

