package com.tradesphere.ai.identity.application.port.in;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginCommand {
    String username;
    String password;
}

