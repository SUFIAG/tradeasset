package com.tradesphere.ai.security.adapter.in.rest;

import com.tradesphere.ai.security.adapter.in.rest.dto.*;
import com.tradesphere.ai.security.application.port.in.*;
import com.tradesphere.ai.security.application.port.in.dto.*;
import com.tradesphere.ai.security.domain.model.SsoSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Single Sign-On (SSO)
 */
@RestController
@RequestMapping("/api/v1/security/sso")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SSO", description = "Single Sign-On authentication API")
public class SsoController {

    private final InitiateSsoUseCase initiateSsoUseCase;
    private final ProcessSsoCallbackUseCase processSsoCallbackUseCase;
    private final ValidateSsoSessionUseCase validateSsoSessionUseCase;
    private final SsoLogoutUseCase ssoLogoutUseCase;

    /**
     * Initiate SSO authentication
     */
    @PostMapping("/initiate")
    @Operation(summary = "Initiate SSO", description = "Start SSO authentication flow")
    public ResponseEntity<SsoInitiationResponse> initiateSso(
            @Valid @RequestBody SsoInitiateRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        log.info("Initiating SSO: provider={}, tenant={}", request.getProviderId(), tenantId);

        SsoInitiationCommand command = SsoInitiationCommand.builder()
            .providerId(request.getProviderId())
            .returnUrl(request.getReturnUrl())
            .tenantId(tenantId)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        SsoInitiationResponse response = initiateSsoUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Process SSO callback
     */
    @PostMapping("/callback")
    @Operation(summary = "SSO Callback", description = "Process SSO callback from identity provider")
    public ResponseEntity<SsoAuthenticationResponse> processCallback(
            @Valid @RequestBody SsoCallbackRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress,
            @RequestHeader(value = "User-Agent", required = false) String userAgent
    ) {
        log.info("Processing SSO callback: requestId={}, provider={}",
            request.getRequestId(), request.getProviderId());

        SsoCallbackCommand command = SsoCallbackCommand.builder()
            .requestId(request.getRequestId())
            .providerId(request.getProviderId())
            .tenantId(tenantId)
            .parameters(request.getParameters())
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        SsoAuthenticationResponse response = processSsoCallbackUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate SSO session
     */
    @GetMapping("/session/{sessionId}/validate")
    @Operation(summary = "Validate Session", description = "Validate SSO session")
    public ResponseEntity<SessionValidationResponse> validateSession(
            @PathVariable String sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.debug("Validating SSO session: sessionId={}", sessionId);

        boolean valid = validateSsoSessionUseCase.validate(sessionId, tenantId);

        return ResponseEntity.ok(SessionValidationResponse.builder()
            .valid(valid)
            .sessionId(sessionId)
            .build());
    }

    /**
     * Get SSO session details
     */
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get Session", description = "Get SSO session details")
    public ResponseEntity<SsoSessionResponse> getSession(
            @PathVariable String sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.debug("Getting SSO session: sessionId={}", sessionId);

        SsoSession session = validateSsoSessionUseCase.getSession(sessionId, tenantId);

        return ResponseEntity.ok(SsoSessionResponse.builder()
            .sessionId(session.getSessionId())
            .userId(session.getUserId())
            .email(session.getEmail())
            .displayName(session.getDisplayName())
            .providerId(session.getProviderId())
            .createdAt(session.getCreatedAt())
            .expiresAt(session.getExpiresAt())
            .attributes(session.getAttributes())
            .build());
    }

    /**
     * Refresh SSO session
     */
    @PostMapping("/session/{sessionId}/refresh")
    @Operation(summary = "Refresh Session", description = "Refresh SSO session expiry")
    public ResponseEntity<Void> refreshSession(
            @PathVariable String sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        log.debug("Refreshing SSO session: sessionId={}", sessionId);

        validateSsoSessionUseCase.refresh(sessionId, tenantId);

        return ResponseEntity.ok().build();
    }

    /**
     * Logout from SSO
     */
    @PostMapping("/logout")
    @Operation(summary = "SSO Logout", description = "Logout from SSO session")
    public ResponseEntity<Void> logout(
            @RequestBody Map<String, String> request,
            @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        String sessionId = request.get("sessionId");
        log.info("SSO logout: sessionId={}", sessionId);

        ssoLogoutUseCase.execute(sessionId, tenantId);

        return ResponseEntity.ok().build();
    }
}

