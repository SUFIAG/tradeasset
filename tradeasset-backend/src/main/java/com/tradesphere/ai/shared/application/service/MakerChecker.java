package com.tradesphere.ai.shared.application.service;

import com.tradesphere.ai.shared.application.port.out.ApprovalPort;
import com.tradesphere.ai.shared.domain.approval.ApprovalRecord;
import com.tradesphere.ai.shared.domain.error.ConflictException;
import com.tradesphere.ai.shared.domain.error.ForbiddenActionException;
import com.tradesphere.ai.shared.domain.error.NotFoundException;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import com.tradesphere.ai.shared.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Maker-checker control (REQ-MKR-001..003). The module that owns the record calls
 * {@link #request} when the maker acts and {@link #approve} or {@link #reject} when a checker
 * decides, then applies its own effect in the same transaction. Self-approval is rejected, never
 * merely warned about.
 */
@Service
public class MakerChecker {

    private final ApprovalPort approvals;
    private final AuditTrail audit;
    private final Clock clock;

    public MakerChecker(ApprovalPort approvals, AuditTrail audit, Clock clock) {
        this.approvals = approvals;
        this.audit = audit;
        this.clock = clock;
    }

    /** Opens a pending approval. Fails with 409 when one is already pending for the same action. */
    public ApprovalRecord request(AuthenticatedUser maker, String entityType, UUID entityId, String action,
                                  String reference, Money amount, String comment) {
        approvals.findPending(maker.tenantId(), entityType, entityId, action).ifPresent(existing -> {
            throw new ConflictException("APPROVAL_ALREADY_PENDING",
                    "An approval for " + action + " is already pending");
        });
        ApprovalRecord record = ApprovalRecord.request(UUID.randomUUID(), maker.tenantId(), entityType, entityId,
                action, reference, amount, maker.userId(), maker.displayName(), comment, clock.instant());
        approvals.insert(record);
        audit.record(maker, entityType, entityId, "APPROVAL_REQUESTED", null, null, comment,
                java.util.Map.of("approvalId", record.id().toString(), "approvalAction", action));
        return record;
    }

    public ApprovalRecord approve(AuthenticatedUser checker, UUID approvalId, String comment) {
        return decide(checker, load(checker.tenantId(), approvalId), true, comment);
    }

    public ApprovalRecord reject(AuthenticatedUser checker, UUID approvalId, String comment) {
        return decide(checker, load(checker.tenantId(), approvalId), false, comment);
    }

    /** Approves the pending request for an action on a record, failing with 404 when none is pending. */
    public ApprovalRecord approvePending(AuthenticatedUser checker, String entityType, UUID entityId, String action,
                                         String comment) {
        return decide(checker, pendingOrThrow(checker.tenantId(), entityType, entityId, action), true, comment);
    }

    public ApprovalRecord rejectPending(AuthenticatedUser checker, String entityType, UUID entityId, String action,
                                        String comment) {
        return decide(checker, pendingOrThrow(checker.tenantId(), entityType, entityId, action), false, comment);
    }

    /** The maker (or an administrator acting on their behalf) withdraws a pending request. */
    public ApprovalRecord cancel(AuthenticatedUser actor, UUID approvalId, String comment) {
        ApprovalRecord record = load(actor.tenantId(), approvalId);
        if (!record.makerId().equals(actor.userId())) {
            throw new ForbiddenActionException("APPROVAL_NOT_MAKER", "Only the maker can withdraw a request");
        }
        ApprovalRecord cancelled = record.cancel(actor.userId(), comment, clock.instant());
        if (!approvals.updateDecision(cancelled)) {
            throw new ConflictException("APPROVAL_ALREADY_DECIDED", "The request has already been decided");
        }
        audit.record(actor, record.entityType(), record.entityId(), "APPROVAL_CANCELLED", null, null, comment,
                java.util.Map.of("approvalId", record.id().toString(), "approvalAction", record.action()));
        return cancelled;
    }

    public Optional<ApprovalRecord> findPending(UUID tenantId, String entityType, UUID entityId, String action) {
        return approvals.findPending(tenantId, entityType, entityId, action);
    }

    public List<ApprovalRecord> historyOf(UUID tenantId, String entityType, UUID entityId) {
        return approvals.listForEntity(tenantId, entityType, entityId);
    }

    /** True when the user is the maker of the pending request, so the UI can hide approve buttons. */
    public boolean isMakerOfPending(AuthenticatedUser user, String entityType, UUID entityId, String action) {
        return approvals.findPending(user.tenantId(), entityType, entityId, action)
                .map(r -> r.makerId().equals(user.userId()))
                .orElse(false);
    }

    private ApprovalRecord pendingOrThrow(UUID tenantId, String entityType, UUID entityId, String action) {
        return approvals.findPending(tenantId, entityType, entityId, action)
                .orElseThrow(() -> new NotFoundException("PendingApproval", action));
    }

    private ApprovalRecord load(UUID tenantId, UUID approvalId) {
        return approvals.findById(tenantId, approvalId)
                .orElseThrow(() -> new NotFoundException("Approval", approvalId));
    }

    private ApprovalRecord decide(AuthenticatedUser checker, ApprovalRecord record, boolean approve, String comment) {
        ApprovalRecord decided = record.decide(approve, checker.userId(), checker.displayName(), comment,
                clock.instant());
        if (!approvals.updateDecision(decided)) {
            throw new ConflictException("APPROVAL_ALREADY_DECIDED", "The request has already been decided");
        }
        audit.record(checker, record.entityType(), record.entityId(),
                approve ? "APPROVAL_GRANTED" : "APPROVAL_REJECTED", null, null, comment,
                java.util.Map.of("approvalId", record.id().toString(), "approvalAction", record.action(),
                        "makerId", record.makerId().toString()));
        return decided;
    }
}
