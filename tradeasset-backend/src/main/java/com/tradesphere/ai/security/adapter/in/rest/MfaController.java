package com.tradesphere.ai.security.adapter.in.rest;

import com.tradesphere.ai.security.adapter.in.rest.dto.*;
import com.tradesphere.ai.security.application.port.in.*;
import com.tradesphere.ai.security.application.port.in.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Multi-Factor Authentication (MFA)
 */
@RestController
@RequestMapping("/api/v1/security/mfa")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MFA", description = "Multi-Factor Authentication API")
public class MfaController {

    private final SetupMfaUseCase setupMfaUseCase;
    private final EnableMfaUseCase enableMfaUseCase;
    private final GenerateMfaOtpUseCase generateMfaOtpUseCase;
    private final VerifyMfaCodeUseCase verifyMfaCodeUseCase;
    private final DisableMfaUseCase disableMfaUseCase;
    private final RegenerateBackupCodesUseCase regenerateBackupCodesUseCase;

    /**
     * Setup MFA for user
     */
    @PostMapping("/setup")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Setup MFA", description = "Setup multi-factor authentication for user")
    public ResponseEntity<MfaSetupResponse> setupMfa(
            @Valid @RequestBody MfaSetupRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Setting up MFA: userId={}, method={}", userId, request.getMethod());

        MfaSetupCommand command = MfaSetupCommand.builder()
            .userId(userId)
            .tenantId(tenantId)
            .method(request.getMethod())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .build();

        MfaSetupResponse response = setupMfaUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Enable MFA after verification
     */
    @PostMapping("/enable")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Enable MFA", description = "Enable MFA after successful verification")
    public ResponseEntity<Void> enableMfa(
            @Valid @RequestBody MfaEnableRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Enabling MFA: userId={}", userId);

        EnableMfaCommand command = EnableMfaCommand.builder()
            .userId(userId)
            .tenantId(tenantId)
            .verificationCode(request.getVerificationCode())
            .build();

        enableMfaUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Generate OTP for SMS/Email
     */
    @PostMapping("/otp/generate")
    @Operation(summary = "Generate OTP", description = "Generate one-time password for SMS/Email")
    public ResponseEntity<OtpGenerateResponse> generateOtp(
            @Valid @RequestBody OtpGenerateRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Generating OTP: userId={}, method={}", userId, request.getMethod());

        GenerateOtpCommand command = GenerateOtpCommand.builder()
            .userId(userId)
            .tenantId(tenantId)
            .method(request.getMethod())
            .build();

        String otp = generateMfaOtpUseCase.execute(command);

        // In production, send OTP via SMS/Email instead of returning it
        return ResponseEntity.ok(OtpGenerateResponse.builder()
            .message("OTP sent successfully")
            .expiresInSeconds(300)
            .build());
    }

    /**
     * Verify MFA code
     */
    @PostMapping("/verify")
    @Operation(summary = "Verify MFA Code", description = "Verify TOTP/OTP/Backup code")
    public ResponseEntity<MfaVerifyResponse> verifyCode(
            @Valid @RequestBody MfaVerifyRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Verifying MFA code: userId={}", userId);

        VerifyMfaCommand command = VerifyMfaCommand.builder()
            .userId(userId)
            .tenantId(tenantId)
            .code(request.getCode())
            .build();

        boolean valid = verifyMfaCodeUseCase.execute(command);

        return ResponseEntity.ok(MfaVerifyResponse.builder()
            .valid(valid)
            .message(valid ? "Verification successful" : "Invalid code")
            .build());
    }

    /**
     * Disable MFA
     */
    @PostMapping("/disable")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Disable MFA", description = "Disable multi-factor authentication")
    public ResponseEntity<Void> disableMfa(
            @Valid @RequestBody MfaDisableRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Disabling MFA: userId={}", userId);

        DisableMfaCommand command = DisableMfaCommand.builder()
            .userId(userId)
            .tenantId(tenantId)
            .password(request.getPassword())
            .build();

        disableMfaUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    /**
     * Regenerate backup codes
     */
    @PostMapping("/backup-codes/regenerate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Regenerate Backup Codes", description = "Generate new backup codes")
    public ResponseEntity<BackupCodesResponse> regenerateBackupCodes(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId
    ) {
        log.info("Regenerating backup codes: userId={}", userId);

        List<String> codes = regenerateBackupCodesUseCase.execute(userId, tenantId);

        return ResponseEntity.ok(BackupCodesResponse.builder()
            .backupCodes(codes)
            .build());
    }
}

