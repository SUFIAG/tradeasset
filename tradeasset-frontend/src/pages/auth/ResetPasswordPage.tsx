/**
 * Reset Password Page
 * Set new password using reset token from email
 */

import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Eye, EyeOff, Lock, CheckCircle, XCircle } from 'lucide-react';
import { Button, Input, Card, CardBody, Alert, PasswordStrengthIndicator } from '../../components/ui';
import { resetPasswordSchema, type ResetPasswordFormData } from '../../schemas/auth.schemas';
import { authService } from '../../services/auth.service';
import { ROUTES } from '../../config/constants';
import './Login.css';

export const ResetPasswordPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isValidating, setIsValidating] = useState(true);
  const [tokenValid, setTokenValid] = useState(false);
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
  });

  const password = watch('password');

  useEffect(() => {
    const validateToken = async () => {
      if (!token) {
        setTokenValid(false);
        setIsValidating(false);
        toast.error('Invalid reset link. Please request a new password reset.');
        return;
      }

      try {
        const response = await authService.validateResetToken(token);
        setTokenValid(response.data?.valid || false);
        if (!response.data?.valid) {
          toast.error('This reset link has expired or is invalid. Please request a new one.');
        }
      } catch (err) {
        setTokenValid(false);
        toast.error('Failed to validate reset link. Please try again.');
      } finally {
        setIsValidating(false);
      }
    };

    validateToken();
  }, [token]);

  const onSubmit = async (data: ResetPasswordFormData) => {
    if (!token) return;

    try {
      setIsLoading(true);

      await authService.resetPassword({
        token,
        newPassword: data.password,
        confirmPassword: data.confirmPassword,
      });

      setSuccess(true);
      toast.success('Password reset successful! Redirecting to login...');

      // Redirect to login after 3 seconds
      setTimeout(() => {
        navigate(ROUTES.LOGIN);
      }, 3000);
    } catch (err: any) {
      toast.error(err.message || 'Failed to reset password. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  if (isValidating) {
    return (
      <div className="auth-container">
        <div className="auth-wrapper">
          <Card variant="elevated" padding="lg" className="auth-card">
            <CardBody>
              <div style={{ textAlign: 'center', padding: 'var(--spacing-8)' }}>
                <div className="animate-spin" style={{ margin: '0 auto', width: '40px', height: '40px' }}>
                  <svg width="40" height="40" viewBox="0 0 24 24" fill="none">
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    />
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    />
                  </svg>
                </div>
                <p style={{ marginTop: 'var(--spacing-4)', color: 'var(--color-text-secondary)' }}>
                  Validating reset link...
                </p>
              </div>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  if (!tokenValid) {
    return (
      <div className="auth-container">
        <div className="auth-wrapper">
          <Card variant="elevated" padding="lg" className="auth-card">
            <CardBody>
              <div style={{ textAlign: 'center' }}>
                <div
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    width: '80px',
                    height: '80px',
                    backgroundColor: 'var(--color-error)',
                    borderRadius: '50%',
                    marginBottom: 'var(--spacing-6)',
                  }}
                >
                  <XCircle size={40} color="white" />
                </div>
                <h2 className="auth-title">Invalid Reset Link</h2>
                <p className="auth-subtitle" style={{ marginBottom: 'var(--spacing-6)' }}>
                  This reset link has expired or is invalid. Please request a new password reset.
                </p>
                <Button
                  variant="primary"
                  size="lg"
                  onClick={() => navigate(ROUTES.FORGOT_PASSWORD)}
                >
                  Request New Reset Link
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-container">
      <div className="auth-wrapper">
        <div className="auth-brand">
          <div className="auth-logo">
            <Lock size={40} />
          </div>
          <h1 className="auth-brand-name">TradeSphere</h1>
          <p className="auth-brand-tagline">Trade Asset Management Platform</p>
        </div>

        <Card variant="elevated" padding="lg" className="auth-card">
          <CardBody>
            {!success ? (
              <>
                <div className="auth-header">
                  <h2 className="auth-title">Reset Password</h2>
                  <p className="auth-subtitle">Enter your new password below</p>
                </div>


                <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
                  <div>
                    <Input
                      label="New Password"
                      type={showPassword ? 'text' : 'password'}
                      placeholder="Create a strong password"
                      autoComplete="new-password"
                      leftIcon={<Lock size={18} />}
                      rightIcon={
                        <button
                          type="button"
                          onClick={() => setShowPassword(!showPassword)}
                          className="password-toggle"
                          aria-label={showPassword ? 'Hide password' : 'Show password'}
                        >
                          {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                        </button>
                      }
                      error={errors.password?.message}
                      fullWidth
                      {...register('password')}
                    />
                    <PasswordStrengthIndicator password={password || ''} />
                  </div>

                  <Input
                    label="Confirm New Password"
                    type={showConfirmPassword ? 'text' : 'password'}
                    placeholder="Re-enter your password"
                    autoComplete="new-password"
                    leftIcon={<Lock size={18} />}
                    rightIcon={
                      <button
                        type="button"
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        className="password-toggle"
                        aria-label={showConfirmPassword ? 'Hide password' : 'Show password'}
                      >
                        {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                      </button>
                    }
                    error={errors.confirmPassword?.message}
                    fullWidth
                    {...register('confirmPassword')}
                  />

                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    fullWidth
                    loading={isLoading}
                  >
                    Reset Password
                  </Button>
                </form>
              </>
            ) : (
              <div style={{ textAlign: 'center' }}>
                <div
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    width: '80px',
                    height: '80px',
                    backgroundColor: 'var(--color-success)',
                    borderRadius: '50%',
                    marginBottom: 'var(--spacing-6)',
                  }}
                >
                  <CheckCircle size={40} color="white" />
                </div>
                <h2 className="auth-title">Password Reset Successful</h2>
                <p className="auth-subtitle" style={{ marginBottom: 'var(--spacing-6)' }}>
                  Your password has been reset successfully. You can now sign in with your new password.
                </p>
                <Alert
                  type="info"
                  message="Redirecting to login page..."
                />
              </div>
            )}

            <div className="auth-divider">
              <span>or</span>
            </div>

            <div className="auth-footer">
              <Link to={ROUTES.LOGIN} className="auth-link">
                Back to Sign In
              </Link>
            </div>
          </CardBody>
        </Card>

        <div className="auth-footer-text">
          <p>&copy; 2026 TradeSphere Inc. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

