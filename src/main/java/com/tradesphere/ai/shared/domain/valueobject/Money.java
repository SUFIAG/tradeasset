package com.tradesphere.ai.shared.domain.valueobject;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Money Value Object
 * Represents an amount of money in a specific currency
 */
@Value
public class Money implements ValueObject {

    BigDecimal amount;
    Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Add two money amounts (must be same currency)
     */
    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtract two money amounts (must be same currency)
     */
    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiply by a scalar
     */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

    /**
     * Divide by a scalar
     */
    public Money divide(BigDecimal divisor) {
        return new Money(this.amount.divide(divisor, 2, RoundingMode.HALF_UP), this.currency);
    }

    /**
     * Check if this amount is greater than another
     */
    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Check if this amount is less than another
     */
    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Check if this amount equals another
     */
    public boolean isEqualTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) == 0;
    }

    /**
     * Check if amount is zero
     */
    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Check if amount is positive
     */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if amount is negative
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Get absolute value
     */
    public Money abs() {
        return new Money(this.amount.abs(), this.currency);
    }

    /**
     * Negate the amount
     */
    public Money negate() {
        return new Money(this.amount.negate(), this.currency);
    }

    /**
     * Get currency code (e.g., "USD", "EUR")
     */
    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("Cannot operate on different currencies: %s and %s",
                    this.currency.getCurrencyCode(),
                    other.currency.getCurrencyCode()));
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency.getCurrencyCode(), amount);
    }
}

