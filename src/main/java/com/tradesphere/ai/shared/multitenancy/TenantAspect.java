package com.tradesphere.ai.shared.multitenancy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * AOP aspect to enforce tenant isolation at repository level
 * Intercepts save operations to inject tenant ID
 * High-performance with minimal overhead
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class TenantAspect {

    @Around("execution(* org.springframework.data.repository.CrudRepository+.save(..)) && args(entity)")
    public Object aroundSave(ProceedingJoinPoint joinPoint, Object entity) throws Throwable {
        if (entity != null && TenantContext.isSet()) {
            injectTenantId(entity);
        }
        return joinPoint.proceed();
    }

    private void injectTenantId(Object entity) {
        try {
            var tenantIdField = entity.getClass().getDeclaredField("tenantId");
            tenantIdField.setAccessible(true);

            UUID currentTenantId = (UUID) tenantIdField.get(entity);
            UUID contextTenantId = TenantContext.getTenantId();

            if (currentTenantId == null) {
                tenantIdField.set(entity, contextTenantId);
                log.trace("Injected tenant ID {} into {}", contextTenantId, entity.getClass().getSimpleName());
            } else if (!currentTenantId.equals(contextTenantId)) {
                log.warn("Tenant ID mismatch: entity={}, context={}", currentTenantId, contextTenantId);
                throw new SecurityException("Cannot save entity with different tenant ID");
            }
        } catch (NoSuchFieldException e) {
            // Entity doesn't have tenantId field - skip
        } catch (Exception e) {
            log.error("Error injecting tenant ID", e);
        }
    }
}

