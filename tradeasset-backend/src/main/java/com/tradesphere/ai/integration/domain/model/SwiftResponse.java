package com.tradesphere.ai.integration.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SWIFT Message Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftResponse {
    private String messageId;
    private String swiftReference;
    private SwiftMessageStatus status;
    private String statusMessage;
    private String acknowledgementTime;
}

