package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

/**
 * Inbound port for setting credit limit
 */
public interface SetCreditLimitUseCase {
    Customer setCreditLimit(SetCreditLimitCommand command);
}

