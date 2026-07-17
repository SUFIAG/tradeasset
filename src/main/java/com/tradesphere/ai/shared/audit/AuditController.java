package com.tradesphere.ai.shared.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * REST API for querying audit logs
 * Read-only API with pagination and filtering
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * Get audit logs with pagination
     */
    @GetMapping
    public ResponseEntity<Page<AuditLogEntity>> getAuditLogs(Pageable pageable) {
        log.debug("Fetching audit logs with pagination: {}", pageable);
        Page<AuditLogEntity> auditLogs = auditService.getAuditLogs(pageable);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Get audit logs for a specific entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogEntity>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        log.debug("Fetching audit logs for entity: {} {}", entityType, entityId);
        List<AuditLogEntity> auditLogs = auditService.getEntityAuditLogs(entityType, entityId);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Get audit logs by user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditLogEntity>> getUserAuditLogs(
            @PathVariable String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            Pageable pageable) {

        // Default to last 30 days if not specified
        if (startTime == null) {
            startTime = Instant.now().minus(30, ChronoUnit.DAYS);
        }
        if (endTime == null) {
            endTime = Instant.now();
        }

        log.debug("Fetching audit logs for user: {} from {} to {}", userId, startTime, endTime);
        Page<AuditLogEntity> auditLogs = auditService.getUserAuditLogs(userId, startTime, endTime, pageable);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Get audit logs by action type
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<Page<AuditLogEntity>> getActionAuditLogs(
            @PathVariable String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            Pageable pageable) {

        // Default to last 7 days if not specified
        if (startTime == null) {
            startTime = Instant.now().minus(7, ChronoUnit.DAYS);
        }
        if (endTime == null) {
            endTime = Instant.now();
        }

        log.debug("Fetching audit logs for action: {} from {} to {}", action, startTime, endTime);
        Page<AuditLogEntity> auditLogs = auditService.getActionAuditLogs(action, startTime, endTime, pageable);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Get count of recent failures for monitoring
     */
    @GetMapping("/failures/count")
    public ResponseEntity<Long> getRecentFailuresCount(
            @RequestParam(defaultValue = "24") int hours) {

        Instant since = Instant.now().minus(hours, ChronoUnit.HOURS);
        log.debug("Counting failed actions since: {}", since);
        long count = auditService.countRecentFailures(since);
        return ResponseEntity.ok(count);
    }
}

