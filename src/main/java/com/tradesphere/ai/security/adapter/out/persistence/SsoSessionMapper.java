package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.domain.model.SsoSession;
import org.springframework.stereotype.Component;

/**
 * Mapper between SSO Session domain model and JPA entity
 */
@Component
public class SsoSessionMapper {

    public SsoSession toDomain(SsoSessionEntity entity) {
        if (entity == null) return null;

        return SsoSession.builder()
            .id(entity.getId())
            .sessionId(entity.getSessionId())
            .userId(entity.getUserId())
            .email(entity.getEmail())
            .displayName(entity.getDisplayName())
            .providerId(entity.getProviderId())
            .tenantId(entity.getTenantId())
            .createdAt(entity.getCreatedAt())
            .expiresAt(entity.getExpiresAt())
            .lastAccessedAt(entity.getLastAccessedAt())
            .attributes(entity.getAttributes())
            .ipAddress(entity.getIpAddress())
            .userAgent(entity.getUserAgent())
            .active(entity.isActive())
            .build();
    }

    public SsoSessionEntity toEntity(SsoSession domain) {
        if (domain == null) return null;

        return SsoSessionEntity.builder()
            .id(domain.getId())
            .sessionId(domain.getSessionId())
            .userId(domain.getUserId())
            .email(domain.getEmail())
            .displayName(domain.getDisplayName())
            .providerId(domain.getProviderId())
            .tenantId(domain.getTenantId())
            .createdAt(domain.getCreatedAt())
            .expiresAt(domain.getExpiresAt())
            .lastAccessedAt(domain.getLastAccessedAt())
            .attributes(domain.getAttributes())
            .ipAddress(domain.getIpAddress())
            .userAgent(domain.getUserAgent())
            .active(domain.isActive())
            .build();
    }
}

