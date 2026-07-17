package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    private UUID id;
    private UUID bgId;
    private String claimNumber;
    private BigDecimal claimAmount;
    private String claimCurrency;
    private String claimReason;
    private String status;
    private BigDecimal approvedAmount;
    private LocalDateTime receivedDate;
}

