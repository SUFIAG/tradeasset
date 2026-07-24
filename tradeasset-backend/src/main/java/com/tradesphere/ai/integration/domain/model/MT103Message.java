package com.tradesphere.ai.integration.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MT103 SWIFT Message - Customer Credit Transfer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MT103Message {
    private String messageId;
    private String senderReference;
    private String beneficiaryAccount;
    private String beneficiaryName;
    private String amount;
    private String currency;
    private String orderingCustomer;
    private String remittanceInfo;
}

