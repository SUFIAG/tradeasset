package com.tradesphere.ai.document.application.port.out;

/**
 * Port for virus scanning service
 * Abstraction for ClamAV or other antivirus scanners
 */
public interface VirusScanPort {

    /**
     * Scan file for viruses
     * Returns scan result message
     */
    ScanResult scanFile(String filePath);

    /**
     * Scan result
     */
    record ScanResult(boolean passed, String message, String threatName) {

        public static ScanResult clean() {
            return new ScanResult(true, "No threats detected", null);
        }

        public static ScanResult threat(String threatName) {
            return new ScanResult(false, "Threat detected: " + threatName, threatName);
        }

        public static ScanResult error(String message) {
            return new ScanResult(false, "Scan error: " + message, null);
        }
    }
}

