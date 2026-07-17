package com.tradesphere.ai.integration.adapter.out.swift;

import com.tradesphere.ai.integration.application.port.out.SwiftGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SWIFT Gateway Adapter (Stub Implementation)
 *
 * Production: Replace with actual SWIFT Alliance integration
 * - SWIFT Alliance Gateway
 * - SWIFT Alliance Access
 * - SWIFT Alliance Lite2
 *
 * This stub provides development/testing capability
 */
@Component
@ConditionalOnProperty(name = "integration.swift.mode", havingValue = "stub", matchIfMissing = true)
@Slf4j
public class SwiftGatewayStubAdapter implements SwiftGateway {

    private final Map<String, SwiftMessage> messageStore = new ConcurrentHashMap<>();
    private final Map<String, MessageStatus> statusStore = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<String> sendMessage(SwiftMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            String messageRef = "SWIFT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            log.info("SWIFT Message Sent (STUB): type={}, sender={}, receiver={}, ref={}",
                message.messageType(), message.senderBic(), message.receiverBic(), messageRef);

            // Store message
            messageStore.put(messageRef, message);
            statusStore.put(messageRef, MessageStatus.SENT);

            // Simulate network delay
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Update status to delivered
            statusStore.put(messageRef, MessageStatus.DELIVERED);

            return messageRef;
        });
    }

    @Override
    public CompletableFuture<SwiftMessage> receiveMessage(String messageRef) {
        return CompletableFuture.supplyAsync(() -> {
            SwiftMessage message = messageStore.get(messageRef);
            if (message == null) {
                log.warn("SWIFT Message not found: ref={}", messageRef);
                throw new IllegalArgumentException("Message not found: " + messageRef);
            }
            log.info("SWIFT Message Received (STUB): ref={}", messageRef);
            return message;
        });
    }

    @Override
    public boolean validateMessage(SwiftMessage message) {
        // Basic validation
        boolean valid = message.messageType() != null &&
                       message.senderBic() != null &&
                       message.receiverBic() != null &&
                       message.fields() != null;

        log.debug("SWIFT Message Validation (STUB): type={}, valid={}",
            message.messageType(), valid);

        return valid;
    }

    @Override
    public MessageStatus getMessageStatus(String messageRef) {
        MessageStatus status = statusStore.getOrDefault(messageRef, MessageStatus.PENDING);
        log.debug("SWIFT Message Status (STUB): ref={}, status={}", messageRef, status);
        return status;
    }
}

