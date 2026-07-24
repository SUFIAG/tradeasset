package com.tradesphere.ai.identity.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Device Information Domain Model
 * Tracks user devices for security and recognition
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    private UUID id;
    private UUID userId;
    private String deviceFingerprint;  // Unique device identifier
    private String deviceName;
    private String deviceType;  // MOBILE, TABLET, DESKTOP
    private String browser;
    private String browserVersion;
    private String operatingSystem;
    private String osVersion;
    private String ipAddress;
    private String location;  // City, Country
    private boolean trusted;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime trustedAt;
    private int loginCount;

    /**
     * Mark device as trusted
     */
    public void markAsTrusted() {
        this.trusted = true;
        this.trustedAt = LocalDateTime.now();
    }

    /**
     * Update last seen timestamp
     */
    public void updateLastSeen() {
        this.lastSeenAt = LocalDateTime.now();
        this.loginCount++;
    }

    /**
     * Check if device is new (less than 3 logins)
     */
    public boolean isNewDevice() {
        return loginCount < 3;
    }
}

