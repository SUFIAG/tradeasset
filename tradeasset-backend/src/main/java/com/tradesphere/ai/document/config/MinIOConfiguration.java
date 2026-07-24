package com.tradesphere.ai.document.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO configuration
 * High-performance S3-compatible object storage
 */
@Configuration
public class MinIOConfiguration {

    @Value("${storage.minio.url:http://localhost:9000}")
    private String minioUrl;

    @Value("${storage.minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${storage.minio.secret-key:minioadmin}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioUrl)
            .credentials(accessKey, secretKey)
            .build();
    }
}

