package com.tradesphere.ai.shared.application.port.out.risk;

import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Cross-module credit and limit checks (REQ-CUS-005, REQ-CUS-006, REQ-RSK-001..003). Implemented by
 * the risk module. Exposure is read from the {@code CREDIT_EXPOSURE} account of the movement
 * journal; it is never stored as an authoritative number.
 */
public interface CreditRiskGateway {

    /**
     * Pre-transaction limit check. Evaluates every applicable limit in the hierarchy (counterparty,
     * group, country, product) and the tightest one binds. Amounts in a currency other than the
     * limit's are converted explicitly with a recorded FX rate. An approved temporary excess for
     * this exact instrument is taken into account.
     *
     * @param productType  an {@code EntityTypes} instrument code: LC, BG, DC, SCF
     * @param instrumentId the instrument being issued, used to find an approved temporary excess
     */
    LimitCheck checkHeadroom(UUID tenantId, UUID customerId, String productType, UUID instrumentId, Money requested);

    /** The customer's active credit limit, exposure and available credit (REQ-CUS-006). */
    CreditPosition creditPosition(UUID tenantId, UUID customerId);

    /**
     * @param withinLimit   true when every applicable limit has headroom for the request
     * @param bindingScope  the scope of the tightest limit, for example COUNTERPARTY or COUNTRY;
     *                      null when no limit applies
     * @param blockers      stable codes: NO_CREDIT_LIMIT, CREDIT_LIMIT_EXPIRED, LIMIT_EXCEEDED,
     *                      FX_RATE_UNAVAILABLE
     * @param conversions   every FX conversion used, with rate, source and date (REQ-MON-003)
     */
    record LimitCheck(boolean withinLimit, String bindingScope, Money limit, Money exposure, Money headroom,
                      Money requestedInLimitCurrency, List<String> blockers, List<FxConversion> conversions,
                      boolean temporaryExcessApplied) {
    }

    record FxConversion(String fromCurrency, String toCurrency, BigDecimal rate, String source, Instant asOf) {
    }

    /**
     * @param hasLimit   false when no active credit limit is set; the other money fields are then null
     * @param limitExpiry calendar date on which the limit lapses
     */
    record CreditPosition(boolean hasLimit, Money limit, java.time.LocalDate limitExpiry, Money exposure,
                          Money available, List<FxConversion> conversions) {
    }
}
