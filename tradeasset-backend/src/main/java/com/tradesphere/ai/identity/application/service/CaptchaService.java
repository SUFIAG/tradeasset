package com.tradesphere.ai.identity.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CAPTCHA Service
 * Responsibility: Protect against automated bot attacks
 *
 * Features:
 * - Simple mathematical CAPTCHA generation
 * - CAPTCHA validation with expiry
 * - Rate limiting
 * - Bot detection heuristics
 *
 * Note: In production, integrate with Google reCAPTCHA or hCaptcha
 */
@Service
@Slf4j
public class CaptchaService {

    private final SecureRandom random = new SecureRandom();

    // In-memory storage (in production, use Redis)
    private final Map<String, CaptchaChallenge> challenges = new ConcurrentHashMap<>();
    private final Map<String, RateLimitInfo> rateLimits = new ConcurrentHashMap<>();

    private static final long CAPTCHA_VALIDITY_SECONDS = 300; // 5 minutes
    private static final int MAX_ATTEMPTS = 3;

    /**
     * Generate CAPTCHA challenge
     */
    public CaptchaResponse generateCaptcha() {
        String challengeId = UUID.randomUUID().toString();

        // Generate simple math problem
        int num1 = random.nextInt(20) + 1;
        int num2 = random.nextInt(20) + 1;
        String operation = random.nextBoolean() ? "+" : "-";

        int answer = operation.equals("+") ? num1 + num2 : num1 + num2 + num2; // Ensure positive result
        if (operation.equals("-")) {
            int temp = num1;
            num1 = num1 + num2;
            num2 = temp;
            answer = num1 - num2;
        }

        String question = String.format("What is %d %s %d?", num1, operation, num2);

        CaptchaChallenge challenge = new CaptchaChallenge(
            challengeId,
            question,
            String.valueOf(answer),
            Instant.now().plusSeconds(CAPTCHA_VALIDITY_SECONDS),
            0
        );

        challenges.put(challengeId, challenge);

        log.debug("CAPTCHA generated: challengeId={}", challengeId);

        return new CaptchaResponse(challengeId, question);
    }

    /**
     * Validate CAPTCHA response
     */
    public boolean validateCaptcha(String challengeId, String userAnswer) {
        CaptchaChallenge challenge = challenges.get(challengeId);

        if (challenge == null) {
            log.warn("Invalid CAPTCHA challenge ID: {}", challengeId);
            return false;
        }

        // Check expiry
        if (Instant.now().isAfter(challenge.expiry)) {
            challenges.remove(challengeId);
            log.warn("CAPTCHA expired: {}", challengeId);
            return false;
        }

        // Check attempts
        if (challenge.attempts >= MAX_ATTEMPTS) {
            challenges.remove(challengeId);
            log.warn("Max CAPTCHA attempts exceeded: {}", challengeId);
            return false;
        }

        // Validate answer
        challenge.attempts++;
        boolean valid = challenge.answer.trim().equals(userAnswer.trim());

        if (valid) {
            challenges.remove(challengeId);
            log.info("CAPTCHA validated successfully: {}", challengeId);
        } else {
            log.warn("Invalid CAPTCHA answer: challengeId={}, attempts={}",
                    challengeId, challenge.attempts);
        }

        return valid;
    }

    /**
     * Check if CAPTCHA is required for IP/user
     * Based on suspicious activity or rate limiting
     */
    public boolean isCaptchaRequired(String ipAddress) {
        RateLimitInfo rateLimit = rateLimits.get(ipAddress);

        if (rateLimit == null) {
            return false;
        }

        // Reset if window passed
        if (Instant.now().isAfter(rateLimit.windowStart.plusSeconds(300))) {
            rateLimits.remove(ipAddress);
            return false;
        }

        // Require CAPTCHA after 3 failed attempts
        return rateLimit.failedAttempts >= 3;
    }

    /**
     * Record failed login attempt
     */
    public void recordFailedAttempt(String ipAddress) {
        rateLimits.compute(ipAddress, (k, v) -> {
            if (v == null || Instant.now().isAfter(v.windowStart.plusSeconds(300))) {
                return new RateLimitInfo(Instant.now(), 1);
            }
            return new RateLimitInfo(v.windowStart, v.failedAttempts + 1);
        });

        log.debug("Failed attempt recorded for IP: {}", ipAddress);
    }

    /**
     * Clear rate limit (after successful login)
     */
    public void clearRateLimit(String ipAddress) {
        rateLimits.remove(ipAddress);
        log.debug("Rate limit cleared for IP: {}", ipAddress);
    }

    /**
     * Detect bot behavior patterns
     */
    public boolean detectBotBehavior(String userAgent, String ipAddress) {
        if (userAgent == null || userAgent.isEmpty()) {
            log.warn("Suspicious: Empty user agent from IP {}", ipAddress);
            return true;
        }

        String ua = userAgent.toLowerCase();

        // Check for common bot indicators
        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider") ||
            ua.contains("scraper") || ua.contains("curl") || ua.contains("wget") ||
            ua.contains("python") || ua.contains("java/")) {
            log.warn("Bot detected: userAgent={}, IP={}", userAgent, ipAddress);
            return true;
        }

        return false;
    }

    // DTOs

    public record CaptchaResponse(String challengeId, String question) {}

    private static class CaptchaChallenge {
        String challengeId;
        String question;
        String answer;
        Instant expiry;
        int attempts;

        CaptchaChallenge(String challengeId, String question, String answer, Instant expiry, int attempts) {
            this.challengeId = challengeId;
            this.question = question;
            this.answer = answer;
            this.expiry = expiry;
            this.attempts = attempts;
        }
    }

    private record RateLimitInfo(Instant windowStart, int failedAttempts) {}
}

