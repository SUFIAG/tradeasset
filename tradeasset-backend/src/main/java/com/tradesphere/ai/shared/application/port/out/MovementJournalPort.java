package com.tradesphere.ai.shared.application.port.out;

import com.tradesphere.ai.shared.domain.journal.Movement;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Append-only movement journal (table {@code trade_movements}). Rows are never updated or deleted. */
public interface MovementJournalPort {

    void post(Collection<Movement> movements);

    Optional<Movement> findById(UUID tenantId, UUID movementId);

    /** Oldest first. */
    List<Movement> listForEntity(UUID tenantId, String entityType, UUID entityId);

    /** Balance of one account of one record in one currency. Zero when there are no movements. */
    BigDecimal balance(UUID tenantId, String entityType, UUID entityId, String account, String currency);

    /** Balances of one account of one record, keyed by currency code. */
    Map<String, BigDecimal> balancesByCurrency(UUID tenantId, String entityType, UUID entityId, String account);

    /** A customer's balance on an account, keyed by currency code (for example credit exposure). */
    Map<String, BigDecimal> customerBalances(UUID tenantId, UUID customerId, String account);

    /** Customer balances on an account for several customers: customerId to (currency to balance). */
    Map<UUID, Map<String, BigDecimal>> customerBalances(UUID tenantId, Collection<UUID> customerIds, String account);

    /** Tenant-wide balance on an account keyed by currency, optionally limited to one entity type. */
    Map<String, BigDecimal> tenantBalances(UUID tenantId, String account, String entityTypeOrNull);
}
