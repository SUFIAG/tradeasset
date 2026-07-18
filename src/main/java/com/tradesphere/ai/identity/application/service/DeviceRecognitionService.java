package com.tradesphere.ai.identity.application.service;

import com.tradesphere.ai.identity.domain.model.DeviceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Device Recognition Service
 * Responsibility: Track and identify user devices for security
 *
 * Features:
 * - Device fingerprinting
 * - Trusted device management
 * - New device detection
 * - Suspicious login detection
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceRecognitionService {

    // In-memory storage (in production, use database)
    private final Map<String, DeviceInfo> deviceStore = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, DeviceInfo>> userDevices = new ConcurrentHashMap<>();

    /**
     * Register or update device information
     */
    public DeviceInfo registerDevice(
            UUID userId,
            String userAgent,
            String ipAddress,
            String acceptLanguage) {

        log.debug("Registering device for user: {}", userId);

        // Generate device fingerprint
        String fingerprint = generateDeviceFingerprint(userAgent, ipAddress);

        // Check if device exists
        DeviceInfo device = deviceStore.get(fingerprint);

        if (device == null) {
            // New device
            device = createNewDevice(userId, fingerprint, userAgent, ipAddress);
            deviceStore.put(fingerprint, device);

            // Add to user's devices
            userDevices.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                      .put(fingerprint, device);

            log.info("New device registered: userId={}, fingerprint={}", userId, fingerprint);
        } else {
            // Existing device - update
            device.updateLastSeen();
            log.debug("Existing device updated: userId={}, fingerprint={}", userId, fingerprint);
        }

        return device;
    }

    /**
     * Check if device is trusted
     */
    public boolean isDeviceTrusted(UUID userId, String userAgent, String ipAddress) {
        String fingerprint = generateDeviceFingerprint(userAgent, ipAddress);
        DeviceInfo device = deviceStore.get(fingerprint);

        if (device == null) {
            log.warn("Unknown device detected: userId={}", userId);
            return false;
        }

        if (!device.getUserId().equals(userId)) {
            log.warn("Device fingerprint mismatch: expected userId={}, found={}",
                    userId, device.getUserId());
            return false;
        }

        return device.isTrusted();
    }

    /**
     * Mark device as trusted
     */
    public void trustDevice(UUID userId, String userAgent, String ipAddress) {
        String fingerprint = generateDeviceFingerprint(userAgent, ipAddress);
        DeviceInfo device = deviceStore.get(fingerprint);

        if (device != null && device.getUserId().equals(userId)) {
            device.markAsTrusted();
            log.info("Device marked as trusted: userId={}, fingerprint={}", userId, fingerprint);
        }
    }

    /**
     * Check if login is from new device
     */
    public boolean isNewDevice(UUID userId, String userAgent, String ipAddress) {
        String fingerprint = generateDeviceFingerprint(userAgent, ipAddress);
        DeviceInfo device = deviceStore.get(fingerprint);

        return device == null || device.isNewDevice();
    }

    /**
     * Get all devices for user
     */
    public Map<String, DeviceInfo> getUserDevices(UUID userId) {
        return userDevices.getOrDefault(userId, Map.of());
    }

    /**
     * Remove device (revoke trust)
     */
    public void removeDevice(UUID userId, String fingerprint) {
        DeviceInfo device = deviceStore.get(fingerprint);

        if (device != null && device.getUserId().equals(userId)) {
            deviceStore.remove(fingerprint);

            Map<String, DeviceInfo> devices = userDevices.get(userId);
            if (devices != null) {
                devices.remove(fingerprint);
            }

            log.info("Device removed: userId={}, fingerprint={}", userId, fingerprint);
        }
    }

    // Helper methods

    private DeviceInfo createNewDevice(UUID userId, String fingerprint, String userAgent, String ipAddress) {
        UserAgentInfo uaInfo = parseUserAgent(userAgent);

        return DeviceInfo.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .deviceFingerprint(fingerprint)
            .deviceName(uaInfo.browser + " on " + uaInfo.os)
            .deviceType(uaInfo.deviceType)
            .browser(uaInfo.browser)
            .browserVersion(uaInfo.browserVersion)
            .operatingSystem(uaInfo.os)
            .osVersion(uaInfo.osVersion)
            .ipAddress(ipAddress)
            .trusted(false)
            .firstSeenAt(LocalDateTime.now())
            .lastSeenAt(LocalDateTime.now())
            .loginCount(1)
            .build();
    }

    private String generateDeviceFingerprint(String userAgent, String ipAddress) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = userAgent + "|" + extractIpNetwork(ipAddress);
            byte[] hash = digest.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating device fingerprint", e);
            return UUID.randomUUID().toString();
        }
    }

    private String extractIpNetwork(String ipAddress) {
        // Extract network portion of IP (first 3 octets for IPv4)
        if (ipAddress != null && ipAddress.contains(".")) {
            String[] parts = ipAddress.split("\\.");
            if (parts.length >= 3) {
                return parts[0] + "." + parts[1] + "." + parts[2];
            }
        }
        return ipAddress;
    }

    private UserAgentInfo parseUserAgent(String userAgent) {
        UserAgentInfo info = new UserAgentInfo();

        if (userAgent == null || userAgent.isEmpty()) {
            info.browser = "Unknown";
            info.os = "Unknown";
            info.deviceType = "DESKTOP";
            return info;
        }

        String ua = userAgent.toLowerCase();

        // Browser detection
        if (ua.contains("chrome") && !ua.contains("edg")) {
            info.browser = "Chrome";
        } else if (ua.contains("firefox")) {
            info.browser = "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            info.browser = "Safari";
        } else if (ua.contains("edg")) {
            info.browser = "Edge";
        } else {
            info.browser = "Other";
        }

        // OS detection
        if (ua.contains("windows")) {
            info.os = "Windows";
        } else if (ua.contains("mac")) {
            info.os = "macOS";
        } else if (ua.contains("linux")) {
            info.os = "Linux";
        } else if (ua.contains("android")) {
            info.os = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            info.os = "iOS";
        } else {
            info.os = "Unknown";
        }

        // Device type detection
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            info.deviceType = "MOBILE";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            info.deviceType = "TABLET";
        } else {
            info.deviceType = "DESKTOP";
        }

        return info;
    }

    private static class UserAgentInfo {
        String browser = "Unknown";
        String browserVersion = "Unknown";
        String os = "Unknown";
        String osVersion = "Unknown";
        String deviceType = "DESKTOP";
    }
}

