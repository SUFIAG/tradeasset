package com.tradesphere.ai.identity.application.service;

import com.tradesphere.ai.identity.application.port.in.RequestPasswordResetUseCase;
import com.tradesphere.ai.identity.application.port.in.ResetPasswordUseCase;
import com.tradesphere.ai.identity.application.port.out.LoadUserPort;
import com.tradesphere.ai.identity.application.port.out.PasswordEncoderPort;
import com.tradesphere.ai.identity.application.port.out.SaveUserPort;
import com.tradesphere.ai.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Password Reset Service
 * Responsibility: Handle forgot password and password reset functionality
 *
 * Business Rules:
 * - Reset token valid for 1 hour
 * - Token can only be used once
 * - Rate limiting: Max 3 requests per hour per email
 * - Password must meet complexity requirements
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService implements RequestPasswordResetUseCase, ResetPasswordUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoderPort passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    // In-memory storage (in production, use Redis or database)
    private final Map<String, PasswordResetToken> resetTokens = new ConcurrentHashMap<>();
    private final Map<String, RateLimitData> rateLimits = new ConcurrentHashMap<>();

    private static final long TOKEN_VALIDITY_SECONDS = 3600; // 1 hour
    private static final int MAX_REQUESTS_PER_HOUR = 3;

    @Override
    public String requestPasswordReset(String email) {
        log.info("Password reset requested for email: {}", email);

        // Check rate limit
        if (isRateLimited(email)) {
            log.warn("Rate limit exceeded for email: {}", email);
            // Return success anyway to prevent email enumeration
            return "If the email exists, a reset link has been sent";
        }

        // Load user by email
        User user = loadUserPort.loadUserByEmail(email).orElse(null);

        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            // Don't reveal that user doesn't exist (security best practice)
            updateRateLimit(email);
            return "If the email exists, a reset link has been sent";
        }

        // Generate secure random token
        String token = generateSecureToken();
        Instant expiry = Instant.now().plusSeconds(TOKEN_VALIDITY_SECONDS);

        // Store token
        PasswordResetToken resetToken = new PasswordResetToken(
            token,
            user.getId().getValue().toString(),
            email,
            expiry,
            false
        );

        resetTokens.put(token, resetToken);
        updateRateLimit(email);

        log.info("Password reset token generated for user: {}, expiry: {}", user.getUsername(), expiry);

        // In production: Send email with reset link containing token
        // For now, return token (ONLY FOR DEVELOPMENT)
        return token;
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        log.info("Password reset attempt with token");

        // Validate token
        PasswordResetToken resetToken = resetTokens.get(token);

        if (resetToken == null) {
            log.warn("Invalid password reset token");
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        if (resetToken.used()) {
            log.warn("Password reset token already used");
            throw new IllegalArgumentException("Reset token has already been used");
        }

        if (Instant.now().isAfter(resetToken.expiry())) {
            resetTokens.remove(token);
            log.warn("Password reset token expired");
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Validate new password
        validatePassword(newPassword);

        // Load user
        User user = loadUserPort.loadUserByEmail(resetToken.email())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update password
        String passwordHash = passwordEncoder.encode(newPassword);
        user.updatePassword(passwordHash, user.getId().getValue()); // Self-update

        // Save user
        saveUserPort.saveUser(user);

        // Mark token as used and remove
        resetToken.markUsed();
        resetTokens.remove(token);

        log.info("Password reset successful for user: {}", user.getUsername());

        return "Password reset successful";
    }

    // Helper methods

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    private boolean isRateLimited(String email) {
        RateLimitData data = rateLimits.get(email);
        if (data == null) return false;

        // Reset counter if window has passed
        if (Instant.now().isAfter(data.windowStart().plusSeconds(3600))) {
            rateLimits.remove(email);
            return false;
        }

        return data.count() >= MAX_REQUESTS_PER_HOUR;
    }

    private void updateRateLimit(String email) {
        rateLimits.compute(email, (k, v) -> {
            if (v == null || Instant.now().isAfter(v.windowStart().plusSeconds(3600))) {
                return new RateLimitData(Instant.now(), 1);
            }
            return new RateLimitData(v.windowStart(), v.count() + 1);
        });
    }

    // DTOs

    private static class PasswordResetToken {
        private final String token;
        private final String userId;
        private final String email;
        private final Instant expiry;
        private boolean used;

        public PasswordResetToken(String token, String userId, String email, Instant expiry, boolean used) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.expiry = expiry;
            this.used = used;
        }

        public String token() { return token; }
        public String userId() { return userId; }
        public String email() { return email; }
        public Instant expiry() { return expiry; }
        public boolean used() { return used; }
        public void markUsed() { this.used = true; }
    }

    private record RateLimitData(Instant windowStart, int count) {}
}


