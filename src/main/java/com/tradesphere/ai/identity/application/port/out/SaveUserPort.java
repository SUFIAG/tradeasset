package com.tradesphere.ai.identity.application.port.out;

import com.tradesphere.ai.identity.domain.model.User;

public interface SaveUserPort {
    User saveUser(User user);
}

