package com.tradesphere.ai.integration.adapter.out.erp;
import com.tradesphere.ai.integration.application.port.out.ErpGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionErpGatewayAdapter implements ErpGateway {
    private final RestTemplate restTemplate;
    @Value("${erp.system.type:SAP}")
    private String erpSystemType;
    @Value("${erp.api.url:https://erp.example.com/api}")
    private String erpApiUrl;
    @Value("${erp.api.key:}")
    private String apiKey;
    @Value("${erp.api.username:}")
    private String username;
    @Value("${erp.api.password:}")
    private String password;
    @Override
    public CompletableFuture<String> syncTransaction(ErpTransaction transaction) {
        log.info("Syncing transaction to ERP: id={}, type={}", transaction.transactionId(), transaction.transactionType());
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = createErpHeaders();
                Map<String, Object> erpTransaction = new HashMap<>();
                erpTransaction.put("transactionId", transaction.transactionId());
                erpTransaction.put("transactionType", transaction.transactionType());
                erpTransaction.put("customerId", transaction.customerId());
                erpTransaction.put("amount", transaction.amount().toString());
                erpTransaction.put("currency", transaction.currency());
                erpTransaction.put("date", transaction.date().toString());
                erpTransaction.putAll(transaction.metadata());
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(erpTransaction, headers);
                ResponseEntity<Map> response = restTemplate.exchange(erpApiUrl + "/transactions", HttpMethod.POST, entity, Map.class);
                String id = response.getBody() != null ? (String) response.getBody().get("id") : transaction.transactionId();
                log.info("Transaction synced to ERP: erpId={}", id);
                return id;
            } catch (Exception e) {
                log.error("Failed to sync transaction to ERP", e);
                throw new RuntimeException("Failed: " + e.getMessage(), e);
            }
        });
    }
    @Override
    public CompletableFuture<ErpCustomer> getCustomer(String customerId) {
        log.info("Fetching customer from ERP: {}", customerId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = createErpHeaders();
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<Map> response = restTemplate.exchange(erpApiUrl + "/customers/" + customerId, HttpMethod.GET, entity, Map.class);
                Map<String, Object> data = response.getBody() != null ? response.getBody() : new HashMap<>();
                return new ErpCustomer(
                    (String) data.getOrDefault("customerId", customerId),
                    (String) data.getOrDefault("customerName", ""),
                    (String) data.getOrDefault("accountNumber", ""),
                    (String) data.getOrDefault("creditLimit", "0"),
                    (String) data.getOrDefault("paymentTerms", ""),
                    (Map<String, Object>) data.getOrDefault("additionalData", new HashMap<>())
                );
            } catch (Exception e) {
                log.error("Failed to fetch customer", e);
                throw new RuntimeException("Failed: " + e.getMessage(), e);
            }
        });
    }
    @Override
    public CompletableFuture<Void> postAccountingEntry(AccountingEntry entry) {
        log.info("Posting accounting entry: {}", entry.entryId());
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = createErpHeaders();
                Map<String, Object> erpEntry = new HashMap<>();
                erpEntry.put("entryId", entry.entryId());
                erpEntry.put("accountCode", entry.accountCode());
                erpEntry.put("debit", entry.debit().toString());
                erpEntry.put("credit", entry.credit().toString());
                erpEntry.put("description", entry.description());
                erpEntry.put("postingDate", entry.postingDate().toString());
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(erpEntry, headers);
                restTemplate.exchange(erpApiUrl + "/accounting/entries", HttpMethod.POST, entity, Map.class);
                return null;
            } catch (Exception e) {
                log.error("Failed to post entry", e);
                throw new RuntimeException("Failed: " + e.getMessage(), e);
            }
        });
    }
    @Override
    public CompletableFuture<InventoryStatus> getInventoryStatus(String itemCode) {
        log.info("Fetching inventory: {}", itemCode);
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = createErpHeaders();
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<Map> response = restTemplate.exchange(erpApiUrl + "/inventory/" + itemCode, HttpMethod.GET, entity, Map.class);
                Map<String, Object> data = response.getBody() != null ? response.getBody() : new HashMap<>();
                return new InventoryStatus(
                    (String) data.getOrDefault("itemCode", itemCode),
                    ((Number) data.getOrDefault("quantityOnHand", 0)).intValue(),
                    ((Number) data.getOrDefault("quantityReserved", 0)).intValue(),
                    ((Number) data.getOrDefault("quantityAvailable", 0)).intValue()
                );
            } catch (Exception e) {
                log.error("Failed to fetch inventory", e);
                throw new RuntimeException("Failed: " + e.getMessage(), e);
            }
        });
    }
    private HttpHeaders createErpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        switch (erpSystemType.toUpperCase()) {
            case "SAP" -> headers.setBasicAuth(username, password);
            case "ORACLE" -> headers.setBearerAuth(apiKey);
            case "DYNAMICS" -> { headers.setBearerAuth(apiKey); headers.set("OData-Version", "4.0"); }
            case "NETSUITE" -> headers.set("Authorization", "OAuth " + apiKey);
            default -> headers.set("X-API-Key", apiKey);
        }
        return headers;
    }
}
