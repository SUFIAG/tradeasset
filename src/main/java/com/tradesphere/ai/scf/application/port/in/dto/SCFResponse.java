package com.tradesphere.ai.scf.application.port.in.dto;

import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for SCF
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SCFResponse {

    private UUID id;
    private UUID tenantId;
    private String scfReference;
    private SCFType type;
    private SCFStatus status;

    // Parties
    private UUID supplierId;
    private String supplierName;
    private UUID buyerId;
    private String buyerName;
    private UUID financierId;
    private String financierName;

    // Financial Details
    private BigDecimal invoiceAmount;
    private BigDecimal financeAmount;
    private BigDecimal advanceRate;
    private String currency;

    // Rates and Fees
    private BigDecimal discountRate;
    private BigDecimal feeAmount;
    private BigDecimal interestRate;
    private BigDecimal netAmount;

    // Invoice/PO Details
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Integer paymentTermDays;

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

    // Audit
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}

