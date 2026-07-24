package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.util.UUID;

/**
 * Use case for authorizing LC payment
 */
public interface AuthorizePaymentUseCase {

    /**
     * Authorize payment for accepted documents
     */
    LetterOfCredit authorizePayment(String lcId, Money paymentAmount, UUID authorizedBy);
}

