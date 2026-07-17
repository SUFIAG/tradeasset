package com.tradesphere.ai.lc.adapter.in.web.dto;

import com.tradesphere.ai.lc.domain.model.Incoterm;
import com.tradesphere.ai.lc.domain.model.LCStatus;
import com.tradesphere.ai.lc.domain.model.LCType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for LC response
 */
@Data
@Builder
public class LCResponse {
    private String id;
    private String lcNumber;
    private LCType lcType;
    private LCStatus status;
    
    private String applicantId;
    private String applicantName;
    
    private String beneficiaryId;
    private String beneficiaryName;
    
    private BigDecimal lcAmount;
    private String currency;
    private BigDecimal availableAmount;
    
    private LocalDate applicationDate;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private LocalDate lastShipmentDate;
    
    private Incoterm incoterm;
    private String portOfLoading;
    private String portOfDischarge;
    private String goodsDescription;
    
    private Boolean partialShipmentAllowed;
    private Boolean transshipmentAllowed;
    
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

