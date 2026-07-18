package com.tradesphere.ai.integration.domain.model;

import java.time.LocalDateTime;

/**
 * SWIFT Message Status
 */
public record SwiftMessageStatus(
    String messageId,
    String status,
    LocalDateTime timestamp,
    String statusDetails
) {}

