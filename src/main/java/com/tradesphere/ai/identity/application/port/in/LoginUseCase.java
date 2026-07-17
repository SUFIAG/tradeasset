package com.tradesphere.ai.identity.application.port.in;

public interface LoginUseCase {
    AuthenticationResult login(LoginCommand command);
}

