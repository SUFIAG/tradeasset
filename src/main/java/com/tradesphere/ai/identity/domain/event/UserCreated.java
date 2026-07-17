package com.tradesphere.ai.identity.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreated extends BaseDomainEvent {

    private final String username;
    private final String email;

    private UserCreated(String userId, String username, String email, UUID causedBy) {
        super(userId, "User", causedBy);
        this.username = username;
        this.email = email;
    }

    public static UserCreated of(String userId, String username, String email, UUID causedBy) {
        return new UserCreated(userId, username, email, causedBy);
    }
}

