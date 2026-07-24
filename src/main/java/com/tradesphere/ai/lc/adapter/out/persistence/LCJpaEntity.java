package com.tradesphere.ai.lc.adapter.out.persistence;

import com.tradesphere.ai.lc.domain.model.Incoterm;
import com.tradesphere.ai.lc.domain.model.LCStatus;
import com.tradesphere.ai.lc.domain.model.LCType;
import com.tradesphere.ai.shared.multitenancy.TenantEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for Letter of Credit persistence
 */
@Entity
@Table(name = "letters_of_credit")
@EntityListeners(TenantEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LCJpaEntity {

    @Id
    private UUID id;

    @Column(name = "lc_number", unique = true, length = 20)
    private String lcNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "lc_type", nullable = false, length = 30)
    private LCType lcType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LCStatus status;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "applicant_name", nullable = false, length = 500)
    private String applicantName;

    @Column(name = "applicant_address", columnDefinition = "TEXT")
    private String applicantAddress;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "beneficiary_name", nullable = false, length = 500)
    private String beneficiaryName;

    @Column(name = "beneficiary_address", columnDefinition = "TEXT")
    private String beneficiaryAddress;

    @Column(name = "issuing_bank_id")
    private UUID issuingBankId;

    @Column(name = "advising_bank_id")
    private UUID advisingBankId;

    @Column(name = "confirming_bank_id")
    private UUID confirmingBankId;

    @Column(name = "lc_amount_value", nullable = false, precision = 20, scale = 2)
    private BigDecimal lcAmountValue;

    @Column(name = "lc_amount_currency", nullable = false, length = 3)
    private String lcAmountCurrency;

    @Column(precision = 5, scale = 2)
    private BigDecimal tolerance;

    @Column(name = "available_amount_value", precision = 20, scale = 2)
    private BigDecimal availableAmountValue;

    @Column(name = "available_amount_currency", length = 3)
    private String availableAmountCurrency;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "last_shipment_date")
    private LocalDate lastShipmentDate;

    @Column(name = "presentation_date")
    private LocalDate presentationDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Incoterm incoterm;

    @Column(name = "port_of_loading", length = 200)
    private String portOfLoading;

    @Column(name = "port_of_discharge", length = 200)
    private String portOfDischarge;

    @Column(name = "place_of_destination", length = 200)
    private String placeOfDestination;

    @Column(name = "partial_shipment_allowed")
    private Boolean partialShipmentAllowed;

    @Column(name = "transshipment_allowed")
    private Boolean transshipmentAllowed;

    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription;

    @Column(name = "hs_code", length = 20)
    private String hsCode;

    @Column(precision = 20, scale = 2)
    private BigDecimal quantity;

    @Column(name = "quantity_unit", length = 50)
    private String quantityUnit;

    @Column(name = "credit_limit_reserved_value", precision = 20, scale = 2)
    private BigDecimal creditLimitReservedValue;

    @Column(name = "credit_limit_reserved_currency", length = 3)
    private String creditLimitReservedCurrency;

    @Column(name = "credit_limit_approved")
    private Boolean creditLimitApproved;

    @Column(name = "documents_required", columnDefinition = "TEXT")
    private String documentsRequired;

    @Column(name = "commercial_invoice_required")
    private Boolean commercialInvoiceRequired;

    @Column(name = "bill_of_lading_required")
    private Boolean billOfLadingRequired;

    @Column(name = "packing_list_required")
    private Boolean packingListRequired;

    @Column(name = "certificate_of_origin_required")
    private Boolean certificateOfOriginRequired;

    @Column(name = "insurance_certificate_required")
    private Boolean insuranceCertificateRequired;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;


    @Column(name = "submitted_by")
    private UUID submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by")
    private UUID rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "swift_message", columnDefinition = "TEXT")
    private String swiftMessage;

    @Column(name = "swift_sent_at")
    private LocalDateTime swiftSentAt;

    @Column(name = "compliance_check_passed")
    private Boolean complianceCheckPassed;

    @Column(name = "sanctions_check_passed")
    private Boolean sanctionsCheckPassed;

    @Column(name = "risk_rating", length = 20)
    private String riskRating;

    @Version
    private Long version;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

