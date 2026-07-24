package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.SsoInitiationCommand;
import com.tradesphere.ai.security.application.port.in.dto.SsoInitiationResponse;

/**
 * Use Case: Initiate SSO Authentication
 */
public interface InitiateSsoUseCase {

    /**
     * Initiate SSO authentication flow
     */
    SsoInitiationResponse execute(SsoInitiationCommand command);
}

