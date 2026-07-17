package com.tradesphere.ai.scf.adapter.out.persistence;

import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for Supply Chain Finance
 */
@Entity
@Table(name = "scf_facilities", schema = "scf")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SCFJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "scf_reference", nullable = false)
    private String scfReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "scf_type", nullable = false)
    private SCFType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SCFStatus status;

    // Parties
    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "buyer_id")
    private UUID buyerId;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "financier_id")
    private UUID financierId;

    @Column(name = "financier_name")
    private String financierName;

    // Financial Details
    @Column(name = "invoice_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal invoiceAmount;

    @Column(name = "finance_amount", precision = 20, scale = 2)
    private BigDecimal financeAmount;

    @Column(name = "advance_rate", precision = 5, scale = 2)
    private BigDecimal advanceRate;

    @Column(nullable = false, length = 3)
    private String currency;

    // Rates and Fees
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "fee_amount", precision = 20, scale = 2)
    private BigDecimal feeAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    // Invoice/PO Details
    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "payment_term_days")
    private Integer paymentTermDays;

    // Financing Details
    @Column(name = "finance_date")
    private LocalDate financeDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "early_payment_date")
    private LocalDate earlyPaymentDate;

    @Column(name = "early_payment_discount", precision = 5, scale = 2)
    private BigDecimal earlyPaymentDiscount;

    // Status Tracking
    @Column(name = "approved")
    private boolean approved;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "disbursed")
    private boolean disbursed;

    @Column(name = "disbursed_at")
    private LocalDateTime disbursedAt;

    @Column(name = "repaid")
    private boolean repaid;

    @Column(name = "repaid_at")
    private LocalDateTime repaidAt;

    // Goods Information
    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription;

    @Column(name = "shipment_reference")
    private String shipmentReference;

    // Risk Assessment
    @Column(name = "credit_score")
    private String creditScore;

    @Column(name = "risk_rating")
    private String riskRating;

    @Column(name = "credit_limit", precision = 20, scale = 2)
    private BigDecimal creditLimit;

    // Workflow & Audit
    @Column(name = "workflow_id")
    private UUID workflowId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Version
    private int version;
}

