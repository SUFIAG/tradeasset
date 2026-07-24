package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.Incoterm;
import com.tradesphere.ai.lc.domain.model.LCType;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to create a new LC application
 */
@Value
@Builder
public class CreateLCCommand {
    UUID tenantId;
    String applicantId;           // Customer ID (UUID as string)
    String applicantName;
    String applicantAddress;
    String beneficiaryId;         // Customer ID (UUID as string)
    String beneficiaryName;
    String beneficiaryAddress;
    Money lcAmount;
    LCType lcType;
    LocalDate expiryDate;
    LocalDate lastShipmentDate;
    Incoterm incoterm;
    String goodsDescription;
    String portOfLoading;
    String portOfDischarge;
    String placeOfDestination;
    Boolean partialShipmentAllowed;
    Boolean transshipmentAllowed;
    String hsCode;
    String documentsRequired;
    UUID createdBy;
}

