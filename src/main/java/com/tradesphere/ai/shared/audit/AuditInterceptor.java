package com.tradesphere.ai.shared.audit;

import com.tradesphere.ai.shared.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * High-performance AOP-based audit interceptor
 * Automatically logs method executions with minimal overhead
 * Uses async logging to avoid blocking business operations
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditInterceptor {

    private final AuditService auditService;

    /**
     * Annotation to mark methods that should be audited
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Audited {
        String action();
        String entityType() default "";
        boolean logParams() default false;
        boolean logResult() default false;
    }

    /**
     * Intercept audited methods and log execution
     * High-performance implementation with minimal memory allocation
     */
    @Around("@annotation(audited)")
    public Object auditMethodExecution(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.trace("Auditing method execution: {}.{}", className, methodName);

        Object result = null;
        boolean success = true;
        String errorMessage = null;

        try {
            // Execute the method
            result = joinPoint.proceed();
            return result;

        } catch (Throwable ex) {
            success = false;
            errorMessage = ex.getMessage();
            throw ex;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            // Async audit logging to avoid blocking
            logAudit(audited, joinPoint, result, success, errorMessage, executionTime);
        }
    }

    /**
     * Log audit information asynchronously
     * Optimized for high throughput
     */
    private void logAudit(Audited audited, ProceedingJoinPoint joinPoint,
                         Object result, boolean success, String errorMessage, long executionTime) {
        try {
            String entityType = audited.entityType().isEmpty()
                ? joinPoint.getTarget().getClass().getSimpleName()
                : audited.entityType();

            String entityId = extractEntityId(joinPoint, result);
            UUID userId = extractUserId(joinPoint);
            String userName = "system"; // Would be extracted from security context

            Map<String, Object> changes = new HashMap<>(4); // Pre-size for memory efficiency

            if (audited.logParams()) {
                changes.put("parameters", extractParameters(joinPoint));
            }

            if (audited.logResult() && result != null) {
                changes.put("result", result.toString());
            }

            String description = String.format("%s.%s",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());

            if (success) {
                auditService.logAsyncWithTiming(
                    entityType,
                    entityId,
                    audited.action(),
                    userId,
                    userName,
                    changes,
                    description,
                    executionTime
                );
            } else {
                auditService.logFailure(
                    entityType,
                    entityId,
                    audited.action(),
                    userId,
                    userName,
                    errorMessage
                );
            }

        } catch (Exception e) {
            // Never throw exceptions from audit logging
            log.error("Failed to log audit", e);
        }
    }

    /**
     * Extract entity ID from method parameters or result
     * Optimized for common patterns
     */
    private String extractEntityId(ProceedingJoinPoint joinPoint, Object result) {
        // Try to get ID from first parameter (common pattern)
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            Object firstArg = args[0];
            if (firstArg instanceof String) {
                return (String) firstArg;
            } else if (firstArg instanceof UUID) {
                return firstArg.toString();
            }
        }

        // Try to get ID from result (for create operations)
        if (result != null) {
            try {
                var idMethod = result.getClass().getMethod("getId");
                Object id = idMethod.invoke(result);
                return id != null ? id.toString() : "unknown";
            } catch (Exception e) {
                // ID extraction failed, use default
            }
        }

        return "unknown";
    }

    /**
     * Extract user ID from method parameters
     * Looks for UUID parameters typically representing userId
     */
    private UUID extractUserId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].contains("userId") || paramNames[i].contains("user")) {
                if (args[i] instanceof UUID) {
                    return (UUID) args[i];
                }
            }
        }

        return null;
    }

    /**
     * Extract method parameters for logging
     * Memory-efficient implementation
     */
    private Map<String, Object> extractParameters(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> params = new HashMap<>(paramNames.length);

        for (int i = 0; i < paramNames.length; i++) {
            // Avoid logging sensitive data
            if (!isSensitiveParam(paramNames[i])) {
                params.put(paramNames[i], args[i]);
            }
        }

        return params;
    }

    /**
     * Check if parameter is sensitive and should not be logged
     */
    private boolean isSensitiveParam(String paramName) {
        String lower = paramName.toLowerCase();
        return lower.contains("password") ||
               lower.contains("secret") ||
               lower.contains("token") ||
               lower.contains("key");
    }
}

