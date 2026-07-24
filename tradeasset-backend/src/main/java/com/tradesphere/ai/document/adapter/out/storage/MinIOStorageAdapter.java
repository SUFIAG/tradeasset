package com.tradesphere.ai.document.adapter.out.storage;

import com.tradesphere.ai.document.application.port.out.FileStoragePort;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO storage adapter
 * High-performance streaming implementation
 * Memory-efficient with direct streaming (no buffering)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MinIOStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;

    @Value("${storage.minio.default-bucket:documents}")
    private String defaultBucket;

    @Override
    public String storeFile(String bucket, String key, InputStream fileStream, long fileSize, String mimeType) {
        log.debug("Storing file: {}/{} size: {} bytes", bucket, key, fileSize);

        try {
            // Ensure bucket exists
            createBucketIfNotExists(bucket);

            // Upload file with streaming (memory-efficient)
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(fileStream, fileSize, -1) // -1 = unknown part size, auto-calculate
                    .contentType(mimeType)
                    .build()
            );

            log.info("File stored successfully: {}/{}", bucket, key);
            return key;

        } catch (Exception e) {
            log.error("Failed to store file: {}/{}", bucket, key, e);
            throw new RuntimeException("File storage failed", e);
        }
    }

    @Override
    public InputStream retrieveFile(String bucket, String key) {
        log.debug("Retrieving file: {}/{}", bucket, key);

        try {
            // Stream file directly (memory-efficient)
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build()
            );

        } catch (Exception e) {
            log.error("Failed to retrieve file: {}/{}", bucket, key, e);
            throw new RuntimeException("File retrieval failed", e);
        }
    }

    @Override
    public void deleteFile(String bucket, String key) {
        log.debug("Deleting file: {}/{}", bucket, key);

        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build()
            );

            log.info("File deleted successfully: {}/{}", bucket, key);

        } catch (Exception e) {
            log.error("Failed to delete file: {}/{}", bucket, key, e);
            throw new RuntimeException("File deletion failed", e);
        }
    }

    @Override
    public boolean fileExists(String bucket, String key) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build()
            );
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String generatePresignedUrl(String bucket, String key, int expirationSeconds) {
        log.debug("Generating presigned URL: {}/{}", bucket, key);

        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(io.minio.http.Method.GET)
                    .bucket(bucket)
                    .object(key)
                    .expiry(expirationSeconds, TimeUnit.SECONDS)
                    .build()
            );

        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}/{}", bucket, key, e);
            throw new RuntimeException("Presigned URL generation failed", e);
        }
    }

    /**
     * Create bucket if it doesn't exist
     */
    private void createBucketIfNotExists(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build()
                );
                log.info("Bucket created: {}", bucket);
            }

        } catch (Exception e) {
            log.error("Failed to check/create bucket: {}", bucket, e);
            throw new RuntimeException("Bucket creation failed", e);
        }
    }
}

