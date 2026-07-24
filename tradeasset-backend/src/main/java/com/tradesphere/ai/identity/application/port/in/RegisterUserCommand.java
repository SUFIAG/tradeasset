package com.tradesphere.ai.identity.application.port.in;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class RegisterUserCommand {
    UUID tenantId;
    String username;
    String email;
    String password;
    String firstName;
    String lastName;
    UUID createdBy;
}

