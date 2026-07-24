package com.tradesphere.ai.document.application.port.out;

import java.io.InputStream;

/**
 * Port for file storage operations (MinIO/S3)
 * High-performance streaming interface
 */
public interface FileStoragePort {
    
    /**
     * Store file and return storage key
     * Uses streaming for memory efficiency
     */
    String storeFile(String bucket, String key, InputStream fileStream, long fileSize, String mimeType);
    
    /**
     * Retrieve file as stream
     * Memory-efficient streaming download
     */
    InputStream retrieveFile(String bucket, String key);
    
    /**
     * Delete file from storage
     */
    void deleteFile(String bucket, String key);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String bucket, String key);
    
    /**
     * Generate presigned URL for direct download (optional)
     */
    String generatePresignedUrl(String bucket, String key, int expirationSeconds);
}

