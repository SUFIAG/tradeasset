package com.tradesphere.ai.lc.adapter.in.web.dto;

import com.tradesphere.ai.lc.domain.model.Incoterm;
import com.tradesphere.ai.lc.domain.model.LCType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new LC application
 */
@Data
public class CreateLCRequest {

    @NotBlank(message = "Applicant ID is required")
    private String applicantId;

    @NotBlank(message = "Applicant name is required")
    @Size(max = 500)
    private String applicantName;

    private String applicantAddress;

    @NotBlank(message = "Beneficiary ID is required")
    private String beneficiaryId;

    @NotBlank(message = "Beneficiary name is required")
    @Size(max = 500)
    private String beneficiaryName;

    private String beneficiaryAddress;

    @NotNull(message = "LC amount is required")
    @DecimalMin(value = "0.01", message = "LC amount must be positive")
    private BigDecimal lcAmount;
    
    // Accept 'amount' as an alias for lcAmount from frontend
    public void setAmount(BigDecimal amount) {
        this.lcAmount = amount;
    }

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO code)")
    private String currency;

    @NotNull(message = "LC type is required")
    private LCType lcType;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Future(message = "Last shipment date must be in the future")
    private LocalDate lastShipmentDate;

    private Incoterm incoterm;

    @Size(max = 5000, message = "Goods description too long")
    private String goodsDescription;

    @Size(max = 200)
    private String portOfLoading;

    @Size(max = 200)
    private String portOfDischarge;

    @Size(max = 200)
    private String placeOfDestination;

    private Boolean partialShipmentAllowed = false;

    private Boolean transshipmentAllowed = false;

    @Size(max = 20)
    private String hsCode;

    private String documentsRequired;
}

