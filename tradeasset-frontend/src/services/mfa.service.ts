/**
 * MFA Service
 * Handles all MFA-related API calls
 */

import { apiClient } from '../lib/api-client';
import { API_CONFIG } from '../config/constants';
import type {
  MfaSetupRequest,
  MfaSetupResponse,
  MfaEnableRequest,
  MfaVerifyRequest,
  MfaVerifyResponse,
  OtpGenerateRequest,
  OtpGenerateResponse,
  BackupCodesResponse,
} from '../types/mfa.types';

export const mfaService = {
  /**
   * Setup MFA for user
   */
  async setupMfa(data: MfaSetupRequest): Promise<MfaSetupResponse> {
    const response = await apiClient.post<MfaSetupResponse>(
      API_CONFIG.ENDPOINTS.MFA.SETUP,
      data
    );
    return response.data;
  },

  /**
   * Enable MFA with verification code
   */
  async enableMfa(data: MfaEnableRequest): Promise<{ message: string }> {
    const response = await apiClient.post<{ message: string }>(
      API_CONFIG.ENDPOINTS.MFA.ENABLE,
      data
    );
    return response.data;
  },

  /**
   * Verify MFA code during login
   */
  async verifyMfa(data: MfaVerifyRequest): Promise<MfaVerifyResponse> {
    const response = await apiClient.post<MfaVerifyResponse>(
      API_CONFIG.ENDPOINTS.MFA.VERIFY,
      data
    );

    // Store tokens if verification successful
    if (response.data.valid && response.data.accessToken) {
      apiClient.setAuthTokens(response.data.accessToken, response.data.refreshToken!);
    }

    return response.data;
  },

  /**
   * Disable MFA
   */
  async disableMfa(): Promise<{ message: string }> {
    const response = await apiClient.post<{ message: string }>(
      API_CONFIG.ENDPOINTS.MFA.DISABLE
    );
    return response.data;
  },

  /**
   * Generate OTP (for SMS/Email methods)
   */
  async generateOtp(data: OtpGenerateRequest): Promise<OtpGenerateResponse> {
    const response = await apiClient.post<OtpGenerateResponse>(
      API_CONFIG.ENDPOINTS.MFA.GENERATE_OTP,
      data
    );
    return response.data;
  },

  /**
   * Regenerate backup codes
   */
  async regenerateBackupCodes(): Promise<BackupCodesResponse> {
    const response = await apiClient.post<BackupCodesResponse>(
      API_CONFIG.ENDPOINTS.MFA.BACKUP_CODES
    );
    return response.data;
  },
};

