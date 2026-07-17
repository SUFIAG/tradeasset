package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for completing LC payment
 */
public interface CompletePaymentUseCase {
    
    /**
     * Complete payment for an LC
     * @param lcId LC identifier
     * @param completedBy User who completed payment
     * @return Updated LC
     */
    LetterOfCredit completePayment(String lcId, UUID completedBy);
}

