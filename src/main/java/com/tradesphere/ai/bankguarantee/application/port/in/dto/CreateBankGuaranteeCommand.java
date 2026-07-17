package com.tradesphere.ai.bankguarantee.application.port.in.dto;

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
public class CreateBankGuaranteeCommand {
    private String tenantId;
    private String referenceNumber;
    private UUID applicantId;
    private UUID beneficiaryId;
    private BGType bgType;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime expiryDate;
    private String purpose;
    private String governingLaw;
    private String jurisdiction;
    private String specialConditions;
    private List<String> requiredDocuments;
    private boolean reductionAllowed;
    private boolean extensionAllowed;
    private String userId;
}

