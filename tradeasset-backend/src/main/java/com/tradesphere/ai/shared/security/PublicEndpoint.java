package com.tradesphere.ai.shared.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a handler method that is reachable without an access token. Only the endpoints listed in
 * REQ-SEC-001 may carry it: login, registration, token refresh, logout, password reset, CAPTCHA and
 * MFA challenge completion. The URL must also be listed in {@code SecurityConfiguration}.
 * Every other handler method needs an explicit {@code @PreAuthorize}; startup fails otherwise.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface PublicEndpoint {
}
