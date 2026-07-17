package com.tradesphere.ai.identity.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserLoggedIn extends BaseDomainEvent {

    private final String username;
    private final Instant loginTime;

    private UserLoggedIn(String userId, String username, Instant loginTime) {
        super(userId, "User");
        this.username = username;
        this.loginTime = loginTime;
    }

    public static UserLoggedIn of(String userId, String username, Instant loginTime) {
        return new UserLoggedIn(userId, username, loginTime);
    }
}

