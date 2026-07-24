/**
 * Form Validation Schemas using Zod
 * Aligned with backend validation rules
 */

import { z } from 'zod';
import { VALIDATION } from '../config/constants';

// Password schema with all requirements
export const passwordSchema = z
  .string()
  .min(VALIDATION.PASSWORD.MIN_LENGTH, `Password must be at least ${VALIDATION.PASSWORD.MIN_LENGTH} characters`)
  .max(VALIDATION.PASSWORD.MAX_LENGTH, `Password must not exceed ${VALIDATION.PASSWORD.MAX_LENGTH} characters`)
  .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
  .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
  .regex(/\d/, 'Password must contain at least one number')
  .regex(/[!@#$%^&*(),.?":{}|<>]/, 'Password must contain at least one special character');

// Username schema
export const usernameSchema = z
  .string()
  .min(VALIDATION.USERNAME.MIN_LENGTH, `Username must be at least ${VALIDATION.USERNAME.MIN_LENGTH} characters`)
  .max(VALIDATION.USERNAME.MAX_LENGTH, `Username must not exceed ${VALIDATION.USERNAME.MAX_LENGTH} characters`)
  .regex(VALIDATION.USERNAME.PATTERN, 'Username can only contain letters, numbers, underscores, and hyphens');

// Email schema
export const emailSchema = z
  .string()
  .email('Please enter a valid email address')
  .toLowerCase();

// Phone number schema (optional)
export const phoneSchema = z
  .string()
  .regex(/^\+?[\d\s\-()]+$/, 'Please enter a valid phone number')
  .optional()
  .or(z.literal(''));

// Login form schema
export const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
  rememberMe: z.boolean().optional(),
});

export type LoginFormData = z.infer<typeof loginSchema>;

// Registration form schema
export const registerSchema = z
  .object({
    username: usernameSchema,
    email: emailSchema,
    password: passwordSchema,
    confirmPassword: z.string(),
    firstName: z.string().min(1, 'First name is required').max(50, 'First name is too long'),
    lastName: z.string().min(1, 'Last name is required').max(50, 'Last name is too long'),
    phoneNumber: phoneSchema,
    acceptTerms: z.boolean().refine((val) => val === true, {
      message: 'You must accept the terms and conditions',
    }),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  });

export type RegisterFormData = z.infer<typeof registerSchema>;

// Forgot password form schema
export const forgotPasswordSchema = z.object({
  email: emailSchema,
});

export type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;

// Reset password form schema
export const resetPasswordSchema = z
  .object({
    password: passwordSchema,
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  });

export type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;

// MFA verification schema
export const mfaVerificationSchema = z.object({
  code: z.string().length(6, 'Verification code must be 6 digits').regex(/^\d+$/, 'Code must contain only numbers'),
});

export type MfaVerificationFormData = z.infer<typeof mfaVerificationSchema>;

// Change password schema
export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Current password is required'),
    newPassword: passwordSchema,
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  })
  .refine((data) => data.currentPassword !== data.newPassword, {
    message: 'New password must be different from current password',
    path: ['newPassword'],
  });

export type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;

// Profile update schema
export const profileUpdateSchema = z.object({
  firstName: z.string().min(1, 'First name is required').max(50),
  lastName: z.string().min(1, 'Last name is required').max(50),
  email: emailSchema,
  phoneNumber: phoneSchema,
});

export type ProfileUpdateFormData = z.infer<typeof profileUpdateSchema>;

