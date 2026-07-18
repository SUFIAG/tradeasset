package com.tradesphere.ai.identity.adapter.in.web;

import com.tradesphere.ai.identity.adapter.in.web.dto.*;
import com.tradesphere.ai.identity.application.port.in.*;
import com.tradesphere.ai.identity.application.service.CaptchaService;
import com.tradesphere.ai.identity.application.service.DeviceRecognitionService;
import com.tradesphere.ai.identity.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final CaptchaService captchaService;
    private final DeviceRecognitionService deviceRecognitionService;

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

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Sends password reset link to user's email")
    public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        log.info("Password reset requested for email: {}", request.getEmail());

        // Request password reset - token will be sent via email
        requestPasswordResetUseCase.requestPasswordReset(request.getEmail());

        // In production, don't return the token - it's sent via email
        return ResponseEntity.ok(AuthResponse.builder()
                .message("If the email exists, a reset link has been sent")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using token from email")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        log.info("Password reset attempt with token");

        String message = resetPasswordUseCase.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(AuthResponse.builder()
                .message(message)
                .build());
    }

    @GetMapping("/captcha/generate")
    @Operation(summary = "Generate CAPTCHA", description = "Generate CAPTCHA challenge for bot protection")
    public ResponseEntity<CaptchaResponse> generateCaptcha() {

        log.debug("Generating CAPTCHA challenge");

        CaptchaService.CaptchaResponse captcha = captchaService.generateCaptcha();

        return ResponseEntity.ok(new CaptchaResponse(
                captcha.challengeId(),
                captcha.question()
        ));
    }

    @GetMapping("/captcha/required")
    @Operation(summary = "Check if CAPTCHA required", description = "Check if CAPTCHA is required for current session")
    public ResponseEntity<CaptchaRequiredResponse> isCaptchaRequired(
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {

        String ip = ipAddress != null ? ipAddress : "unknown";
        boolean required = captchaService.isCaptchaRequired(ip);

        return ResponseEntity.ok(new CaptchaRequiredResponse(required));
    }

    @GetMapping("/devices")
    @Operation(summary = "Get user devices", description = "Get list of recognized devices for current user")
    public ResponseEntity<DevicesResponse> getUserDevices(
            @RequestHeader("X-User-ID") String userId) {

        log.debug("Getting devices for user: {}", userId);

        Map<String, com.tradesphere.ai.identity.domain.model.DeviceInfo> devices =
                deviceRecognitionService.getUserDevices(UUID.fromString(userId));

        return ResponseEntity.ok(new DevicesResponse(devices.values().stream().toList()));
    }

    @PostMapping("/devices/{fingerprint}/trust")
    @Operation(summary = "Trust device", description = "Mark a device as trusted")
    public ResponseEntity<Void> trustDevice(
            @PathVariable String fingerprint,
            @RequestHeader("X-User-ID") String userId,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {

        log.info("Trusting device for user: {}", userId);

        deviceRecognitionService.trustDevice(
                UUID.fromString(userId),
                userAgent != null ? userAgent : "unknown",
                ipAddress != null ? ipAddress : "unknown"
        );

        return ResponseEntity.ok().build();
    }
}


