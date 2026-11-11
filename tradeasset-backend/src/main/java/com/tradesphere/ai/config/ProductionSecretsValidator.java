package com.tradesphere.ai.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Refuses to start the {@code prod} profile when a required secret is absent (REQ-SEC-009).
 * Registered in {@code META-INF/spring.factories} so it runs before any bean is created.
 */
public class ProductionSecretsValidator implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final List<String> REQUIRED = List.of(
            "tradesphere.security.jwt.secret",
            "spring.datasource.password",
            "tradesphere.storage.minio.access-key",
            "tradesphere.storage.minio.secret-key");

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Environment env = event.getEnvironment();
        if (!env.matchesProfiles("prod")) {
            return;
        }
        List<String> missing = new ArrayList<>();
        for (String key : REQUIRED) {
            String value = env.getProperty(key);
            if (value == null || value.isBlank()) {
                missing.add(key);
            }
        }
        if (env.getProperty("tradesphere.security.mfa.expose-otp-in-response", Boolean.class, false)) {
            missing.add("tradesphere.security.mfa.expose-otp-in-response must be false in prod");
        }
        if (env.getProperty("tradesphere.document.scanning.allow-unscanned", Boolean.class, false)) {
            missing.add("tradesphere.document.scanning.allow-unscanned must be false in prod");
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Refusing to start in prod: missing or unsafe configuration " + missing);
        }
    }
}
