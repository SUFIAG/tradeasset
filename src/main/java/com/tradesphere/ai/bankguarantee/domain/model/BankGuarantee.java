package com.tradesphere.ai.bankguarantee.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bank Guarantee Aggregate Root
 * Manages bank guarantee lifecycle and operations
 * 
 * Types of Bank Guarantees:
 * - Bid Bond/Tender Guarantee
 * - Performance Guarantee
 * - Advance Payment Guarantee
 * - Retention Money Guarantee
 * - Payment Guarantee
 * - Financial Guarantee
 */
@Getter
@Setter
public class BankGuarantee extends AggregateRoot<BankGuaranteeId> {

    // Basic Information
    private UUID tenantId;
    private String guaranteeNumber;
    private String bgNumber;  // Bank Guarantee number
    private String referenceNumber;
    private BankGuaranteeType type;
    private BGType bgType;  // Simplified BG type
    private BankGuaranteeStatus status;
    
    // Parties
    private UUID applicantId;         // Customer requesting guarantee
    private String applicantName;
    private UUID beneficiaryId;       // Party receiving guarantee
    private String beneficiaryName;
    private String beneficiaryBankName;
    private String beneficiaryBankSwift;
    
    // Issuing Bank
    private String issuingBank;
    private String issuingBankName;
    private String issuingBankBranch;
    private String issuingBankSwift;
    
    // Advising Bank
    private String advisingBank;

    // Financial Details
    private BigDecimal guaranteeAmount;
    private BigDecimal amount;
    private String currency;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    
    // Dates
    private LocalDateTime applicationDate;
    private LocalDate issueDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private LocalDate claimByDate;
    private LocalDateTime claimExpiryDate;

    // Guarantee Details
    private String underlyingContract;
    private String underlyingContractNumber;
    private LocalDateTime underlyingContractDate;
    private String underlyingContractValue;
    private String purpose;
    private String terms;
    private String governingLaw;
    private String jurisdiction;
    private String claimProcedure;
    private String specialConditions;
    private boolean autoExtension;
    private int extensionDays;
    private boolean reductionAllowed;
    private boolean extensionAllowed;
    private String reductionTerms;
    private String extensionTerms;
    private List<String> requiredDocuments;

    // Claims
    private List<GuaranteeClaim> claims;
    private boolean claimed;
    private int claimsCount;
    private LocalDateTime claimedAt;
    private BigDecimal claimedAmount;
    private BigDecimal paidAmount;

    // Document References
    private List<UUID> attachedDocuments;
    
    // Workflow
    private UUID workflowId;
    private String approvalStatus;
    private LocalDateTime approvedAt;
    private UUID approvedBy;
    private LocalDateTime issuedAt;
    private UUID issuedBy;

    // Audit
    private UUID lastModifiedBy;
    private LocalDateTime lastModifiedAt;
    
    // Package-private constructor for mapper
    BankGuarantee(BankGuaranteeId id) {
        super(id);
        this.claims = new ArrayList<>();
        this.attachedDocuments = new ArrayList<>();
        this.requiredDocuments = new ArrayList<>();
    }

    // No-arg constructor for JPA/Mapper (public for reflection-based mapper)
    public BankGuarantee() {
        super(null);
        this.claims = new ArrayList<>();
        this.attachedDocuments = new ArrayList<>();
        this.requiredDocuments = new ArrayList<>();
    }

    /**
     * Create new bank guarantee
     */
    public static BankGuarantee create(
            UUID tenantId,
            BankGuaranteeType type,
            UUID applicantId,
            String applicantName,
            String beneficiaryName,
            BigDecimal guaranteeAmount,
            String currency,
            LocalDate expiryDate,
            String purpose,
            UUID createdBy) {
        
        if (tenantId == null) throw new IllegalArgumentException("Tenant ID required");
        if (type == null) throw new IllegalArgumentException("Guarantee type required");
        if (guaranteeAmount == null || guaranteeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid guarantee amount required");
        }
        
        BankGuarantee guarantee = new BankGuarantee(BankGuaranteeId.generate());
        guarantee.tenantId = tenantId;
        guarantee.guaranteeNumber = generateGuaranteeNumber(type);
        guarantee.type = type;
        guarantee.status = BankGuaranteeStatus.DRAFT;
        guarantee.applicantId = applicantId;
        guarantee.applicantName = applicantName;
        guarantee.beneficiaryName = beneficiaryName;
        guarantee.guaranteeAmount = guaranteeAmount;
        guarantee.currency = currency;
        guarantee.expiryDate = expiryDate;
        guarantee.purpose = purpose;
        guarantee.setCreatedBy(createdBy);
        guarantee.setCreatedAt(java.time.Instant.now());
        guarantee.approvalStatus = "PENDING";
        
        return guarantee;
    }
    
    /**
     * Submit for approval
     */
    public void submit() {
        if (status != BankGuaranteeStatus.DRAFT) {
            throw new IllegalStateException("Can only submit draft guarantees");
        }
        this.status = BankGuaranteeStatus.PENDING_APPROVAL;
        this.approvalStatus = "SUBMITTED";
    }
    
    /**
     * Approve guarantee
     */
    public void approve(UUID approvedBy) {
        if (status != BankGuaranteeStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only approve pending guarantees");
        }
        this.status = BankGuaranteeStatus.APPROVED;
        this.approvalStatus = "APPROVED";
        this.lastModifiedBy = approvedBy;
        this.lastModifiedAt = LocalDateTime.now();
    }
    
    /**
     * Issue guarantee
     */
    public void issue(LocalDate issueDate, String issuingBankName) {
        if (status != BankGuaranteeStatus.APPROVED) {
            throw new IllegalStateException("Can only issue approved guarantees");
        }
        this.status = BankGuaranteeStatus.ACTIVE;
        this.issueDate = issueDate;
        this.effectiveDate = issueDate;
        this.issuingBankName = issuingBankName;
    }
    
    /**
     * Register a claim against the guarantee
     */
    public void registerClaim(BigDecimal claimAmount, String claimReason, UUID claimedBy) {
        if (status != BankGuaranteeStatus.ACTIVE) {
            throw new IllegalStateException("Can only claim active guarantees");
        }
        if (LocalDate.now().isAfter(expiryDate)) {
            throw new IllegalStateException("Guarantee has expired");
        }
        
        GuaranteeClaim claim = new GuaranteeClaim(
            UUID.randomUUID(),
            claimAmount,
            claimReason,
            LocalDateTime.now(),
            claimedBy,
            "PENDING"
        );
        
        this.claims.add(claim);
        this.claimed = true;
        this.claimedAt = LocalDateTime.now();
        this.status = BankGuaranteeStatus.CLAIMED;
    }
    
    /**
     * Cancel guarantee
     */
    public void cancel(String reason, UUID cancelledBy) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel terminated guarantee");
        }
        this.status = BankGuaranteeStatus.CANCELLED;
        this.lastModifiedBy = cancelledBy;
        this.lastModifiedAt = LocalDateTime.now();
    }
    
    /**
     * Expire guarantee
     */
    public void expire() {
        if (LocalDate.now().isAfter(expiryDate) && status == BankGuaranteeStatus.ACTIVE) {
            this.status = BankGuaranteeStatus.EXPIRED;
        }
    }
    
    /**
     * Check if guarantee is expired
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
    
    /**
     * Calculate remaining days
     */
    public long getRemainingDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
    
    /**
     * Update guarantee details
     */
    public void updateDetails(String purpose, String terms, String governingLaw,
                            String jurisdiction, String claimProcedure, String specialConditions) {
        this.purpose = purpose;
        this.terms = terms;
        this.governingLaw = governingLaw;
        this.jurisdiction = jurisdiction;
        this.claimProcedure = claimProcedure;
        this.specialConditions = specialConditions;
    }

    /**
     * Add required document
     */
    public void addRequiredDocument(String document) {
        if (this.requiredDocuments == null) {
            this.requiredDocuments = new ArrayList<>();
        }
        this.requiredDocuments.add(document);
    }

    /**
     * Reject guarantee
     */
    public void reject(String reason, String rejectedBy) {
        if (status != BankGuaranteeStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Can only reject pending guarantees");
        }
        this.status = BankGuaranteeStatus.REJECTED;
        this.approvalStatus = "REJECTED: " + reason;
    }

    /**
     * Request reduction
     */
    public void requestReduction(BigDecimal newAmount, String reason) {
        if (!reductionAllowed) {
            throw new IllegalStateException("Reduction not allowed for this guarantee");
        }
        if (newAmount.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("New amount must be less than current amount");
        }
        // Implementation would create a reduction request
    }

    /**
     * Request extension
     */
    public void requestExtension(LocalDateTime newExpiryDate, String reason) {
        if (!extensionAllowed) {
            throw new IllegalStateException("Extension not allowed for this guarantee");
        }
        // Implementation would create an extension request
    }

    /**
     * Process claim
     */
    public void processClaim(BigDecimal approvedAmount) {
        if (status != BankGuaranteeStatus.CLAIMED) {
            throw new IllegalStateException("No active claim to process");
        }
        this.paidAmount = (this.paidAmount != null) ? this.paidAmount.add(approvedAmount) : approvedAmount;
        this.status = BankGuaranteeStatus.SETTLED;
    }

    /**
     * Release guarantee
     */
    public void release(String reason) {
        if (status != BankGuaranteeStatus.ACTIVE && status != BankGuaranteeStatus.CLAIMED) {
            throw new IllegalStateException("Can only release active or claimed guarantees");
        }
        this.status = BankGuaranteeStatus.RELEASED;
    }

    /**
     * Calculate available amount
     */
    public BigDecimal getAvailableAmount() {
        if (amount == null) return BigDecimal.ZERO;
        if (paidAmount == null) return amount;
        return amount.subtract(paidAmount);
    }

    private static String generateGuaranteeNumber(BankGuaranteeType type) {
        String prefix = switch (type) {
            case BID_BOND -> "BB";
            case PERFORMANCE -> "PG";
            case ADVANCE_PAYMENT -> "APG";
            case RETENTION -> "RG";
            case PAYMENT -> "PAY";
            case FINANCIAL -> "FG";
        };
        return prefix + "-" + System.currentTimeMillis();
    }
    
    /**
     * Guarantee Claim nested entity
     */
    @Getter
    @Setter
    public static class GuaranteeClaim {
        private UUID id;
        private BigDecimal claimAmount;
        private String claimReason;
        private LocalDateTime claimDate;
        private UUID claimedBy;
        private String status;
        private LocalDateTime processedDate;
        
        public GuaranteeClaim(UUID id, BigDecimal claimAmount, String claimReason, 
                             LocalDateTime claimDate, UUID claimedBy, String status) {
            this.id = id;
            this.claimAmount = claimAmount;
            this.claimReason = claimReason;
            this.claimDate = claimDate;
            this.claimedBy = claimedBy;
            this.status = status;
        }
    }
}

