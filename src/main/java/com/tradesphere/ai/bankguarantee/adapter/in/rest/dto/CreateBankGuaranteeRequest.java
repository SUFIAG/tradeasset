package com.tradesphere.ai.bankguarantee.adapter.in.rest.dto;

import com.tradesphere.ai.bankguarantee.domain.model.BGType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a Bank Guarantee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBankGuaranteeRequest {
    private String referenceNumber;

    @NotNull(message = "Applicant ID is required")
    private UUID applicantId;

    @NotNull(message = "Beneficiary ID is required")
    private UUID beneficiaryId;

    @NotNull(message = "BG type is required")
    private BGType bgType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private String currency;

    @NotNull(message = "Expiry date is required")
    private LocalDateTime expiryDate;

    @NotNull(message = "Purpose is required")
    private String purpose;

    private String governingLaw;
    private String jurisdiction;
    private String specialConditions;
    private List<String> requiredDocuments;
    private boolean reductionAllowed;
    private boolean extensionAllowed;
}

