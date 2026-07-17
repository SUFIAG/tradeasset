package com.tradesphere.ai.scf.application.port.in.dto;

import com.tradesphere.ai.scf.domain.model.SCFType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to create a new SCF transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSCFCommand {

    private UUID tenantId;
    private SCFType type;

    // Parties
    private UUID supplierId;
    private String supplierName;
    private UUID buyerId;
    private String buyerName;

    // Financial Details
    private BigDecimal invoiceAmount;
    private BigDecimal advanceRate;
    private String currency;

    // Invoice/PO Details
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Integer paymentTermDays;

    // Goods Information
    private String goodsDescription;
    private String shipmentReference;

    private UUID createdBy;
}

