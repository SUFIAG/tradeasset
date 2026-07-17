package com.tradesphere.ai.scf.domain.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Automated Discount Calculation Rules Engine
 * Calculates optimal discount rates based on various business rules
 */
@Service
@Slf4j
public class DiscountCalculationEngine {

    private static final BigDecimal BASE_DISCOUNT_RATE = new BigDecimal("8.00"); // 8% annual
    private static final BigDecimal MAX_DISCOUNT_RATE = new BigDecimal("18.00"); // 18% annual
    private static final BigDecimal MIN_DISCOUNT_RATE = new BigDecimal("3.00"); // 3% annual

    /**
     * Calculate discount rate based on multiple factors
     */
    public DiscountRateResult calculateDiscountRate(DiscountRateRequest request) {
        log.info("Calculating discount rate for: supplier={}, amount={}",
            request.getSupplierName(), request.getInvoiceAmount());

        BigDecimal baseRate = BASE_DISCOUNT_RATE;

        // Apply business rules
        baseRate = applyRiskAdjustment(baseRate, request.getRiskRating());
        baseRate = applyCreditScoreAdjustment(baseRate, request.getCreditScore());
        baseRate = applyPaymentTermAdjustment(baseRate, request.getPaymentTermDays());
        baseRate = applyInvoiceAmountAdjustment(baseRate, request.getInvoiceAmount());
        baseRate = applyRelationshipAdjustment(baseRate, request.getRelationshipDuration());
        baseRate = applyMarketConditionsAdjustment(baseRate, request.getMarketRate());

        // Ensure within bounds
        if (baseRate.compareTo(MAX_DISCOUNT_RATE) > 0) {
            baseRate = MAX_DISCOUNT_RATE;
        }
        if (baseRate.compareTo(MIN_DISCOUNT_RATE) < 0) {
            baseRate = MIN_DISCOUNT_RATE;
        }

        // Calculate early payment discount if applicable
        BigDecimal earlyPaymentDiscount = calculateEarlyPaymentDiscount(
            request.getDueDate(),
            request.getEarlyPaymentDays(),
            baseRate
        );

        // Calculate fees
        BigDecimal feeAmount = calculateFeeAmount(
            request.getFinanceAmount(),
            baseRate,
            request.getPaymentTermDays()
        );

        DiscountRateResult result = new DiscountRateResult();
        result.setDiscountRate(baseRate.setScale(2, RoundingMode.HALF_UP));
        result.setEarlyPaymentDiscount(earlyPaymentDiscount.setScale(2, RoundingMode.HALF_UP));
        result.setFeeAmount(feeAmount.setScale(2, RoundingMode.HALF_UP));
        result.setEffectiveAnnualRate(baseRate.setScale(2, RoundingMode.HALF_UP));
        result.setRecommendedAdvanceRate(calculateAdvanceRate(request.getRiskRating()));

        log.info("Calculated discount rate: {}%, fee: {}", result.getDiscountRate(), result.getFeeAmount());
        return result;
    }

    /**
     * Risk-based adjustment: Higher risk = higher rate
     */
    private BigDecimal applyRiskAdjustment(BigDecimal baseRate, String riskRating) {
        if (riskRating == null) return baseRate;

        return switch (riskRating.toUpperCase()) {
            case "AAA", "AA" -> baseRate.subtract(new BigDecimal("1.5")); // -1.5%
            case "A" -> baseRate.subtract(new BigDecimal("0.5")); // -0.5%
            case "BBB" -> baseRate; // No change
            case "BB" -> baseRate.add(new BigDecimal("1.0")); // +1.0%
            case "B" -> baseRate.add(new BigDecimal("2.0")); // +2.0%
            case "CCC", "CC", "C" -> baseRate.add(new BigDecimal("4.0")); // +4.0%
            default -> baseRate;
        };
    }

    /**
     * Credit score adjustment: Better score = lower rate
     */
    private BigDecimal applyCreditScoreAdjustment(BigDecimal baseRate, String creditScore) {
        if (creditScore == null) return baseRate;

        try {
            int score = Integer.parseInt(creditScore);
            if (score >= 750) return baseRate.subtract(new BigDecimal("1.0")); // Excellent
            if (score >= 700) return baseRate.subtract(new BigDecimal("0.5")); // Good
            if (score >= 650) return baseRate; // Fair
            if (score >= 600) return baseRate.add(new BigDecimal("1.0")); // Poor
            return baseRate.add(new BigDecimal("2.5")); // Very poor
        } catch (NumberFormatException e) {
            return baseRate;
        }
    }

    /**
     * Payment term adjustment: Longer terms = higher rate
     */
    private BigDecimal applyPaymentTermAdjustment(BigDecimal baseRate, Integer paymentTermDays) {
        if (paymentTermDays == null) return baseRate;

        if (paymentTermDays <= 30) return baseRate.subtract(new BigDecimal("0.5"));
        if (paymentTermDays <= 60) return baseRate;
        if (paymentTermDays <= 90) return baseRate.add(new BigDecimal("0.5"));
        return baseRate.add(new BigDecimal("1.0"));
    }

    /**
     * Invoice amount adjustment: Larger amounts = lower rate (volume discount)
     */
    private BigDecimal applyInvoiceAmountAdjustment(BigDecimal baseRate, BigDecimal invoiceAmount) {
        if (invoiceAmount == null) return baseRate;

        if (invoiceAmount.compareTo(new BigDecimal("1000000")) >= 0) {
            return baseRate.subtract(new BigDecimal("1.0")); // >$1M
        }
        if (invoiceAmount.compareTo(new BigDecimal("500000")) >= 0) {
            return baseRate.subtract(new BigDecimal("0.5")); // >$500K
        }
        if (invoiceAmount.compareTo(new BigDecimal("100000")) >= 0) {
            return baseRate; // >$100K
        }
        return baseRate.add(new BigDecimal("0.5")); // <$100K (small invoice premium)
    }

    /**
     * Relationship duration adjustment: Longer relationship = lower rate
     */
    private BigDecimal applyRelationshipAdjustment(BigDecimal baseRate, Integer monthsDuration) {
        if (monthsDuration == null) return baseRate;

        if (monthsDuration >= 36) return baseRate.subtract(new BigDecimal("0.75")); // 3+ years
        if (monthsDuration >= 24) return baseRate.subtract(new BigDecimal("0.5")); // 2+ years
        if (monthsDuration >= 12) return baseRate.subtract(new BigDecimal("0.25")); // 1+ year
        return baseRate; // New customer
    }

    /**
     * Market conditions adjustment
     */
    private BigDecimal applyMarketConditionsAdjustment(BigDecimal baseRate, BigDecimal marketRate) {
        if (marketRate == null) return baseRate;

        // Adjust towards market rate (weighted average: 70% our rate, 30% market)
        BigDecimal weightedRate = baseRate.multiply(new BigDecimal("0.70"))
            .add(marketRate.multiply(new BigDecimal("0.30")));
        return weightedRate;
    }

    /**
     * Calculate early payment discount
     */
    private BigDecimal calculateEarlyPaymentDiscount(LocalDate dueDate, Integer earlyDays, BigDecimal annualRate) {
        if (dueDate == null || earlyDays == null || earlyDays <= 0) {
            return BigDecimal.ZERO;
        }

        // Calculate discount for early payment (pro-rated from annual rate)
        BigDecimal dailyRate = annualRate.divide(new BigDecimal("365"), 6, RoundingMode.HALF_UP);
        return dailyRate.multiply(new BigDecimal(earlyDays));
    }

    /**
     * Calculate fee amount based on financing amount and rate
     */
    private BigDecimal calculateFeeAmount(BigDecimal financeAmount, BigDecimal annualRate, Integer days) {
        if (financeAmount == null || days == null) return BigDecimal.ZERO;

        // Fee = Principal * Rate * (Days/365)
        BigDecimal dailyRate = annualRate.divide(new BigDecimal("36500"), 8, RoundingMode.HALF_UP);
        return financeAmount.multiply(dailyRate).multiply(new BigDecimal(days));
    }

    /**
     * Calculate recommended advance rate based on risk
     */
    private BigDecimal calculateAdvanceRate(String riskRating) {
        if (riskRating == null) return new BigDecimal("80.00");

        return switch (riskRating.toUpperCase()) {
            case "AAA", "AA" -> new BigDecimal("95.00");
            case "A" -> new BigDecimal("90.00");
            case "BBB" -> new BigDecimal("85.00");
            case "BB" -> new BigDecimal("80.00");
            case "B" -> new BigDecimal("75.00");
            case "CCC", "CC", "C" -> new BigDecimal("70.00");
            default -> new BigDecimal("80.00");
        };
    }

    /**
     * Request for discount rate calculation
     */
    @Data
    public static class DiscountRateRequest {
        private String supplierName;
        private BigDecimal invoiceAmount;
        private BigDecimal financeAmount;
        private String riskRating;
        private String creditScore;
        private Integer paymentTermDays;
        private LocalDate dueDate;
        private Integer earlyPaymentDays;
        private Integer relationshipDuration; // months
        private BigDecimal marketRate;
    }

    /**
     * Result of discount rate calculation
     */
    @Data
    public static class DiscountRateResult {
        private BigDecimal discountRate;
        private BigDecimal earlyPaymentDiscount;
        private BigDecimal feeAmount;
        private BigDecimal effectiveAnnualRate;
        private BigDecimal recommendedAdvanceRate;
    }
}

