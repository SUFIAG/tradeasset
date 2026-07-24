/**
 * Forgot Password Page
 * Request password reset via email
 */

import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { KeyRound, Mail, ArrowLeft, CheckCircle } from 'lucide-react';
import { Button, Input, Card, CardBody, Alert } from '../../components/ui';
import { forgotPasswordSchema, type ForgotPasswordFormData } from '../../schemas/auth.schemas';
import { authService } from '../../services/auth.service';
import { ROUTES } from '../../config/constants';
import './Login.css';

export const ForgotPasswordPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const onSubmit = async (data: ForgotPasswordFormData) => {
    try {
      setIsLoading(true);

      await authService.forgotPassword({ email: data.email });
      setSuccess(true);
      toast.success('Password reset email sent! Check your inbox.');
    } catch (err: any) {
      toast.error(err.message || 'Failed to send reset email. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-wrapper">
        <div className="auth-brand">
          <div className="auth-logo">
            <KeyRound size={40} />
          </div>
          <h1 className="auth-brand-name">TradeSphere</h1>
          <p className="auth-brand-tagline">Trade Asset Management Platform</p>
        </div>

        <Card variant="elevated" padding="lg" className="auth-card">
          <CardBody>
            {!success ? (
              <>
                <div className="auth-header">
                  <h2 className="auth-title">Forgot Password?</h2>
                  <p className="auth-subtitle">
                    No worries, we'll send you reset instructions
                  </p>
                </div>


                <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
                  <Input
                    label="Email Address"
                    type="email"
                    placeholder="Enter your email address"
                    autoComplete="email"
                    leftIcon={<Mail size={18} />}
                    error={errors.email?.message}
                    helperText="We'll send a password reset link to this email"
                    fullWidth
                    {...register('email')}
                  />

                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    fullWidth
                    loading={isLoading}
                  >
                    Send Reset Link
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
                <h2 className="auth-title">Check Your Email</h2>
                <p className="auth-subtitle" style={{ marginBottom: 'var(--spacing-8)' }}>
                  We've sent a password reset link to your email address. Please check your inbox and
                  follow the instructions to reset your password.
                </p>
                <Alert
                  type="info"
                  message="Didn't receive the email? Check your spam folder or try again in a few minutes."
                />
              </div>
            )}

            <div className="auth-divider">
              <span>or</span>
            </div>

            <div className="auth-footer">
              <Link to={ROUTES.LOGIN} className="auth-link" style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--spacing-2)' }}>
                <ArrowLeft size={16} />
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

