package com.tradesphere.ai.integration.application.service;

import com.tradesphere.ai.integration.application.port.out.SwiftGateway;
import com.tradesphere.ai.integration.application.port.out.SwiftGateway.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SWIFT Integration Service
 * High-throughput message processing with connection pooling
 *
 * Optimizations:
 * - Async message sending (non-blocking)
 * - Message batching for bulk operations
 * - Connection pooling for high throughput
 * - Response caching for status queries
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SwiftIntegrationService {

    private final SwiftGateway swiftGateway;

    // Message tracking store (in-memory for performance)
    private final Map<String, MessageStatus> messageStatusCache = new ConcurrentHashMap<>();

    /**
     * Send Letter of Credit (MT700)
     * Async for high throughput
     */
    public CompletableFuture<String> sendLetterOfCredit(LetterOfCreditData lcData) {
        log.info("Sending LC via SWIFT MT700: lcNumber={}", lcData.lcNumber());

        Map<String, String> fields = new HashMap<>();
        fields.put("40A", "IRREVOCABLE");
        fields.put("20", lcData.lcNumber());
        fields.put("31C", lcData.issueDate().toString());
        fields.put("31D", lcData.expiryDate().toString());
        fields.put("32B", lcData.currency() + lcData.amount());
        fields.put("50", lcData.applicant());
        fields.put("59", lcData.beneficiary());
        fields.put("45A", lcData.goodsDescription());
        fields.put("46A", lcData.documentsRequired());
        fields.put("47A", lcData.specialInstructions());

        SwiftMessage message = new SwiftMessage(
            "MT700",
            lcData.issuingBank(),
            lcData.advisingBank(),
            fields,
            null
        );

        return swiftGateway.sendMessage(message)
            .thenApply(ref -> {
                messageStatusCache.put(ref, MessageStatus.SENT);
                log.info("LC sent successfully: ref={}", ref);
                return ref;
            });
    }

    /**
     * Send Bank Guarantee (MT760)
     */
    public CompletableFuture<String> sendBankGuarantee(BankGuaranteeData bgData) {
        log.info("Sending BG via SWIFT MT760: bgNumber={}", bgData.guaranteeNumber());

        Map<String, String> fields = new HashMap<>();
        fields.put("27", "1/1");
        fields.put("20", bgData.guaranteeNumber());
        fields.put("23", bgData.guaranteeType());
        fields.put("32B", bgData.currency() + bgData.amount());
        fields.put("30", bgData.issueDate().toString());
        fields.put("31E", bgData.expiryDate().toString());
        fields.put("50", bgData.applicant());
        fields.put("59", bgData.beneficiary());
        fields.put("77C", bgData.termsAndConditions());

        SwiftMessage message = new SwiftMessage(
            "MT760",
            bgData.issuingBank(),
            bgData.beneficiaryBank(),
            fields,
            null
        );

        return swiftGateway.sendMessage(message)
            .thenApply(ref -> {
                messageStatusCache.put(ref, MessageStatus.SENT);
                log.info("BG sent successfully: ref={}", ref);
                return ref;
            });
    }

    /**
     * Send Payment (MT103)
     */
    public CompletableFuture<String> sendPayment(PaymentData paymentData) {
        log.info("Sending payment via SWIFT MT103: ref={}", paymentData.paymentReference());

        Map<String, String> fields = new HashMap<>();
        fields.put("20", paymentData.paymentReference());
        fields.put("32A", paymentData.valueDate() + paymentData.currency() + paymentData.amount());
        fields.put("50K", paymentData.orderingCustomer());
        fields.put("59", paymentData.beneficiary());
        fields.put("70", paymentData.remittanceInfo());
        fields.put("71A", "SHA");  // Shared charges

        SwiftMessage message = new SwiftMessage(
            "MT103",
            paymentData.senderBank(),
            paymentData.receiverBank(),
            fields,
            null
        );

        return swiftGateway.sendMessage(message)
            .thenApply(ref -> {
                messageStatusCache.put(ref, MessageStatus.SENT);
                log.info("Payment sent successfully: ref={}", ref);
                return ref;
            });
    }

    /**
     * Receive incoming SWIFT message
     */
    public CompletableFuture<SwiftMessage> receiveMessage(String messageRef) {
        log.debug("Receiving SWIFT message: ref={}", messageRef);
        return swiftGateway.receiveMessage(messageRef);
    }

    /**
     * Get message status (cached for performance)
     */
    @Cacheable(value = "swiftMessageStatus", key = "#messageRef")
    public MessageStatus getMessageStatus(String messageRef) {
        MessageStatus cachedStatus = messageStatusCache.get(messageRef);
        if (cachedStatus != null) {
            return cachedStatus;
        }
        return swiftGateway.getMessageStatus(messageRef);
    }

    /**
     * Validate SWIFT message format
     */
    public boolean validateMessage(SwiftMessage message) {
        return swiftGateway.validateMessage(message);
    }

    /**
     * Batch send messages (high throughput)
     */
    public CompletableFuture<Map<String, String>> batchSendMessages(java.util.List<SwiftMessage> messages) {
        log.info("Batch sending {} SWIFT messages", messages.size());

        // Send all messages in parallel for maximum throughput
        java.util.List<CompletableFuture<Map.Entry<Integer, String>>> futures = new java.util.ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            final int index = i;
            SwiftMessage msg = messages.get(i);
            CompletableFuture<Map.Entry<Integer, String>> future = swiftGateway.sendMessage(msg)
                .thenApply(ref -> Map.entry(index, ref));
            futures.add(future);
        }

        // Wait for all to complete
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                Map<String, String> results = new HashMap<>();
                futures.forEach(f -> {
                    try {
                        Map.Entry<Integer, String> entry = f.get();
                        results.put("message_" + entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        log.error("Batch send error", e);
                    }
                });
                return results;
            });
    }

    // DTOs

    public record LetterOfCreditData(
        String lcNumber,
        String issueDate,
        String expiryDate,
        String currency,
        String amount,
        String applicant,
        String beneficiary,
        String goodsDescription,
        String documentsRequired,
        String specialInstructions,
        String issuingBank,
        String advisingBank
    ) {}

    public record BankGuaranteeData(
        String guaranteeNumber,
        String guaranteeType,
        String issueDate,
        String expiryDate,
        String currency,
        String amount,
        String applicant,
        String beneficiary,
        String termsAndConditions,
        String issuingBank,
        String beneficiaryBank
    ) {}

    public record PaymentData(
        String paymentReference,
        String valueDate,
        String currency,
        String amount,
        String orderingCustomer,
        String beneficiary,
        String remittanceInfo,
        String senderBank,
        String receiverBank
    ) {}
}

