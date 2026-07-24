package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterClaimCommand {
    private UUID bgId;
    private String tenantId;
    private String userId;
    private BigDecimal claimAmount;
    private String claimCurrency;
    private String claimReason;
    private String claimType;
    private List<UUID> supportingDocuments;
}

