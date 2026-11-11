package com.tradesphere.ai.shared.application.port.out.compliance;

import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Trade-based money laundering red-flag detection (REQ-CMP-006), implemented by the compliance
 * module. Raised flags are stored as alerts for a compliance officer to review; they do not
 * dispose of anything automatically.
 */
public interface TbmlGateway {

    /** Evaluates the facts, stores an alert for every raised flag and returns the flags. */
    List<TbmlFlag> evaluate(TradeFacts facts);

    /**
     * @param quantity       may be null; with {@code unit} and {@code hsCode} enables the price check
     * @param routeCountries ISO alpha-2 codes of ports or transit countries, in order
     */
    record TradeFacts(UUID tenantId, String entityType, UUID entityId, String reference, UUID customerId,
                      String customerIndustry, Money amount, String goodsDescription, String hsCode,
                      BigDecimal quantity, String unit, String originCountry, String destinationCountry,
                      List<String> routeCountries, List<String> counterpartyCountries, UUID requestedBy) {
    }

    /**
     * @param code     PRICE_DEVIATION, UNUSUAL_ROUTING, HIGH_RISK_JURISDICTION, GOODS_BUSINESS_MISMATCH,
     *                 ROUND_AMOUNT, DUAL_USE_GOODS
     * @param severity LOW, MEDIUM or HIGH
     */
    record TbmlFlag(String code, String severity, String description) {
    }
}
