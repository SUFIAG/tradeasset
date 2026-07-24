package com.tradesphere.ai.scf.application.port.out;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Port for integrating with external financing platforms
 */
public interface ExternalFinancingPlatform {

    /**
     * Submit SCF request to external platform
     */
    FinancingResponse submitFinancingRequest(FinancingRequest request);

    /**
     * Check status of external financing
     */
    FinancingStatus checkStatus(String externalReference);

    /**
     * Confirm disbursement from external platform
     */
    DisbursementConfirmation confirmDisbursement(String externalReference);

    /**
     * Process repayment through external platform
     */
    RepaymentConfirmation processRepayment(RepaymentRequest request);

    /**
     * Get available financing offers from platform
     */
    FinancingOffer getFinancingOffer(OfferRequest request);

    @Data
    @Builder
    class FinancingRequest {
        private UUID scfId;
        private String scfReference;
        private String supplierName;
        private String buyerName;
        private BigDecimal invoiceAmount;
        private BigDecimal requestedAmount;
        private String currency;
        private LocalDate dueDate;
        private String invoiceNumber;
        private String riskRating;
    }

    @Data
    @Builder
    class FinancingResponse {
        private String externalReference;
        private boolean approved;
        private BigDecimal approvedAmount;
        private BigDecimal discountRate;
        private BigDecimal feeAmount;
        private String platformName;
        private String statusMessage;
    }

    @Data
    @Builder
    class FinancingStatus {
        private String externalReference;
        private String status; // PENDING, APPROVED, DISBURSED, REPAID, REJECTED
        private BigDecimal currentAmount;
        private LocalDate statusDate;
    }

    @Data
    @Builder
    class DisbursementConfirmation {
        private String externalReference;
        private String transactionId;
        private BigDecimal disbursedAmount;
        private LocalDate disbursementDate;
        private boolean successful;
    }

    @Data
    @Builder
    class RepaymentRequest {
        private String externalReference;
        private BigDecimal amount;
        private LocalDate repaymentDate;
        private String paymentMethod;
    }

    @Data
    @Builder
    class RepaymentConfirmation {
        private String transactionId;
        private BigDecimal repaidAmount;
        private LocalDate repaymentDate;
        private boolean successful;
    }

    @Data
    @Builder
    class OfferRequest {
        private BigDecimal invoiceAmount;
        private String currency;
        private LocalDate dueDate;
        private String riskRating;
        private String creditScore;
    }

    @Data
    @Builder
    class FinancingOffer {
        private BigDecimal maxFinancingAmount;
        private BigDecimal discountRate;
        private BigDecimal advanceRate;
        private BigDecimal feeAmount;
        private int validityDays;
    }
}

