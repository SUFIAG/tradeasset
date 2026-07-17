package com.tradesphere.ai.scf.adapter.out.financing;

import com.tradesphere.ai.scf.application.port.out.ExternalFinancingPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Mock implementation of external financing platform integration
 * In production, this would integrate with real platforms like:
 * - Trade Finance platforms (e.g., Marco Polo, Contour, TradeIX)
 * - Supply Chain Finance platforms (e.g., Taulia, C2FO, PrimeRevenue)
 * - Bank APIs for factoring services
 */
@Component
@Slf4j
public class MockExternalFinancingAdapter implements ExternalFinancingPlatform {

    private static final String PLATFORM_NAME = "MockFinancingPlatform";

    @Override
    public FinancingResponse submitFinancingRequest(FinancingRequest request) {
        log.info("Submitting financing request to {}: scfId={}, amount={}",
            PLATFORM_NAME, request.getScfId(), request.getRequestedAmount());

        // Simulate external platform processing
        String externalRef = "EXT-" + UUID.randomUUID().toString().substring(0, 8);

        // Mock approval logic based on amount and risk
        boolean approved = request.getRequestedAmount().compareTo(new BigDecimal("10000000")) < 0
            && !isHighRisk(request.getRiskRating());

        BigDecimal discountRate = calculateDiscountRate(request.getRiskRating());
        BigDecimal feeAmount = request.getRequestedAmount()
            .multiply(discountRate)
            .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        log.info("External platform response: reference={}, approved={}, rate={}",
            externalRef, approved, discountRate);

        return FinancingResponse.builder()
            .externalReference(externalRef)
            .approved(approved)
            .approvedAmount(approved ? request.getRequestedAmount() : BigDecimal.ZERO)
            .discountRate(discountRate)
            .feeAmount(feeAmount)
            .platformName(PLATFORM_NAME)
            .statusMessage(approved ? "Approved by platform" : "Rejected - exceeds limits")
            .build();
    }

    @Override
    public FinancingStatus checkStatus(String externalReference) {
        log.info("Checking status for external reference: {}", externalReference);

        return FinancingStatus.builder()
            .externalReference(externalReference)
            .status("APPROVED")
            .currentAmount(new BigDecimal("100000.00"))
            .statusDate(LocalDate.now())
            .build();
    }

    @Override
    public DisbursementConfirmation confirmDisbursement(String externalReference) {
        log.info("Confirming disbursement for: {}", externalReference);

        return DisbursementConfirmation.builder()
            .externalReference(externalReference)
            .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8))
            .disbursedAmount(new BigDecimal("100000.00"))
            .disbursementDate(LocalDate.now())
            .successful(true)
            .build();
    }

    @Override
    public RepaymentConfirmation processRepayment(RepaymentRequest request) {
        log.info("Processing repayment for: {}, amount={}",
            request.getExternalReference(), request.getAmount());

        return RepaymentConfirmation.builder()
            .transactionId("RPAY-" + UUID.randomUUID().toString().substring(0, 8))
            .repaidAmount(request.getAmount())
            .repaymentDate(request.getRepaymentDate())
            .successful(true)
            .build();
    }

    @Override
    public FinancingOffer getFinancingOffer(OfferRequest request) {
        log.info("Getting financing offer for amount: {}", request.getInvoiceAmount());

        BigDecimal discountRate = calculateDiscountRate(request.getRiskRating());
        BigDecimal advanceRate = calculateAdvanceRate(request.getRiskRating());
        BigDecimal maxFinancing = request.getInvoiceAmount()
            .multiply(advanceRate)
            .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        return FinancingOffer.builder()
            .maxFinancingAmount(maxFinancing)
            .discountRate(discountRate)
            .advanceRate(advanceRate)
            .feeAmount(maxFinancing.multiply(discountRate).divide(new BigDecimal("100")))
            .validityDays(30)
            .build();
    }

    private boolean isHighRisk(String riskRating) {
        if (riskRating == null) return false;
        return riskRating.matches("^(C|CC|CCC|D).*");
    }

    private BigDecimal calculateDiscountRate(String riskRating) {
        if (riskRating == null) return new BigDecimal("8.00");

        return switch (riskRating.toUpperCase()) {
            case "AAA", "AA" -> new BigDecimal("5.50");
            case "A" -> new BigDecimal("6.50");
            case "BBB" -> new BigDecimal("7.50");
            case "BB" -> new BigDecimal("9.00");
            case "B" -> new BigDecimal("11.00");
            default -> new BigDecimal("14.00");
        };
    }

    private BigDecimal calculateAdvanceRate(String riskRating) {
        if (riskRating == null) return new BigDecimal("80.00");

        return switch (riskRating.toUpperCase()) {
            case "AAA", "AA" -> new BigDecimal("95.00");
            case "A" -> new BigDecimal("90.00");
            case "BBB" -> new BigDecimal("85.00");
            case "BB" -> new BigDecimal("80.00");
            default -> new BigDecimal("75.00");
        };
    }
}

