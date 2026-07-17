package com.tradesphere.ai.identity.application.port.in;

import com.tradesphere.ai.identity.domain.model.User;

public interface RegisterUserUseCase {
    User register(RegisterUserCommand command);
}

