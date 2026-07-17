package com.tradesphere.ai.integration.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MT760 SWIFT Message - Bank Guarantee Issuance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MT760Message {
    private String messageId;
    private String guaranteeNumber;
    private String applicant;
    private String beneficiary;
    private String amount;
    private String currency;
    private String expiryDate;
    private String terms;
}

