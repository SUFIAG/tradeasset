package com.tradesphere.ai.shared.domain.valueobject;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;

/**
 * Money value object: an amount plus an ISO 4217 currency (REQ-MON-001).
 *
 * <p>The scale is driven by the currency's minor units (REQ-MON-002): KWD, BHD, JOD and OMR
 * carry 3 decimals, JPY carries 0, most others carry 2. Arithmetic across currencies throws
 * (REQ-MON-003). Values are always {@link BigDecimal} (REQ-MON-004).
 */
@Value
public class Money implements ValueObject, Comparable<Money> {

    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    BigDecimal amount;
    Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        this.currency = currency;
        this.amount = amount.setScale(scaleOf(currency), ROUNDING);
    }

    /** Number of minor units for a currency; currencies without a defined scale default to 2. */
    public static int scaleOf(Currency currency) {
        int digits = currency.getDefaultFractionDigits();
        return digits < 0 ? 2 : digits;
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, toCurrency(currencyCode));
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    /** Parses a decimal string; never goes through a binary floating point type. */
    public static Money of(String amount, String currencyCode) {
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }
        return new Money(new BigDecimal(amount.trim()), toCurrency(currencyCode));
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, toCurrency(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    private static Currency toCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be empty");
        }
        try {
            return Currency.getInstance(currencyCode.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ISO 4217 currency: " + currencyCode);
        }
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /** Multiply by a decimal factor (rate, percentage). Rounded to the currency scale. */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

    public Money divide(BigDecimal divisor) {
        return new Money(this.amount.divide(divisor, scaleOf(currency), ROUNDING), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isEqualTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) == 0;
    }

    public boolean isZero() {
        return this.amount.signum() == 0;
    }

    public boolean isPositive() {
        return this.amount.signum() > 0;
    }

    public boolean isNegative() {
        return this.amount.signum() < 0;
    }

    public Money abs() {
        return new Money(this.amount.abs(), this.currency);
    }

    public Money negate() {
        return new Money(this.amount.negate(), this.currency);
    }

    public Money min(Money other) {
        return isLessThan(other) ? this : other;
    }

    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    public boolean hasSameCurrencyAs(Money other) {
        return other != null && currency.equals(other.currency);
    }

    /** Plain decimal string at the currency's scale, e.g. "12500.500" for KWD. */
    public String toPlainString() {
        return amount.toPlainString();
    }

    /**
     * Server-side display format (REQ-MON-005): ISO code, grouped digits, currency scale.
     * Example: "KWD 12,500.500", "JPY 1,000,000", "SAR -250.00".
     */
    public String format() {
        int scale = scaleOf(currency);
        StringBuilder pattern = new StringBuilder("#,##0");
        if (scale > 0) {
            pattern.append('.');
            pattern.append("0".repeat(scale));
        }
        DecimalFormat df = new DecimalFormat(pattern.toString(), DecimalFormatSymbols.getInstance(Locale.US));
        df.setRoundingMode(ROUNDING);
        return currency.getCurrencyCode() + " " + df.format(amount);
    }

    @Override
    public int compareTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    private void assertSameCurrency(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot operate on a null amount");
        }
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(this.getCurrencyCode(), other.getCurrencyCode());
        }
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }

    /** Thrown when arithmetic is attempted across currencies (REQ-MON-003). */
    public static class CurrencyMismatchException extends IllegalArgumentException {
        private final String left;
        private final String right;

        public CurrencyMismatchException(String left, String right) {
            super("Cannot operate on different currencies: " + left + " and " + right);
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }
    }
}
