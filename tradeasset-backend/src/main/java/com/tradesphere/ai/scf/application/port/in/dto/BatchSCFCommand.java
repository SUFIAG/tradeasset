package com.tradesphere.ai.scf.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Command to process batch SCF operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchSCFCommand {

    private UUID tenantId;
    private String batchReference;
    private BatchOperationType operationType;
    private List<UUID> scfIds;
    private UUID processedBy;

    public enum BatchOperationType {
        APPROVE,
        DISBURSE,
        REPAY,
        CANCEL
    }
}

