/**
 * Authentication Service
 * Handles all authentication-related API calls
 */

import { apiClient } from '../lib/api-client';
import { API_CONFIG } from '../config/constants';
import type {
  LoginRequest,
  RegisterRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  AuthResponse,
  CaptchaChallenge,
  User,
} from '../types/auth.types';
import type { ApiResponse } from '../types/common.types';

export const authService = {
  /**
   * User Registration
   */
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>(
      API_CONFIG.ENDPOINTS.AUTH.REGISTER,
      data
    );
    return response.data;
  },

  /**
   * User Login
   */
  async login(data: LoginRequest): Promise<AuthResponse> {
    const loginData = {
      ...data,
      deviceFingerprint: apiClient.getDeviceFingerprint(),
    };

    const response = await apiClient.post<AuthResponse>(
      API_CONFIG.ENDPOINTS.AUTH.LOGIN,
      loginData
    );

    // Store tokens if login successful
    if (response.data.accessToken) {
      apiClient.setAuthTokens(response.data.accessToken, response.data.refreshToken);
    }

    return response.data;
  },

  /**
   * User Logout
   */
  async logout(): Promise<void> {
    try {
      await apiClient.post(API_CONFIG.ENDPOINTS.AUTH.LOGOUT);
    } finally {
      apiClient.clearTokens();
    }
  },

  /**
   * Forgot Password
   */
  async forgotPassword(data: ForgotPasswordRequest): Promise<ApiResponse<{ message: string }>> {
    const response = await apiClient.post<ApiResponse<{ message: string }>>(
      API_CONFIG.ENDPOINTS.AUTH.FORGOT_PASSWORD,
      data
    );
    return response.data;
  },

  /**
   * Reset Password
   */
  async resetPassword(data: ResetPasswordRequest): Promise<ApiResponse<{ message: string }>> {
    const response = await apiClient.post<ApiResponse<{ message: string }>>(
      API_CONFIG.ENDPOINTS.AUTH.RESET_PASSWORD,
      data
    );
    return response.data;
  },

  /**
   * Validate Reset Token
   */
  async validateResetToken(token: string): Promise<ApiResponse<{ valid: boolean }>> {
    const response = await apiClient.get<ApiResponse<{ valid: boolean }>>(
      `${API_CONFIG.ENDPOINTS.AUTH.VALIDATE_TOKEN}?token=${token}`
    );
    return response.data;
  },

  /**
   * Get Captcha Challenge
   */
  async getCaptcha(): Promise<CaptchaChallenge> {
    const response = await apiClient.get<CaptchaChallenge>(
      API_CONFIG.ENDPOINTS.AUTH.CAPTCHA_GENERATE
    );
    return response.data;
  },

  /**
   * Check if CAPTCHA is required
   */
  async isCaptchaRequired(): Promise<{ required: boolean; reason?: string }> {
    const response = await apiClient.get<{ required: boolean; reason?: string }>(
      API_CONFIG.ENDPOINTS.AUTH.CAPTCHA_REQUIRED
    );
    return response.data;
  },

  /**
   * Validate Captcha (included in login/register request)
   */
  validateCaptchaInRequest(challengeId: string, answer: string): { captchaChallengeId: string; captchaAnswer: string } {
    return {
      captchaChallengeId: challengeId,
      captchaAnswer: answer,
    };
  },

  /**
   * Get Current User Profile
   */
  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<User>('/api/auth/me');
    return response.data;
  },

  /**
   * Refresh Access Token
   */
  async refreshToken(refreshToken: string): Promise<AuthResponse> {
    const response = await apiClient.post<AuthResponse>(
      API_CONFIG.ENDPOINTS.AUTH.REFRESH,
      { refreshToken }
    );

    // Update stored tokens
    if (response.data.accessToken) {
      apiClient.setAuthTokens(response.data.accessToken, response.data.refreshToken);
    }

    return response.data;
  },
};

