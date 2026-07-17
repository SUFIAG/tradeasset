package com.tradesphere.ai.scf.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Supply Chain Finance Aggregate Root
 * Manages supply chain financing programs including invoice financing,
 * reverse factoring, and payables finance
 *
 * SCF Programs:
 * - Invoice Financing/Factoring
 * - Reverse Factoring/Supplier Finance
 * - Purchase Order Financing
 * - Inventory Financing
 * - Receivables Discounting
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class SupplyChainFinance extends AggregateRoot<SCFId> {

    // Basic Information
    private UUID tenantId;
    private String scfReference;
    private SCFType type;
    private SCFStatus status;

    // Parties
    private UUID supplierId;
    private String supplierName;
    private UUID buyerId;
    private String buyerName;
    private UUID financierId;  // Bank or financial institution
    private String financierName;

    // Financial Details
    private BigDecimal invoiceAmount;
    private BigDecimal financeAmount;  // Amount to be financed
    private BigDecimal advanceRate;    // % of invoice value
    private String currency;

    // Rates and Fees
    private BigDecimal discountRate;   // Annual discount rate %
    private BigDecimal feeAmount;
    private BigDecimal interestRate;

    // Invoice/PO Details
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private int paymentTermDays;

    // Financing Details
    private LocalDate financeDate;
    private LocalDate maturityDate;
    private LocalDate earlyPaymentDate;
    private BigDecimal earlyPaymentDiscount;

    // Status Tracking
    private boolean approved;
    private LocalDateTime approvedAt;
    private boolean disbursed;
    private LocalDateTime disbursedAt;
    private boolean repaid;
    private LocalDateTime repaidAt;

    // Goods Information
    private String goodsDescription;
    private String shipmentReference;

    // Risk Assessment
    private String creditScore;
    private String riskRating;
    private BigDecimal creditLimit;

    // Workflow & Audit
    private UUID workflowId;

    private SupplyChainFinance(SCFId id) {
        super(id);
    }

    /**
     * Create new SCF transaction
     */
    public static SupplyChainFinance create(
            UUID tenantId,
            SCFType type,
            UUID supplierId,
            String supplierName,
            UUID buyerId,
            String buyerName,
            BigDecimal invoiceAmount,
            BigDecimal advanceRate,
            String currency,
            LocalDate dueDate,
            UUID createdBy) {

        if (tenantId == null) throw new IllegalArgumentException("Tenant ID required");
        if (invoiceAmount == null || invoiceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid invoice amount required");
        }

        SupplyChainFinance scf = new SupplyChainFinance(SCFId.generate());
        scf.tenantId = tenantId;
        scf.scfReference = generateReference(type);
        scf.type = type;
        scf.status = SCFStatus.PENDING;
        scf.supplierId = supplierId;
        scf.supplierName = supplierName;
        scf.buyerId = buyerId;
        scf.buyerName = buyerName;
        scf.invoiceAmount = invoiceAmount;
        scf.advanceRate = advanceRate;
        scf.financeAmount = invoiceAmount.multiply(advanceRate).divide(new BigDecimal("100"));
        scf.currency = currency;
        scf.dueDate = dueDate;
        scf.setCreatedBy(createdBy);
        scf.setCreatedAt(java.time.Instant.now());

        return scf;
    }

    /**
     * Approve financing
     */
    public void approve(BigDecimal discountRate, UUID approvedBy) {
        if (status != SCFStatus.PENDING) {
            throw new IllegalStateException("Can only approve pending requests");
        }
        this.status = SCFStatus.APPROVED;
        this.discountRate = discountRate;
        this.approved = true;
        this.approvedAt = LocalDateTime.now();

        // Calculate fees and interest
        calculateFinancingCosts();
    }

    /**
     * Disburse funds to supplier
     */
    public void disburse() {
        if (!approved) {
            throw new IllegalStateException("Must be approved before disbursement");
        }
        this.status = SCFStatus.FUNDED;
        this.disbursed = true;
        this.disbursedAt = LocalDateTime.now();
        this.financeDate = LocalDate.now();
    }

    /**
     * Process repayment
     */
    public void repay(BigDecimal amount, LocalDate repaymentDate) {
        if (!disbursed) {
            throw new IllegalStateException("Must be disbursed before repayment");
        }
        this.status = SCFStatus.REPAID;
        this.repaid = true;
        this.repaidAt = LocalDateTime.now();
        this.maturityDate = repaymentDate;
    }

    /**
     * Offer early payment discount
     */
    public void offerEarlyPayment(BigDecimal discountPercent, LocalDate paymentDate) {
        if (type != SCFType.REVERSE_FACTORING) {
            throw new IllegalStateException("Early payment only for reverse factoring");
        }
        this.earlyPaymentDiscount = discountPercent;
        this.earlyPaymentDate = paymentDate;
    }

    /**
     * Calculate financing costs (discount and fees)
     */
    private void calculateFinancingCosts() {
        if (discountRate == null) return;

        // Calculate days between finance date and due date
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(
            LocalDate.now(),
            dueDate
        );

        // Calculate discount amount: Principal * Rate * (Days/365)
        BigDecimal dailyRate = discountRate.divide(new BigDecimal("36500"), 6, java.math.RoundingMode.HALF_UP);
        this.feeAmount = financeAmount
            .multiply(dailyRate)
            .multiply(new BigDecimal(days))
            .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Calculate net amount to supplier (after fees)
     */
    public BigDecimal getNetAmount() {
        if (feeAmount == null) return financeAmount;
        return financeAmount.subtract(feeAmount);
    }

    private static String generateReference(SCFType type) {
        String prefix = switch (type) {
            case INVOICE_FINANCING -> "IF";
            case REVERSE_FACTORING -> "RF";
            case PURCHASE_ORDER_FINANCING -> "POF";
            case INVENTORY_FINANCING -> "INV";
            case RECEIVABLES_DISCOUNTING -> "RD";
        };
        return prefix + "-" + System.currentTimeMillis();
    }
}

