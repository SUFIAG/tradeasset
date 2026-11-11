package com.tradesphere.ai.shared.application.service;

import com.tradesphere.ai.shared.application.port.out.MovementJournalPort;
import com.tradesphere.ai.shared.domain.error.BusinessRuleViolationException;
import com.tradesphere.ai.shared.domain.error.NotFoundException;
import com.tradesphere.ai.shared.domain.journal.Movement;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The only way to change a balance (REQ-MON-006, REQ-MON-007). Balances are always derived from
 * the journal; a correction is a reversing movement, never an edit.
 */
@Service
public class Ledger {

    private final MovementJournalPort journal;
    private final Clock clock;

    public Ledger(MovementJournalPort journal, Clock clock) {
        this.journal = journal;
        this.clock = clock;
    }

    /** Starts a batch of movements that is posted atomically by {@link Batch#post()}. */
    public Batch batch(UUID tenantId, String entityType, UUID entityId, UUID actorId) {
        return new Batch(tenantId, entityType, entityId, actorId);
    }

    public Money balance(UUID tenantId, String entityType, UUID entityId, String account, String currency) {
        BigDecimal value = journal.balance(tenantId, entityType, entityId, account, currency);
        return Money.of(value == null ? BigDecimal.ZERO : value, currency);
    }

    public Map<String, BigDecimal> customerBalances(UUID tenantId, UUID customerId, String account) {
        return journal.customerBalances(tenantId, customerId, account);
    }

    public List<Movement> movementsOf(UUID tenantId, String entityType, UUID entityId) {
        return journal.listForEntity(tenantId, entityType, entityId);
    }

    /** Posts the compensating movement for a posted one. A movement can be reversed once. */
    public Movement reverse(UUID tenantId, UUID movementId, UUID actorId, String reason) {
        Movement original = journal.findById(tenantId, movementId)
                .orElseThrow(() -> new NotFoundException("Movement", movementId));
        if (original.reversesMovementId() != null) {
            throw new BusinessRuleViolationException("MOVEMENT_IS_REVERSAL", "A reversing movement cannot be reversed");
        }
        Movement reversal = original.reversal(UUID.randomUUID(), actorId, reason, clock.instant());
        journal.post(List.of(reversal));
        return reversal;
    }

    /** Collects movements for one record and posts them together. */
    public final class Batch {
        private final UUID tenantId;
        private final String entityType;
        private final UUID entityId;
        private final UUID actorId;
        private final List<Movement> movements = new ArrayList<>();

        private Batch(UUID tenantId, String entityType, UUID entityId, UUID actorId) {
            this.tenantId = tenantId;
            this.entityType = entityType;
            this.entityId = entityId;
            this.actorId = actorId;
        }

        /**
         * @param signedAmount positive to increase the account balance, negative to decrease it
         */
        public Batch add(String account, String movementType, Money signedAmount, UUID customerId, String reason,
                         String reference) {
            if (signedAmount.isZero()) {
                return this;
            }
            movements.add(new Movement(UUID.randomUUID(), tenantId, entityType, entityId, customerId, account,
                    movementType, signedAmount, reason, reference, actorId, clock.instant(), null));
            return this;
        }

        public List<Movement> post() {
            if (!movements.isEmpty()) {
                journal.post(movements);
            }
            return List.copyOf(movements);
        }
    }
}
