package com.tradesphere.ai.integration.adapter.out.erp;

import com.tradesphere.ai.integration.application.port.out.ErpGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ERP Gateway Adapter (Stub Implementation)
 *
 * Production: Replace with actual ERP integration
 * - SAP: Use SAP JCo or REST APIs
 * - Oracle: Use Oracle Integration Cloud
 * - Microsoft Dynamics: Use Web API
 * - Custom: REST/SOAP adapters
 *
 * This stub provides development/testing capability
 */
@Component
@ConditionalOnProperty(name = "integration.erp.mode", havingValue = "stub", matchIfMissing = true)
@Slf4j
public class ErpGatewayStubAdapter implements ErpGateway {

    private final Map<String, ErpTransaction> transactionStore = new ConcurrentHashMap<>();
    private final Map<String, ErpCustomer> customerStore = new ConcurrentHashMap<>();

    public ErpGatewayStubAdapter() {
        // Seed with sample customer data
        customerStore.put("CUST001", new ErpCustomer(
            "CUST001",
            "ABC Corporation",
            "ACC-001",
            "1000000.00",
            "NET30",
            Map.of("industry", "Manufacturing", "rating", "A")
        ));
    }

    @Override
    public CompletableFuture<String> syncTransaction(ErpTransaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            String erpRef = "ERP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            log.info("ERP Transaction Synced (STUB): type={}, customerId={}, amount={}, erpRef={}",
                transaction.transactionType(), transaction.customerId(),
                transaction.amount(), erpRef);

            // Store transaction
            transactionStore.put(erpRef, transaction);

            // Simulate processing delay
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return erpRef;
        });
    }

    @Override
    public CompletableFuture<ErpCustomer> getCustomer(String customerId) {
        return CompletableFuture.supplyAsync(() -> {
            ErpCustomer customer = customerStore.get(customerId);

            if (customer == null) {
                log.warn("ERP Customer not found (STUB): customerId={}", customerId);
                // Return default customer
                customer = new ErpCustomer(
                    customerId,
                    "Unknown Customer",
                    "ACC-" + customerId,
                    "500000.00",
                    "NET30",
                    new HashMap<>()
                );
            }

            log.debug("ERP Customer Retrieved (STUB): customerId={}, name={}",
                customer.customerId(), customer.customerName());

            return customer;
        });
    }

    @Override
    public CompletableFuture<Void> postAccountingEntry(AccountingEntry entry) {
        return CompletableFuture.runAsync(() -> {
            log.info("ERP Accounting Entry Posted (STUB): account={}, debit={}, credit={}",
                entry.accountCode(), entry.debit(), entry.credit());

            // Simulate posting delay
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public CompletableFuture<InventoryStatus> getInventoryStatus(String itemCode) {
        return CompletableFuture.supplyAsync(() -> {
            // Return mock inventory status
            InventoryStatus status = new InventoryStatus(
                itemCode,
                1000,  // On hand
                200,   // Reserved
                800    // Available
            );

            log.debug("ERP Inventory Status (STUB): itemCode={}, available={}",
                itemCode, status.quantityAvailable());

            return status;
        });
    }
}

