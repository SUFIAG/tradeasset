package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.AcceptEarlyPaymentCommand;
import com.tradesphere.ai.scf.application.port.in.dto.SCFResponse;

/**
 * Use case for accepting early payment offers
 */
public interface AcceptEarlyPaymentUseCase {
    SCFResponse acceptEarlyPayment(AcceptEarlyPaymentCommand command);
}

