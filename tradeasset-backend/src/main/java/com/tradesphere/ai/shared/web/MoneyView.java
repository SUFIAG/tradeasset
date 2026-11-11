package com.tradesphere.ai.shared.web;

import com.tradesphere.ai.shared.domain.valueobject.Money;

/**
 * Money in an API response (REQ-MON-005). {@code amount} is a plain decimal string at the
 * currency's scale, so no client parses it into a binary float; {@code formatted} is what the
 * client renders as given.
 *
 * <pre>{"amount":"12500.500","currency":"KWD","formatted":"KWD 12,500.500"}</pre>
 */
public record MoneyView(String amount, String currency, String formatted) {

    public static MoneyView of(Money money) {
        if (money == null) {
            return null;
        }
        return new MoneyView(money.toPlainString(), money.getCurrencyCode(), money.format());
    }
}
