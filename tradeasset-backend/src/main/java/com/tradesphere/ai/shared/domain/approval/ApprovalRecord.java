package com.tradesphere.ai.shared.domain.approval;

import com.tradesphere.ai.shared.domain.error.ForbiddenActionException;
import com.tradesphere.ai.shared.domain.error.IllegalStateTransitionException;
import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A maker-checker record (REQ-MKR-001..003). It stores both identities, both timestamps and the
 * checker's comment. The checker can never be the maker; this is enforced here and by a database
 * constraint.
 */
public record ApprovalRecord(
        UUID id,
        UUID tenantId,
        String entityType,
        UUID entityId,
        String action,
        String reference,
        ApprovalStatus status,
        Money amount,
        UUID makerId,
        String makerName,
        Instant makerAt,
        String makerComment,
        UUID checkerId,
        String checkerName,
        Instant checkerAt,
        String checkerComment) {

    public ApprovalRecord {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(makerId, "makerId");
        Objects.requireNonNull(makerAt, "makerAt");
    }

    public static ApprovalRecord request(UUID id, UUID tenantId, String entityType, UUID entityId, String action,
                                         String reference, Money amount, UUID makerId, String makerName,
                                         String makerComment, Instant at) {
        return new ApprovalRecord(id, tenantId, entityType, entityId, action, reference, ApprovalStatus.PENDING,
                amount, makerId, makerName, at, makerComment, null, null, null, null);
    }

    public boolean isPending() {
        return status == ApprovalStatus.PENDING;
    }

    /** Records the checker's decision. Throws when the checker is the maker (REQ-MKR-002). */
    public ApprovalRecord decide(boolean approve, UUID checkerId, String checkerName, String comment, Instant at) {
        Objects.requireNonNull(checkerId, "checkerId");
        if (!isPending()) {
            throw new IllegalStateTransitionException("Approval", status.name(),
                    approve ? "APPROVE" : "REJECT", List.of());
        }
        if (checkerId.equals(makerId)) {
            throw new ForbiddenActionException("FOUR_EYES_VIOLATION",
                    "The maker of a request cannot also be its checker");
        }
        return new ApprovalRecord(id, tenantId, entityType, entityId, action, reference,
                approve ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED, amount, makerId, makerName, makerAt,
                makerComment, checkerId, checkerName, at, comment);
    }

    /** The maker withdraws a pending request. */
    public ApprovalRecord cancel(UUID actorId, String comment, Instant at) {
        if (!isPending()) {
            throw new IllegalStateTransitionException("Approval", status.name(), "CANCEL", List.of());
        }
        return new ApprovalRecord(id, tenantId, entityType, entityId, action, reference, ApprovalStatus.CANCELLED,
                amount, makerId, makerName, makerAt, makerComment, null, null, at, comment);
    }
}
