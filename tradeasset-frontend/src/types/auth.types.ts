/**
 * Authentication Type Definitions
 * Aligned with backend DTOs and domain models
 */

export interface LoginRequest {
  username: string;
  password: string;
  deviceFingerprint?: string;
  captchaToken?: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  captchaToken?: string;
}

export interface ForgotPasswordRequest {
  email: string;
  captchaToken?: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface AuthResponse {
  message: string;
  userId: string;
  username: string;
  email: string;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  mfaRequired?: boolean;
  mfaToken?: string;
}

export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  roles: string[];
  status: UserStatus;
  mfaEnabled: boolean;
  emailVerified: boolean;
  createdAt: string;
  lastLoginAt?: string;
}

export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'PENDING_VERIFICATION';

export const UserStatus = {
  ACTIVE: 'ACTIVE' as const,
  INACTIVE: 'INACTIVE' as const,
  LOCKED: 'LOCKED' as const,
  PENDING_VERIFICATION: 'PENDING_VERIFICATION' as const,
};

export interface CaptchaChallenge {
  challengeId: string;
  question: string;  // Math question like "What is 5 + 3?"
  expiresAt?: string;
}

export interface CaptchaValidationRequest {
  challengeId: string;
  answer: string;  // User's answer to the question
}

export interface CaptchaRequiredResponse {
  required: boolean;
  reason?: string;
}

export interface MfaSetupResponse {
  secret: string;
  qrCodeUrl: string;
  backupCodes: string[];
}

export interface MfaVerificationRequest {
  code: string;
  mfaToken?: string;
}

export interface DeviceInfo {
  deviceId: string;
  deviceName: string;
  userAgent: string;
  ipAddress: string;
  trusted: boolean;
  lastUsed: string;
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  mfaRequired: boolean;
  mfaToken: string | null;
}

export interface PasswordValidation {
  hasMinLength: boolean;
  hasUppercase: boolean;
  hasLowercase: boolean;
  hasNumber: boolean;
  hasSpecialChar: boolean;
  isValid: boolean;
}

