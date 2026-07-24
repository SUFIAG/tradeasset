package com.tradesphere.ai.security.application.port.in;

import com.tradesphere.ai.security.application.port.in.dto.SsoCallbackCommand;
import com.tradesphere.ai.security.application.port.in.dto.SsoAuthenticationResponse;

/**
 * Use Case: Process SSO Callback
 */
public interface ProcessSsoCallbackUseCase {
    
    /**
     * Process SSO callback from identity provider
     */
    SsoAuthenticationResponse execute(SsoCallbackCommand command);
}

