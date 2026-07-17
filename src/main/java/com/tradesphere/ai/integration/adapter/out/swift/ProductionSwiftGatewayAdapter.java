package com.tradesphere.ai.integration.adapter.out.swift;

import com.tradesphere.ai.integration.application.port.out.SwiftGateway;
import com.tradesphere.ai.integration.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Production SWIFT Gateway Adapter
 * Integrates with SWIFT Alliance Lite2, SWIFT Alliance Access, or SWIFT Alliance Gateway
 *
 * Supported message types:
 * - MT700: Letter of Credit Issuance
 * - MT710: Advise of Third Bank's Letter of Credit
 * - MT720: Transfer of Letter of Credit
 * - MT760: Bank Guarantee Issuance
 * - MT103: Single Customer Credit Transfer (Payments)
 * - MT202: General Financial Institution Transfer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionSwiftGatewayAdapter implements SwiftGateway {

    private final RestTemplate restTemplate;

    @Value("${swift.gateway.url:https://swift-alliance.example.com/api}")
    private String swiftGatewayUrl;

    @Value("${swift.bic:EXAMPLEUS33}")
    private String institutionBIC;

    @Value("${swift.api.key}")
    private String apiKey;

    @Value("${swift.api.secret}")
    private String apiSecret;

    // Message tracking
    private final Map<String, SwiftMessageStatus> messageStatusCache = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<SwiftResponse> sendMT700(MT700Message message) {
        log.info("Sending MT700 (LC Issuance) to SWIFT: lcNumber={}", message.lcNumber());

        return CompletableFuture.supplyAsync(() -> {
            try {
                String swiftMessage = formatMT700(message);
                String messageId = sendToSwift("MT700", swiftMessage, message.receiverBIC());

                return new SwiftResponse(
                    true,
                    messageId,
                    "MT700",
                    LocalDateTime.now(),
                    "Message sent successfully"
                );
            } catch (Exception e) {
                log.error("Failed to send MT700", e);
                return new SwiftResponse(
                    false,
                    null,
                    "MT700",
                    LocalDateTime.now(),
                    "Failed to send: " + e.getMessage()
                );
            }
        });
    }

    @Override
    public CompletableFuture<SwiftResponse> sendMT760(MT760Message message) {
        log.info("Sending MT760 (Bank Guarantee) to SWIFT: bgNumber={}", message.bgNumber());

        return CompletableFuture.supplyAsync(() -> {
            try {
                String swiftMessage = formatMT760(message);
                String messageId = sendToSwift("MT760", swiftMessage, message.receiverBIC());

                return new SwiftResponse(
                    true,
                    messageId,
                    "MT760",
                    LocalDateTime.now(),
                    "Message sent successfully"
                );
            } catch (Exception e) {
                log.error("Failed to send MT760", e);
                return new SwiftResponse(
                    false,
                    null,
                    "MT760",
                    LocalDateTime.now(),
                    "Failed to send: " + e.getMessage()
                );
            }
        });
    }

    @Override
    public CompletableFuture<SwiftResponse> sendMT103(MT103Message message) {
        log.info("Sending MT103 (Payment) to SWIFT: amount={}", message.amount());

        return CompletableFuture.supplyAsync(() -> {
            try {
                String swiftMessage = formatMT103(message);
                String messageId = sendToSwift("MT103", swiftMessage, message.receiverBIC());

                return new SwiftResponse(
                    true,
                    messageId,
                    "MT103",
                    LocalDateTime.now(),
                    "Payment message sent successfully"
                );
            } catch (Exception e) {
                log.error("Failed to send MT103", e);
                return new SwiftResponse(
                    false,
                    null,
                    "MT103",
                    LocalDateTime.now(),
                    "Failed to send: " + e.getMessage()
                );
            }
        });
    }

    @Override
    public SwiftMessageStatus checkMessageStatus(String messageId) {
        log.debug("Checking SWIFT message status: {}", messageId);

        // Check cache first
        SwiftMessageStatus cached = messageStatusCache.get(messageId);
        if (cached != null) {
            return cached;
        }

        // Query SWIFT gateway
        try {
            HttpHeaders headers = createSwiftHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                swiftGatewayUrl + "/messages/" + messageId + "/status",
                HttpMethod.GET,
                entity,
                Map.class
            );

            SwiftMessageStatus status = parseStatusResponse(response.getBody());
            messageStatusCache.put(messageId, status);
            return status;

        } catch (Exception e) {
            log.error("Failed to check message status", e);
            return new SwiftMessageStatus(
                messageId,
                "UNKNOWN",
                null,
                "Failed to check status"
            );
        }
    }

    @Override
    public boolean validateBIC(String bic) {
        // BIC format: 4 letters (bank code) + 2 letters (country) + 2 alphanumeric (location) + optional 3 alphanumeric (branch)
        return bic != null && bic.matches("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    }

    /**
     * Send message to SWIFT network
     */
    private String sendToSwift(String messageType, String swiftMessage, String receiverBIC) {
        log.debug("Sending {} message to {}", messageType, receiverBIC);

        HttpHeaders headers = createSwiftHeaders();

        Map<String, Object> request = Map.of(
            "messageType", messageType,
            "senderBIC", institutionBIC,
            "receiverBIC", receiverBIC,
            "messageText", swiftMessage,
            "priority", "NORMAL",
            "deliveryMode", "ONLINE"
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            swiftGatewayUrl + "/messages/send",
            HttpMethod.POST,
            entity,
            Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String messageId = (String) response.getBody().get("messageId");
            log.info("SWIFT message sent successfully: messageId={}", messageId);

            // Cache initial status
            messageStatusCache.put(messageId, new SwiftMessageStatus(
                messageId,
                "SENT",
                LocalDateTime.now(),
                "Message sent to SWIFT network"
            ));

            return messageId;
        } else {
            throw new RuntimeException("Failed to send SWIFT message: " + response.getStatusCode());
        }
    }

    /**
     * Create authentication headers for SWIFT API
     */
    private HttpHeaders createSwiftHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-SWIFT-API-Key", apiKey);
        headers.set("X-SWIFT-API-Secret", apiSecret);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        return headers;
    }

    /**
     * Format MT700 message (Letter of Credit)
     */
    private String formatMT700(MT700Message msg) {
        return String.format("""
            :27:1/1
            :40A:IRREVOCABLE
            :20:%s
            :31C:%s
            :31D:%s
            :50:%s
            :59:%s
            :32B:%s%s
            :41D:%s
            :44C:%s
            :45A:%s
            :46A:%s
            :47A:%s
            """,
            msg.lcNumber(),
            msg.issueDate(),
            msg.expiryDate(),
            msg.applicant(),
            msg.beneficiary(),
            msg.currency(), msg.amount(),
            msg.availableWith(),
            msg.latestShipmentDate(),
            msg.goodsDescription(),
            msg.documentsRequired(),
            msg.additionalConditions()
        );
    }

    /**
     * Format MT760 message (Bank Guarantee)
     */
    private String formatMT760(MT760Message msg) {
        return String.format("""
            :27:1/1
            :20:%s
            :23:%s
            :31C:%s
            :31E:%s
            :52A:%s
            :50:%s
            :59:%s
            :32B:%s%s
            :39B:%s
            :77C:%s
            """,
            msg.bgNumber(),
            msg.guaranteeType(),
            msg.issueDate(),
            msg.expiryDate(),
            msg.issuingBank(),
            msg.applicant(),
            msg.beneficiary(),
            msg.currency(), msg.amount(),
            msg.maximumAmount(),
            msg.termsAndConditions()
        );
    }

    /**
     * Format MT103 message (Payment)
     */
    private String formatMT103(MT103Message msg) {
        return String.format("""
            :20:%s
            :23B:CRED
            :32A:%s%s%s
            :50K:%s
            :59:%s
            :70:%s
            :71A:SHA
            """,
            msg.referenceNumber(),
            msg.valueDate(),
            msg.currency(),
            msg.amount(),
            msg.orderingCustomer(),
            msg.beneficiaryCustomer(),
            msg.remittanceInformation()
        );
    }

    /**
     * Parse status response from SWIFT
     */
    private SwiftMessageStatus parseStatusResponse(Map<String, Object> response) {
        return new SwiftMessageStatus(
            (String) response.get("messageId"),
            (String) response.get("status"),
            LocalDateTime.parse((String) response.get("timestamp")),
            (String) response.get("statusDetails")
        );
    }
}

