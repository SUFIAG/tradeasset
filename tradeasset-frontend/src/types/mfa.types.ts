/**
 * MFA (Multi-Factor Authentication) Type Definitions
 * Aligned with backend MFA implementation
 */

export type MfaMethod = 'TOTP' | 'SMS' | 'EMAIL' | 'BACKUP_CODE';

export const MfaMethod = {
  TOTP: 'TOTP' as const,
  SMS: 'SMS' as const,
  EMAIL: 'EMAIL' as const,
  BACKUP_CODE: 'BACKUP_CODE' as const,
};

export interface MfaSetupRequest {
  method: MfaMethod;
  phoneNumber?: string;  // Required for SMS
  email?: string;        // Required for EMAIL
}

export interface MfaSetupResponse {
  secret: string;
  qrCodeUrl: string;
  backupCodes: string[];
  method: MfaMethod;
}

export interface MfaEnableRequest {
  code: string;  // 6-digit verification code
}

export interface MfaVerifyRequest {
  code: string;
  mfaToken?: string;
}

export interface MfaVerifyResponse {
  valid: boolean;
  message: string;
  accessToken?: string;
  refreshToken?: string;
}

export interface OtpGenerateRequest {
  method: MfaMethod;
}

export interface OtpGenerateResponse {
  message: string;
  expiresIn: number;
}

export interface BackupCodesResponse {
  backupCodes: string[];
  message: string;
}

