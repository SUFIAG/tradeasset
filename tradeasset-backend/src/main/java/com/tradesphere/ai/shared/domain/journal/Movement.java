package com.tradesphere.ai.shared.domain.journal;

import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * One posted, immutable row of the movement journal (REQ-MON-006, REQ-MON-007).
 *
 * @param amount             signed amount; positive increases the account balance
 * @param customerId         the customer whose credit is affected; required for
 *                           {@link LedgerAccounts#CREDIT_EXPOSURE}, optional otherwise
 * @param reversesMovementId set only on a reversing movement
 */
public record Movement(
        UUID id,
        UUID tenantId,
        String entityType,
        UUID entityId,
        UUID customerId,
        String account,
        String movementType,
        Money amount,
        String reason,
        String reference,
        UUID actorId,
        Instant occurredAt,
        UUID reversesMovementId) {

    public Movement {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(account, "account");
        Objects.requireNonNull(movementType, "movementType");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(occurredAt, "occurredAt");
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A movement needs a reason");
        }
        if (LedgerAccounts.CREDIT_EXPOSURE.equals(account) && customerId == null) {
            throw new IllegalArgumentException("A credit exposure movement needs a customer");
        }
    }

    /** Builds the compensating movement that cancels this one (REQ-MON-007). */
    public Movement reversal(UUID newId, UUID actor, String why, Instant at) {
        if (reversesMovementId != null) {
            throw new IllegalStateException("A reversal cannot itself be reversed");
        }
        return new Movement(newId, tenantId, entityType, entityId, customerId, account,
                movementType + "_REVERSAL", amount.negate(), why, reference, actor, at, id);
    }
}
