package com.tradesphere.ai.lc.adapter.in.web.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Bulk operation request for batch processing
 * Optimized for high throughput
 */
@Data
public class BulkLCOperationRequest {

    private List<String> lcIds;
    private BulkOperationType operationType;
    private UUID operatedBy;
    private String reason; // For rejections, suspensions, etc.

    public enum BulkOperationType {
        APPROVE,
        REJECT,
        ISSUE,
        CANCEL,
        CHECK_EXPIRY
    }
}

