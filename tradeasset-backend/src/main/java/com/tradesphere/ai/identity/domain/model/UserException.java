package com.tradesphere.ai.identity.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

public class UserException extends DomainException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserException userNotFound(String userId) {
        return new UserException("User not found: " + userId);
    }

    public static UserException userAlreadyExists(String email) {
        return new UserException("User with email already exists: " + email);
    }

    public static UserException invalidCredentials() {
        return new UserException("Invalid username or password");
    }

    public static UserException accountLocked() {
        return new UserException("Account is locked due to multiple failed login attempts");
    }
}

