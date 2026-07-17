package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.domain.model.IdentityProvider;
import org.springframework.stereotype.Component;

/**
 * Mapper between Identity Provider domain model and JPA entity
 */
@Component
public class IdentityProviderMapper {

    public IdentityProvider toDomain(IdentityProviderEntity entity) {
        if (entity == null) return null;

        return IdentityProvider.builder()
            .id(entity.getId())
            .providerId(entity.getProviderId())
            .providerName(entity.getProviderName())
            .tenantId(entity.getTenantId())
            .protocol(entity.getProtocol())
            .authEndpoint(entity.getAuthEndpoint())
            .tokenEndpoint(entity.getTokenEndpoint())
            .userInfoEndpoint(entity.getUserInfoEndpoint())
            .logoutEndpoint(entity.getLogoutEndpoint())
            .clientId(entity.getClientId())
            .clientSecret(entity.getClientSecret())
            .additionalConfig(entity.getAdditionalConfig())
            .active(entity.isActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public IdentityProviderEntity toEntity(IdentityProvider domain) {
        if (domain == null) return null;

        return IdentityProviderEntity.builder()
            .id(domain.getId())
            .providerId(domain.getProviderId())
            .providerName(domain.getProviderName())
            .tenantId(domain.getTenantId())
            .protocol(domain.getProtocol())
            .authEndpoint(domain.getAuthEndpoint())
            .tokenEndpoint(domain.getTokenEndpoint())
            .userInfoEndpoint(domain.getUserInfoEndpoint())
            .logoutEndpoint(domain.getLogoutEndpoint())
            .clientId(domain.getClientId())
            .clientSecret(domain.getClientSecret())
            .additionalConfig(domain.getAdditionalConfig())
            .active(domain.isActive())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}

