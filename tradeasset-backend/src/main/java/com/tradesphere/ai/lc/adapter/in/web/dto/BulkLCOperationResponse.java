package com.tradesphere.ai.lc.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Bulk operation response with success/failure details
 */
@Data
@Builder
@AllArgsConstructor
public class BulkLCOperationResponse {

    private int totalRequested;
    private int successCount;
    private int failureCount;
    private List<OperationResult> results;

    @Data
    @Builder
    @AllArgsConstructor
    public static class OperationResult {
        private String lcId;
        private String lcNumber;
        private boolean success;
        private String errorMessage;
    }
}

