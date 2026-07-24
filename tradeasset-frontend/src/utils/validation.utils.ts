/**
 * Validation Utilities
 * Helper functions for form validation
 */

import { VALIDATION } from '../config/constants';
import type { PasswordValidation } from '../types/auth.types';

/**
 * Validate password strength
 */
export const validatePassword = (password: string): PasswordValidation => {
  const hasMinLength = password.length >= VALIDATION.PASSWORD.MIN_LENGTH;
  const hasUppercase = /[A-Z]/.test(password);
  const hasLowercase = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

  const isValid =
    hasMinLength &&
    hasUppercase &&
    hasLowercase &&
    hasNumber &&
    hasSpecialChar;

  return {
    hasMinLength,
    hasUppercase,
    hasLowercase,
    hasNumber,
    hasSpecialChar,
    isValid,
  };
};

/**
 * Validate username format
 */
export const validateUsername = (username: string): boolean => {
  if (username.length < VALIDATION.USERNAME.MIN_LENGTH) return false;
  if (username.length > VALIDATION.USERNAME.MAX_LENGTH) return false;
  return VALIDATION.USERNAME.PATTERN.test(username);
};

/**
 * Validate email format
 */
export const validateEmail = (email: string): boolean => {
  return VALIDATION.EMAIL.PATTERN.test(email);
};

/**
 * Get password strength level
 */
export const getPasswordStrength = (password: string): {
  strength: 'weak' | 'fair' | 'good' | 'strong';
  percentage: number;
} => {
  const validation = validatePassword(password);
  let score = 0;

  if (validation.hasMinLength) score++;
  if (validation.hasUppercase) score++;
  if (validation.hasLowercase) score++;
  if (validation.hasNumber) score++;
  if (validation.hasSpecialChar) score++;

  // Additional points for length
  if (password.length >= 12) score++;
  if (password.length >= 16) score++;

  const percentage = (score / 7) * 100;

  let strength: 'weak' | 'fair' | 'good' | 'strong';
  if (percentage < 40) strength = 'weak';
  else if (percentage < 60) strength = 'fair';
  else if (percentage < 80) strength = 'good';
  else strength = 'strong';

  return { strength, percentage };
};

/**
 * Format error messages from API
 */
export const formatErrorMessage = (error: any): string => {
  if (typeof error === 'string') return error;
  if (error?.message) return error.message;
  if (error?.response?.data?.message) return error.response.data.message;
  if (error?.response?.data?.error) return error.response.data.error;
  return 'An unexpected error occurred';
};

/**
 * Sanitize user input
 */
export const sanitizeInput = (input: string): string => {
  return input.trim().replace(/[<>]/g, '');
};

/**
 * Check if password matches confirmation
 */
export const passwordsMatch = (password: string, confirmPassword: string): boolean => {
  return password === confirmPassword && password.length > 0;
};

/**
 * Validate phone number format (basic)
 */
export const validatePhoneNumber = (phone: string): boolean => {
  // Basic validation - adjust based on requirements
  const phoneRegex = /^\+?[\d\s\-()]+$/;
  return phoneRegex.test(phone) && phone.replace(/\D/g, '').length >= 10;
};

/**
 * Calculate password entropy
 */
export const calculatePasswordEntropy = (password: string): number => {
  let charset = 0;
  if (/[a-z]/.test(password)) charset += 26;
  if (/[A-Z]/.test(password)) charset += 26;
  if (/\d/.test(password)) charset += 10;
  if (/[^a-zA-Z0-9]/.test(password)) charset += 32;

  return password.length * Math.log2(charset);
};

/**
 * Format validation errors for display
 */
export const formatValidationErrors = (errors: any[]): string[] => {
  if (!Array.isArray(errors)) return [];
  return errors.map((error) => {
    if (typeof error === 'string') return error;
    if (error.message) return error.message;
    if (error.field && error.message) return `${error.field}: ${error.message}`;
    return 'Validation error';
  });
};

/**
 * Debounce function for input validation
 */
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  delay: number
): ((...args: Parameters<T>) => void) => {
  let timeoutId: ReturnType<typeof setTimeout>;
  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delay);
  };
};

