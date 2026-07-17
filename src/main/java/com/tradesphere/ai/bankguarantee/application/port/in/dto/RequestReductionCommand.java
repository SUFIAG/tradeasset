package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReductionCommand {
    private UUID bgId;
    private String tenantId;
    private String userId;
    private BigDecimal reductionAmount;
    private BigDecimal newTotalAmount;
    private String reductionReason;
    private String supportingEvidence;
}

