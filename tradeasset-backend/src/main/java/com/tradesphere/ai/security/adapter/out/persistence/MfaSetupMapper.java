package com.tradesphere.ai.security.adapter.out.persistence;

import com.tradesphere.ai.security.domain.model.MfaSetup;
import org.springframework.stereotype.Component;

/**
 * Mapper between MFA Setup domain model and JPA entity
 */
@Component
public class MfaSetupMapper {
    
    public MfaSetup toDomain(MfaSetupEntity entity) {
        if (entity == null) return null;
        
        return MfaSetup.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .tenantId(entity.getTenantId())
            .method(entity.getMethod())
            .secret(entity.getSecret())
            .backupCodes(entity.getBackupCodes())
            .enabled(entity.isEnabled())
            .verified(entity.isVerified())
            .createdAt(entity.getCreatedAt())
            .enabledAt(entity.getEnabledAt())
            .lastUsedAt(entity.getLastUsedAt())
            .phoneNumber(entity.getPhoneNumber())
            .email(entity.getEmail())
            .build();
    }
    
    public MfaSetupEntity toEntity(MfaSetup domain) {
        if (domain == null) return null;
        
        return MfaSetupEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .tenantId(domain.getTenantId())
            .method(domain.getMethod())
            .secret(domain.getSecret())
            .backupCodes(domain.getBackupCodes())
            .enabled(domain.isEnabled())
            .verified(domain.isVerified())
            .createdAt(domain.getCreatedAt())
            .enabledAt(domain.getEnabledAt())
            .lastUsedAt(domain.getLastUsedAt())
            .phoneNumber(domain.getPhoneNumber())
            .email(domain.getEmail())
            .build();
    }
}

