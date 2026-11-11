package com.tradesphere.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.shared.security.JwtAuthenticationFilter;
import com.tradesphere.ai.shared.security.JwtCodec;
import com.tradesphere.ai.shared.web.Problems;
import com.tradesphere.ai.shared.web.idempotency.IdempotencyFilter;
import com.tradesphere.ai.shared.web.idempotency.IdempotencyStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Deny by default (REQ-SEC-001). Only the endpoints listed in {@link #PUBLIC_POST} and
 * {@link #PUBLIC_GET} are reachable without a valid access token; everything else under /api needs
 * one, and anything outside the known URL spaces is denied. Every controller method additionally
 * carries an explicit {@code @PreAuthorize}, checked at startup by {@link EndpointAuthorizationVerifier}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    /** Unauthenticated POST endpoints. Keep in sync with {@code @PublicEndpoint} handlers. */
    public static final String[] PUBLIC_POST = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/api/v1/auth/password/forgot",
            "/api/v1/auth/password/reset",
            "/api/v1/auth/mfa/verify",
            "/api/v1/auth/mfa/enrollment/start",
            "/api/v1/auth/mfa/enrollment/confirm"
    };

    /** Unauthenticated GET endpoints. */
    public static final String[] PUBLIC_GET = {
            "/api/v1/auth/captcha",
            "/actuator/health",
            "/actuator/health/**",
            "/api-docs",
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtCodec jwtCodec, ObjectMapper mapper,
                                                   IdempotencyStore idempotencyStore,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Bearer tokens only, no cookies: CSRF does not apply.
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())
                .headers(h -> h
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; frame-ancestors 'none'; object-src 'none'"))
                        .frameOptions(f -> f.deny()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/**").hasRole("SYSTEM_ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) -> {
                            boolean expired = Boolean.TRUE.equals(
                                    request.getAttribute(JwtAuthenticationFilter.EXPIRED_TOKEN_ATTRIBUTE));
                            Problems.write(response, mapper, expired
                                    ? Problems.of(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_EXPIRED",
                                    "The access token has expired. Refresh it and retry.")
                                    : Problems.of(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_REQUIRED",
                                    "A valid access token is required"));
                        })
                        .accessDeniedHandler((request, response, ex) -> Problems.write(response, mapper,
                                Problems.of(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                                        "You do not have permission for this action"))))
                .addFilterBefore(new JwtAuthenticationFilter(jwtCodec), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new IdempotencyFilter(idempotencyStore, mapper), AuthorizationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // REQ-SEC-006
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${tradesphere.security.cors.allowed-origins:http://localhost:5173}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Accept-Language",
                IdempotencyFilter.HEADER, "If-Match"));
        config.setExposedHeaders(List.of(IdempotencyFilter.REPLAYED_HEADER, "Content-Disposition", "ETag", "Location"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
