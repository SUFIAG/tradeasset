package com.tradesphere.ai.identity.adapter.in.web;

import com.tradesphere.ai.identity.adapter.in.web.dto.LoginRequest;
import com.tradesphere.ai.identity.adapter.in.web.dto.RegisterRequest;
import com.tradesphere.ai.identity.adapter.in.web.dto.AuthResponse;
import com.tradesphere.ai.identity.application.port.in.*;
import com.tradesphere.ai.identity.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Authentication REST Controller
 * Handles user registration and login
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Registers a new user in the system")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantId) {

        log.info("Registering new user: {}", request.getUsername());

        UUID actualTenantId = tenantId != null ? tenantId : UUID.fromString("00000000-0000-0000-0000-000000000001");

        RegisterUserCommand command = RegisterUserCommand.builder()
                .tenantId(actualTenantId)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdBy(actualTenantId) // System user for now
                .build();

        User user = registerUserUseCase.register(command);

        return ResponseEntity.ok(AuthResponse.builder()
                .message("User registered successfully")
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .build());
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for user: {}", request.getUsername());

        LoginCommand command = LoginCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        AuthenticationResult result = loginUseCase.login(command);

        return ResponseEntity.ok(AuthResponse.builder()
                .message("Login successful")
                .userId(result.getUserId())
                .username(result.getUsername())
                .email(result.getEmail())
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .expiresIn(result.getExpiresIn())
                .build());
    }
}

