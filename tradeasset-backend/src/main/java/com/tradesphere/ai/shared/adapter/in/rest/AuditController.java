package com.tradesphere.ai.shared.adapter.in.rest;

import com.tradesphere.ai.shared.application.port.out.ApprovalPort;
import com.tradesphere.ai.shared.application.port.out.TimelinePort;
import com.tradesphere.ai.shared.application.service.EntityAccessRegistry;
import com.tradesphere.ai.shared.domain.approval.ApprovalStatus;
import com.tradesphere.ai.shared.domain.error.ValidationException;
import com.tradesphere.ai.shared.query.PageQuery;
import com.tradesphere.ai.shared.query.PageResult;
import com.tradesphere.ai.shared.security.AuthenticatedUser;
import com.tradesphere.ai.shared.security.Roles;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Cross-module read endpoints over the shared registers: the audit trail, per-record timelines and
 * the maker-checker queue.
 */
@RestController
@RequestMapping("/api/v1")
public class AuditController {

    private final TimelinePort timeline;
    private final ApprovalPort approvals;
    private final EntityAccessRegistry access;

    public AuditController(TimelinePort timeline, ApprovalPort approvals, EntityAccessRegistry access) {
        this.timeline = timeline;
        this.approvals = approvals;
        this.access = access;
    }

    /** Tenant-wide audit trail search (REQ-REL-003). */
    @GetMapping("/audit")
    @PreAuthorize("hasAnyRole('AUDITOR','SYSTEM_ADMIN','COMPLIANCE_OFFICER')")
    public PageResult<TimelineEntryView> searchAudit(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) UUID actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        PageQuery query = PageQuery.of(page, size, sort, "occurredAt,desc");
        return timeline.search(user.tenantId(),
                        new TimelinePort.AuditFilter(upper(entityType), entityId, actorId, upper(action), from, to), query)
                .map(e -> TimelineEntryView.of(e, true));
    }

    /** State timeline and audit history of one record (REQ-UI-004), for any record type the caller may read. */
    @GetMapping("/timeline/{entityType}/{entityId}")
    @PreAuthorize("isAuthenticated()")
    public List<TimelineEntryView> timelineOf(@AuthenticationPrincipal AuthenticatedUser user,
                                              @PathVariable String entityType, @PathVariable UUID entityId) {
        String type = upper(entityType);
        access.requireRead(user, type, entityId);
        boolean showAddress = user.hasAnyRole(Roles.AUDITOR, Roles.SYSTEM_ADMIN);
        return timeline.listFor(user.tenantId(), type, entityId).stream()
                .map(e -> TimelineEntryView.of(e, showAddress)).toList();
    }

    /** Maker-checker history of one record. */
    @GetMapping("/approvals/{entityType}/{entityId}")
    @PreAuthorize("isAuthenticated()")
    public List<ApprovalView> approvalsOf(@AuthenticationPrincipal AuthenticatedUser user,
                                          @PathVariable String entityType, @PathVariable UUID entityId) {
        String type = upper(entityType);
        access.requireRead(user, type, entityId);
        return approvals.listForEntity(user.tenantId(), type, entityId).stream().map(ApprovalView::of).toList();
    }

    /** The approval queue: requests awaiting a checker (REQ-MKR-001). */
    @GetMapping("/approvals")
    @PreAuthorize("hasAnyRole('CHECKER','APPROVER','RISK_OFFICER','COMPLIANCE_OFFICER','SYSTEM_ADMIN','AUDITOR')")
    public PageResult<ApprovalView> approvalQueue(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String entityType,
                                                  @RequestParam(required = false) String action,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer size,
                                                  @RequestParam(required = false) String sort) {
        ApprovalStatus parsed = ApprovalStatus.PENDING;
        if (status != null && !status.isBlank()) {
            try {
                parsed = ApprovalStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("status", "Unknown approval status: " + status);
            }
        }
        PageQuery query = PageQuery.of(page, size, sort, "makerAt,desc");
        return approvals.search(user.tenantId(), parsed, upper(entityType), upper(action), query).map(ApprovalView::of);
    }

    private static String upper(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
