package com.tradesphere.ai.integration.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ERP Integration Port (Hexagonal Architecture)
 * High-performance integration with enterprise systems
 */
public interface ErpGateway {

    /**
     * Sync transaction to ERP (async for performance)
     */
    CompletableFuture<String> syncTransaction(ErpTransaction transaction);

    /**
     * Get customer data from ERP
     */
    CompletableFuture<ErpCustomer> getCustomer(String customerId);

    /**
     * Update accounting entries
     */
    CompletableFuture<Void> postAccountingEntry(AccountingEntry entry);

    /**
     * Get inventory status
     */
    CompletableFuture<InventoryStatus> getInventoryStatus(String itemCode);

    /**
     * ERP Transaction DTO
     */
    record ErpTransaction(
        String transactionId,
        String transactionType,
        String customerId,
        BigDecimal amount,
        String currency,
        LocalDate date,
        Map<String, Object> metadata
    ) {}

    /**
     * ERP Customer DTO
     */
    record ErpCustomer(
        String customerId,
        String customerName,
        String accountNumber,
        String creditLimit,
        String paymentTerms,
        Map<String, Object> additionalData
    ) {}

    /**
     * Accounting Entry DTO
     */
    record AccountingEntry(
        String entryId,
        String accountCode,
        BigDecimal debit,
        BigDecimal credit,
        String description,
        LocalDate postingDate
    ) {}

    /**
     * Inventory Status DTO
     */
    record InventoryStatus(
        String itemCode,
        int quantityOnHand,
        int quantityReserved,
        int quantityAvailable
    ) {}
}

