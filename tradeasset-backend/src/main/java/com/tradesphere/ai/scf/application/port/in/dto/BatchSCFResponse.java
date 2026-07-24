package com.tradesphere.ai.scf.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response for batch SCF operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchSCFResponse {

    private String batchReference;
    private BatchSCFCommand.BatchOperationType operationType;
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<BatchResult> results;
    private LocalDateTime processedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatchResult {
        private UUID scfId;
        private boolean success;
        private String message;
        private String errorCode;
    }
}

