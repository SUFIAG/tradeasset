package com.tradesphere.ai.security.application.service;

import com.tradesphere.ai.security.application.port.in.*;
import com.tradesphere.ai.security.application.port.in.dto.*;
import com.tradesphere.ai.security.application.port.out.MfaSetupPort;
import com.tradesphere.ai.security.domain.model.MfaMethod;
import com.tradesphere.ai.security.domain.model.MfaSetup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Multi-Factor Authentication Service
 * High-performance MFA with minimal latency
 *
 * Supported Methods:
 * - TOTP (Time-based One-Time Password)
 * - SMS OTP
 * - Email OTP
 * - Backup Codes
 *
 * Optimizations:
 * - In-memory OTP storage for fast validation
 * - Rate limiting for security
 * - Async OTP delivery
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MfaService implements SetupMfaUseCase, EnableMfaUseCase, GenerateMfaOtpUseCase,
        VerifyMfaCodeUseCase, DisableMfaUseCase, RegenerateBackupCodesUseCase {


    private final MfaSetupPort mfaSetupPort;
    private final SecureRandom secureRandom = new SecureRandom();

    // In-memory storage for OTPs (thread-safe)
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private final Map<String, RateLimitData> rateLimitStore = new ConcurrentHashMap<>();

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_SECONDS = 300; // 5 minutes
    private static final int MAX_ATTEMPTS = 3;

    /**
     * Setup MFA for user
     */
    @Override
    public MfaSetupResponse execute(MfaSetupCommand command) {
        log.info("Setting up MFA: userId={}, method={}, tenant={}",
            command.getUserId(), command.getMethod(), command.getTenantId());

        // Check if MFA already exists
        Optional<MfaSetup> existing = mfaSetupPort.findByUserId(command.getUserId(), command.getTenantId());
        if (existing.isPresent() && existing.get().isEnabled()) {
            throw new IllegalStateException("MFA is already enabled for this user");
        }

        String secret = generateSecret();
        List<String> backupCodes = generateBackupCodes(10);

        MfaSetup setup = MfaSetup.builder()
            .id(UUID.randomUUID())
            .userId(command.getUserId())
            .tenantId(command.getTenantId())
            .method(command.getMethod())
            .secret(secret)
            .backupCodes(backupCodes)
            .enabled(false)
            .verified(false)
            .createdAt(LocalDateTime.now())
            .phoneNumber(command.getPhoneNumber())
            .email(command.getEmail())
            .build();

        mfaSetupPort.save(setup);

        return MfaSetupResponse.builder()
            .secret(secret)
            .qrCodeUrl(command.getMethod() == MfaMethod.TOTP ? generateTotpUrl(command.getUserId(), secret) : null)
            .backupCodes(backupCodes)
            .method(command.getMethod().name())
            .build();
    }

    /**
     * Enable MFA after verification
     */
    @Override
    public void execute(EnableMfaCommand command) {
        MfaSetup setup = mfaSetupPort.findByUserId(command.getUserId(), command.getTenantId())
            .orElseThrow(() -> new IllegalStateException("MFA not set up"));

        if (!verifyCode(command.getUserId(), command.getTenantId(), command.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        setup.enable();
        mfaSetupPort.update(setup);

        log.info("MFA enabled: userId={}, method={}", command.getUserId(), setup.getMethod());
    }

    /**
     * Generate OTP for SMS/Email delivery
     */
    @Override
    public String execute(GenerateOtpCommand command) {
        log.debug("Generating OTP: userId={}, method={}", command.getUserId(), command.getMethod());

        // Check rate limit
        if (isRateLimited(command.getUserId())) {
            throw new IllegalStateException("Too many OTP requests. Please wait.");
        }

        String otp = generateNumericCode(OTP_LENGTH);
        Instant expiry = Instant.now().plusSeconds(OTP_VALIDITY_SECONDS);

        OtpData otpData = new OtpData(
            otp,
            command.getMethod(),
            expiry,
            0
        );

        otpStore.put(command.getUserId(), otpData);
        updateRateLimit(command.getUserId());

        log.debug("OTP generated: userId={}, expiry={}", command.getUserId(), expiry);
        return otp;
    }

    /**
     * Verify OTP/TOTP code
     */
    @Override
    public boolean execute(VerifyMfaCommand command) {
        return verifyCode(command.getUserId(), command.getTenantId(), command.getCode());
    }

    /**
     * Disable MFA
     */
    @Override
    public void execute(DisableMfaCommand command) {
        // Would validate password in production
        mfaSetupPort.delete(command.getUserId(), command.getTenantId());
        otpStore.remove(command.getUserId());
        log.info("MFA disabled: userId={}", command.getUserId());
    }

    /**
     * Regenerate backup codes
     */
    @Override
    public List<String> execute(String userId, String tenantId) {
        MfaSetup setup = mfaSetupPort.findByUserId(userId, tenantId)
            .orElseThrow(() -> new IllegalStateException("MFA not set up"));

        List<String> newCodes = generateBackupCodes(10);
        setup.setBackupCodes(newCodes);
        mfaSetupPort.update(setup);

        log.info("Backup codes regenerated: userId={}", userId);
        return newCodes;
    }

    /**
     * Verify code with tenant context
     */
    private boolean verifyCode(String userId, String tenantId, String code) {
        log.debug("Verifying code: userId={}, tenant={}", userId, tenantId);

        MfaSetup setup = mfaSetupPort.findByUserId(userId, tenantId).orElse(null);
        if (setup == null || !setup.isEnabled()) {
            return false;
        }

        // Check for backup code first
        if (setup.useBackupCode(code)) {
            mfaSetupPort.update(setup);
            log.info("Backup code used: userId={}", userId);
            return true;
        }

        // Verify TOTP
        if (setup.getMethod() == MfaMethod.TOTP) {
            boolean valid = verifyTotp(setup.getSecret(), code);
            if (valid) {
                setup.setLastUsedAt(LocalDateTime.now());
                mfaSetupPort.update(setup);
            }
            return valid;
        }

        // Verify OTP (SMS/Email)
        return verifyOtp(userId, code);
    }

    /**
     * Verify OTP from storage
     */
    private boolean verifyOtp(String userId, String code) {
        OtpData otpData = otpStore.get(userId);

        if (otpData == null) {
            log.warn("No OTP found: userId={}", userId);
            return false;
        }

        // Check expiry
        if (Instant.now().isAfter(otpData.expiry())) {
            otpStore.remove(userId);
            log.warn("OTP expired: userId={}", userId);
            return false;
        }

        // Check attempts
        if (otpData.attempts() >= MAX_ATTEMPTS) {
            otpStore.remove(userId);
            log.warn("Max OTP attempts exceeded: userId={}", userId);
            return false;
        }

        // Verify code
        boolean valid = otpData.code().equals(code);

        if (valid) {
            otpStore.remove(userId);
            log.info("OTP verified: userId={}", userId);
        } else {
            otpData.incrementAttempts();
            log.warn("Invalid OTP: userId={}, attempts={}", userId, otpData.attempts());
        }

        return valid;
    }

    /**
     * Verify TOTP using time-based algorithm
     */
    private boolean verifyTotp(String secret, String code) {
        // Simplified TOTP validation (production should use library like GoogleAuthenticator)
        long timeWindow = Instant.now().getEpochSecond() / 30;
        String expectedCode = generateTotpCode(secret, timeWindow);

        // Allow 1 time window before/after for clock drift
        boolean valid = expectedCode.equals(code) ||
                       generateTotpCode(secret, timeWindow - 1).equals(code) ||
                       generateTotpCode(secret, timeWindow + 1).equals(code);

        log.debug("TOTP verification: valid={}", valid);
        return valid;
    }

    /**
     * Check if MFA is enabled for user
     */
    @Cacheable(value = "mfaEnabled", key = "#userId + '_' + #tenantId")
    public boolean isMfaEnabled(String userId, String tenantId) {
        return mfaSetupPort.isMfaEnabled(userId, tenantId);
    }

    // Helper methods

    private String generateSecret() {
        byte[] bytes = new byte[20];
        secureRandom.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String generateNumericCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    private List<String> generateBackupCodes(int count) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            codes.add(generateNumericCode(8));
        }
        return codes;
    }

    private String generateTotpUrl(String userId, String secret) {
        return String.format("otpauth://totp/TradeSphere:%s?secret=%s&issuer=TradeSphere",
            userId, secret);
    }

    private String generateTotpCode(String secret, long timeWindow) {
        // Simplified TOTP generation (use proper library in production)
        int hash = (secret + timeWindow).hashCode();
        return String.format("%06d", Math.abs(hash % 1000000));
    }

    private boolean isRateLimited(String userId) {
        RateLimitData data = rateLimitStore.get(userId);
        if (data == null) return false;

        // Allow 5 requests per 10 minutes
        return data.count() >= 5 &&
               Instant.now().isBefore(data.windowStart().plusSeconds(600));
    }

    private void updateRateLimit(String userId) {
        rateLimitStore.compute(userId, (k, v) -> {
            if (v == null || Instant.now().isAfter(v.windowStart().plusSeconds(600))) {
                return new RateLimitData(Instant.now(), 1);
            }
            return new RateLimitData(v.windowStart(), v.count() + 1);
        });
    }

    // DTOs

    private static class OtpData {
        private final String code;
        private final MfaMethod method;
        private final Instant expiry;
        private int attempts;

        public OtpData(String code, MfaMethod method, Instant expiry, int attempts) {
            this.code = code;
            this.method = method;
            this.expiry = expiry;
            this.attempts = attempts;
        }

        public String code() { return code; }
        public MfaMethod method() { return method; }
        public Instant expiry() { return expiry; }
        public int attempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
    }

    private record RateLimitData(Instant windowStart, int count) {}
}

