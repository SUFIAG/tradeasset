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
public class AmendmentResponse {
    private UUID id;
    private UUID bgId;
    private int amendmentNumber;
    private String amendmentType;
    private BigDecimal oldAmount;
    private BigDecimal newAmount;
    private LocalDateTime oldExpiryDate;
    private LocalDateTime newExpiryDate;
    private String changesDescription;
    private String status;
    private LocalDateTime requestedAt;
}

