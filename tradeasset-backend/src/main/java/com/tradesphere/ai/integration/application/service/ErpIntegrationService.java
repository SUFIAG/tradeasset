package com.tradesphere.ai.integration.application.service;

import com.tradesphere.ai.integration.application.port.out.ErpGateway;
import com.tradesphere.ai.integration.application.port.out.ErpGateway.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ERP Integration Service
 * High-performance integration with enterprise systems (SAP, Oracle, etc.)
 *
 * Optimizations:
 * - Async operations for non-blocking I/O
 * - Aggressive caching for reference data
 * - Batch operations for bulk sync
 * - Circuit breaker for resilience
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ErpIntegrationService {

    private final ErpGateway erpGateway;

    /**
     * Sync Letter of Credit to ERP
     */
    public CompletableFuture<String> syncLetterOfCredit(LcTransaction lcTransaction) {
        log.info("Syncing LC to ERP: lcNumber={}", lcTransaction.lcNumber());

        ErpTransaction erpTxn = new ErpTransaction(
            lcTransaction.lcNumber(),
            "LETTER_OF_CREDIT",
            lcTransaction.customerId(),
            lcTransaction.amount(),
            lcTransaction.currency(),
            lcTransaction.issueDate(),
            Map.of(
                "beneficiary", lcTransaction.beneficiary(),
                "expiryDate", lcTransaction.expiryDate().toString(),
                "status", lcTransaction.status()
            )
        );

        return erpGateway.syncTransaction(erpTxn)
            .thenApply(erpRef -> {
                log.info("LC synced to ERP: lcNumber={}, erpRef={}", lcTransaction.lcNumber(), erpRef);
                return erpRef;
            });
    }

    /**
     * Sync Bank Guarantee to ERP
     */
    public CompletableFuture<String> syncBankGuarantee(BgTransaction bgTransaction) {
        log.info("Syncing BG to ERP: bgNumber={}", bgTransaction.bgNumber());

        ErpTransaction erpTxn = new ErpTransaction(
            bgTransaction.bgNumber(),
            "BANK_GUARANTEE",
            bgTransaction.customerId(),
            bgTransaction.amount(),
            bgTransaction.currency(),
            bgTransaction.issueDate(),
            Map.of(
                "guaranteeType", bgTransaction.guaranteeType(),
                "beneficiary", bgTransaction.beneficiary(),
                "expiryDate", bgTransaction.expiryDate().toString()
            )
        );

        return erpGateway.syncTransaction(erpTxn);
    }

    /**
     * Get customer data from ERP (cached for performance)
     */
    @Cacheable(value = "erpCustomers", key = "#customerId")
    public CompletableFuture<ErpCustomer> getCustomer(String customerId) {
        log.debug("Fetching customer from ERP: customerId={}", customerId);
        return erpGateway.getCustomer(customerId);
    }

    /**
     * Post accounting entry (async for high throughput)
     */
    public CompletableFuture<Void> postAccountingEntry(
            String accountCode,
            BigDecimal debit,
            BigDecimal credit,
            String description,
            LocalDate postingDate) {

        log.debug("Posting accounting entry: account={}, debit={}, credit={}",
            accountCode, debit, credit);

        AccountingEntry entry = new AccountingEntry(
            java.util.UUID.randomUUID().toString(),
            accountCode,
            debit,
            credit,
            description,
            postingDate
        );

        return erpGateway.postAccountingEntry(entry);
    }

    /**
     * Post LC accounting entries (debit/credit)
     */
    public CompletableFuture<Void> postLcAccounting(String lcNumber, BigDecimal amount, String currency) {
        log.info("Posting LC accounting entries: lcNumber={}, amount={}", lcNumber, amount);

        // Debit: LC Liability
        CompletableFuture<Void> debit = postAccountingEntry(
            "2100-LC-LIABILITY",
            amount,
            BigDecimal.ZERO,
            "LC Opened: " + lcNumber,
            LocalDate.now()
        );

        // Credit: Customer Account
        CompletableFuture<Void> credit = postAccountingEntry(
            "1100-CUSTOMER-ACCOUNT",
            BigDecimal.ZERO,
            amount,
            "LC Opened: " + lcNumber,
            LocalDate.now()
        );

        return CompletableFuture.allOf(debit, credit);
    }

    /**
     * Get inventory status (cached)
     */
    @Cacheable(value = "erpInventory", key = "#itemCode")
    public CompletableFuture<InventoryStatus> getInventoryStatus(String itemCode) {
        log.debug("Fetching inventory status from ERP: itemCode={}", itemCode);
        return erpGateway.getInventoryStatus(itemCode);
    }

    /**
     * Batch sync transactions (high throughput)
     */
    public CompletableFuture<Map<String, String>> batchSyncTransactions(
            java.util.List<ErpTransaction> transactions) {

        log.info("Batch syncing {} transactions to ERP", transactions.size());

        // Sync all in parallel
        java.util.List<CompletableFuture<Map.Entry<String, String>>> futures = new java.util.ArrayList<>();

        for (ErpTransaction txn : transactions) {
            CompletableFuture<Map.Entry<String, String>> future = erpGateway.syncTransaction(txn)
                .thenApply(ref -> Map.entry(txn.transactionId(), ref));
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                Map<String, String> results = new HashMap<>();
                futures.forEach(f -> {
                    try {
                        Map.Entry<String, String> entry = f.get();
                        results.put(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        log.error("Batch sync error", e);
                    }
                });
                return results;
            });
    }

    /**
     * Sync customer master data
     */
    public CompletableFuture<String> syncCustomerMasterData(CustomerMasterData customerData) {
        log.info("Syncing customer master data: customerId={}", customerData.customerId());

        ErpTransaction txn = new ErpTransaction(
            "CUST-" + customerData.customerId(),
            "CUSTOMER_MASTER",
            customerData.customerId(),
            BigDecimal.ZERO,
            "",
            LocalDate.now(),
            Map.of(
                "customerName", customerData.customerName(),
                "accountNumber", customerData.accountNumber(),
                "creditLimit", customerData.creditLimit(),
                "paymentTerms", customerData.paymentTerms()
            )
        );

        return erpGateway.syncTransaction(txn);
    }

    // DTOs

    public record LcTransaction(
        String lcNumber,
        String customerId,
        BigDecimal amount,
        String currency,
        LocalDate issueDate,
        LocalDate expiryDate,
        String beneficiary,
        String status
    ) {}

    public record BgTransaction(
        String bgNumber,
        String customerId,
        BigDecimal amount,
        String currency,
        LocalDate issueDate,
        LocalDate expiryDate,
        String guaranteeType,
        String beneficiary
    ) {}

    public record CustomerMasterData(
        String customerId,
        String customerName,
        String accountNumber,
        String creditLimit,
        String paymentTerms
    ) {}
}

