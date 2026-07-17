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
public class RequestAmendmentCommand {
    private UUID bgId;
    private String tenantId;
    private String userId;
    private String amendmentType;
    private BigDecimal newAmount;
    private LocalDateTime newExpiryDate;
    private String changesDescription;
}

