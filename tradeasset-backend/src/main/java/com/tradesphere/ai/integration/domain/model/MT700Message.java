package com.tradesphere.ai.integration.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MT700 SWIFT Message - Letter of Credit Issuance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MT700Message {
    private String messageId;
    private String lcNumber;
    private String applicant;
    private String beneficiary;
    private String amount;
    private String currency;
    private String expiryDate;
    private String description;
}

