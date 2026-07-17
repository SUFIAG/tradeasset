package com.tradesphere.ai.integration.application.port.out;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SWIFT Integration Port (Hexagonal Architecture)
 * High-throughput message exchange with SWIFT network
 */
public interface SwiftGateway {
    
    /**
     * Send SWIFT message (async for high throughput)
     * @return Message reference ID
     */
    CompletableFuture<String> sendMessage(SwiftMessage message);
    
    /**
     * Receive SWIFT messages (polling or push)
     */
    CompletableFuture<SwiftMessage> receiveMessage(String messageRef);
    
    /**
     * Validate SWIFT message format
     */
    boolean validateMessage(SwiftMessage message);
    
    /**
     * Get message status
     */
    MessageStatus getMessageStatus(String messageRef);
    
    /**
     * SWIFT Message DTO
     */
    record SwiftMessage(
        String messageType,  // MT700, MT760, etc.
        String senderBic,
        String receiverBic,
        Map<String, String> fields,
        byte[] rawMessage
    ) {}
    
    enum MessageStatus {
        PENDING, SENT, DELIVERED, FAILED, ACKNOWLEDGED
    }
}

