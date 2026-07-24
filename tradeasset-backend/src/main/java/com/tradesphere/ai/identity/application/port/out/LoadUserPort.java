package com.tradesphere.ai.identity.application.port.out;

import com.tradesphere.ai.identity.domain.model.User;
import com.tradesphere.ai.identity.domain.model.UserId;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> loadUserById(UserId userId);
    Optional<User> loadUserByUsername(String username);
    Optional<User> loadUserByEmail(String email);
}

