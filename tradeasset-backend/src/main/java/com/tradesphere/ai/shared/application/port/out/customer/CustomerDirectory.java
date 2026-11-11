package com.tradesphere.ai.shared.application.port.out.customer;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cross-module view of customers. Implemented by the customer module; consumed by every module
 * that needs a party to a transaction. Reads are always scoped to the tenant.
 */
public interface CustomerDirectory {

    Optional<CustomerSnapshot> findById(UUID tenantId, UUID customerId);

    List<CustomerSnapshot> findByIds(UUID tenantId, Collection<UUID> customerIds);

    /**
     * Reasons the customer cannot be party to a new transaction on the given date
     * (REQ-CUS-002, REQ-CUS-004, REQ-CMP-003). An empty list means eligible. Codes:
     * CUSTOMER_NOT_FOUND, CUSTOMER_NOT_ACTIVE, KYC_NOT_COMPLETE, KYC_EXPIRED, COMPLIANCE_HOLD.
     */
    List<Blocker> transactionBlockers(UUID tenantId, UUID customerId, LocalDate asOf);

    record Blocker(String code, String message) {
    }
}
