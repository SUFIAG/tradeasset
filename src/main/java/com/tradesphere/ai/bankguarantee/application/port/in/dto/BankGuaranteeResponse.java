package com.tradesphere.ai.bankguarantee.application.port.in.dto;

import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankGuaranteeResponse {
    private UUID id;
    private String tenantId;
    private String bgNumber;
    private String referenceNumber;
    private UUID applicantId;
    private UUID beneficiaryId;
    private String issuingBank;
    private BGType bgType;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime applicationDate;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private BGStatus status;
    private String purpose;
    private int claimsCount;
    private BigDecimal claimedAmount;
    private BigDecimal availableAmount;
    private List<String> requiredDocuments;
    private LocalDateTime createdAt;
}

