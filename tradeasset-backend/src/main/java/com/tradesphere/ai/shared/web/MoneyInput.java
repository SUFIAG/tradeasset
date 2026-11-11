package com.tradesphere.ai.shared.web;

import com.tradesphere.ai.shared.domain.error.ValidationException;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Money in an API request. The amount is a decimal string, never a JSON number, so it never passes
 * through a binary float (REQ-MON-004). More decimals than the currency allows is rejected rather
 * than silently rounded.
 *
 * <pre>{"amount":"12500.500","currency":"KWD"}</pre>
 */
public record MoneyInput(
        @NotBlank @Pattern(regexp = "-?\\d{1,18}(\\.\\d{1,6})?", message = "must be a decimal string") String amount,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}", message = "must be an ISO 4217 code") String currency) {

    /** Converts to Money, rejecting unknown currencies and excess precision. */
    public Money toMoney(String fieldName) {
        if (amount == null || currency == null) {
            throw new ValidationException(fieldName, "amount and currency are required");
        }
        Currency iso;
        try {
            iso = Currency.getInstance(currency.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValidationException(fieldName + ".currency", "Unknown ISO 4217 currency: " + currency);
        }
        BigDecimal value;
        try {
            value = new BigDecimal(amount.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + ".amount", "must be a decimal string");
        }
        if (value.stripTrailingZeros().scale() > Money.scaleOf(iso)) {
            throw new ValidationException(fieldName + ".amount",
                    iso.getCurrencyCode() + " allows at most " + Money.scaleOf(iso) + " decimal places");
        }
        return Money.of(value, iso);
    }

    /** Converts and requires a strictly positive amount. */
    public Money toPositiveMoney(String fieldName) {
        Money money = toMoney(fieldName);
        if (!money.isPositive()) {
            throw new ValidationException(fieldName + ".amount", "must be greater than zero");
        }
        return money;
    }
}
