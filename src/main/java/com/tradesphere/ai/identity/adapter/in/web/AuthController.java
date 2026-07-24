package com.tradesphere.ai.identity.adapter.in.web;

import com.tradesphere.ai.identity.adapter.in.web.dto.*;
import com.tradesphere.ai.identity.application.port.in.*;
import com.tradesphere.ai.identity.application.port.out.LoadUserPort;
import com.tradesphere.ai.identity.application.port.out.PasswordEncoderPort;
import com.tradesphere.ai.identity.application.port.out.SaveUserPort;
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
    private final PasswordEncoderPort passwordEncoder;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

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

        log.info("Login attempt for user: {} with device: {}", request.getUsername(), request.getDeviceFingerprint());

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

    // TEMPORARY - DEVELOPMENT ONLY - Remove in production
    @GetMapping("/dev/hash-password")
    @Operation(summary = "[DEV] Generate BCrypt hash", description = "Development endpoint to generate password hash")
    public ResponseEntity<Map<String, String>> generatePasswordHash(
            @RequestParam String password) {

        log.warn("DEV ENDPOINT CALLED - Generating hash for password");

        String hash = passwordEncoder.encode(password);
        boolean matches = passwordEncoder.matches(password, hash);

        return ResponseEntity.ok(Map.of(
                "password", password,
                "hash", hash,
                "verification", String.valueOf(matches)
        ));
    }

    // TEMPORARY - DEVELOPMENT ONLY - Debug login issue
    @GetMapping("/dev/debug-user/{username}")
    @Operation(summary = "[DEV] Debug user", description = "Development endpoint to debug user data")
    public ResponseEntity<Map<String, Object>> debugUser(
            @PathVariable String username,
            @RequestParam(required = false) String testPassword) {

        log.warn("DEV ENDPOINT CALLED - Debugging user: {}", username);

        try {
            var userOpt = loadUserPort.loadUserByUsername(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "found", false,
                        "message", "User not found in database"
                ));
            }

            var user = userOpt.get();
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("found", true);
            response.put("userId", user.getId().toString());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("status", user.getStatus().toString());
            response.put("isLocked", user.isLocked());
            response.put("passwordHashPrefix", user.getPasswordHash().substring(0, Math.min(30, user.getPasswordHash().length())));
            response.put("passwordHashLength", user.getPasswordHash().length());

            if (testPassword != null && !testPassword.isEmpty()) {
                boolean matches = passwordEncoder.matches(testPassword, user.getPasswordHash());
                response.put("testPassword", testPassword);
                response.put("passwordMatches", matches);

                // Also test with fresh encoding
                String freshHash = passwordEncoder.encode(testPassword);
                response.put("freshHashGenerated", freshHash.substring(0, Math.min(30, freshHash.length())));
                response.put("freshHashMatches", passwordEncoder.matches(testPassword, freshHash));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "error", true,
                    "message", e.getMessage(),
                    "type", e.getClass().getSimpleName()
            ));
        }
    }

    // TEMPORARY - DEVELOPMENT ONLY - Fix admin password
    @PostMapping("/dev/fix-admin-password")
    @Operation(summary = "[DEV] Fix admin password", description = "Development endpoint to fix admin password hash")
    public ResponseEntity<Map<String, Object>> fixAdminPassword() {
        log.warn("DEV ENDPOINT CALLED - Fixing admin password");

        try {
            var userOpt = loadUserPort.loadUserByUsername("admin");

            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Admin user not found"
                ));
            }

            var user = userOpt.get();

            // Set the correct BCrypt hash for "admin123"
            String correctHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

            // Use reflection to set the password hash (since User is likely immutable)
            // Or better, create a new user instance with updated hash
            var field = user.getClass().getDeclaredField("passwordHash");
            field.setAccessible(true);
            field.set(user, correctHash);

            // Save the updated user
            saveUserPort.saveUser(user);

            // Verify it worked
            var verifyUserOpt = loadUserPort.loadUserByUsername("admin");
            var verifyUser = verifyUserOpt.get();
            boolean matches = passwordEncoder.matches("admin123", verifyUser.getPasswordHash());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Admin password hash updated",
                    "passwordHashLength", verifyUser.getPasswordHash().length(),
                    "passwordMatches", matches
            ));

        } catch (Exception e) {
            log.error("Failed to fix admin password", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "type", e.getClass().getSimpleName()
            ));
        }
    }
}


