package com.tradesphere.ai.bankguarantee.adapter.out.persistence;

import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for Bank Guarantee
 */
@Entity
@Table(name = "bank_guarantees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankGuaranteeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "bg_number")
    private String bgNumber;

    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "issuing_bank")
    private String issuingBank;

    @Column(name = "advising_bank")
    private String advisingBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "bg_type", nullable = false)
    private BGType bgType;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "claim_expiry_date")
    private LocalDateTime claimExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BGStatus status;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "governing_law")
    private String governingLaw;

    private String jurisdiction;

    @Column(name = "claim_procedure", columnDefinition = "TEXT")
    private String claimProcedure;

    @Column(name = "underlying_contract_number")
    private String underlyingContractNumber;

    @Column(name = "underlying_contract_date")
    private LocalDate underlyingContractDate;

    @Column(name = "underlying_contract_value", precision = 20, scale = 2)
    private BigDecimal underlyingContractValue;

    @Column(name = "reduction_allowed")
    private boolean reductionAllowed;

    @Column(name = "extension_allowed")
    private boolean extensionAllowed;

    @Column(name = "reduction_terms", columnDefinition = "TEXT")
    private String reductionTerms;

    @Column(name = "extension_terms", columnDefinition = "TEXT")
    private String extensionTerms;

    @Column(name = "claims_count")
    private int claimsCount;

    @Column(name = "claimed_amount", precision = 20, scale = 2)
    private BigDecimal claimedAmount;

    @Column(name = "paid_amount", precision = 20, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "required_documents", columnDefinition = "TEXT[]")
    private String[] requiredDocuments;

    @Column(name = "special_conditions", columnDefinition = "TEXT")
    private String specialConditions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "issued_by")
    private String issuedBy;

    @Version
    private int version;
}

