/**
 * Register Page
 * User registration with password strength validation and CAPTCHA
 */

import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Eye, EyeOff, UserPlus, User, Mail, Lock, Phone } from 'lucide-react';
import { Button, Input, Card, CardBody, PasswordStrengthIndicator } from '../../components/ui';
import { Captcha } from '../../components/auth/Captcha';
import { registerSchema, type RegisterFormData } from '../../schemas/auth.schemas';
import { useAuthStore } from '../../store/auth.store';
import { authService } from '../../services/auth.service';
import { ROUTES } from '../../config/constants';
import './Login.css';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const { register: registerUser, isLoading } = useAuthStore();

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [captchaRequired, setCaptchaRequired] = useState(false);
  const [captchaData, setCaptchaData] = useState<{ challengeId: string; answer: string }>({ challengeId: '', answer: '' });

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const password = watch('password');

  // Check if CAPTCHA is required on mount
  useEffect(() => {
    const checkCaptcha = async () => {
      try {
        const result = await authService.isCaptchaRequired();
        setCaptchaRequired(result.required);
      } catch (err) {
        // If backend is not available, don't require CAPTCHA by default
        console.log('CAPTCHA check skipped - backend not available');
        setCaptchaRequired(false);
      }
    };
    checkCaptcha();
  }, []);

  const handleCaptchaChange = (challengeId: string, answer: string) => {
    setCaptchaData({ challengeId, answer });
  };

  const onSubmit = async (data: RegisterFormData) => {
    try {
      const registerData: any = {
        username: data.username,
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
        phoneNumber: data.phoneNumber,
      };

      // Add CAPTCHA data if required and provided
      if (captchaRequired && captchaData.challengeId && captchaData.answer) {
        registerData.captchaChallengeId = captchaData.challengeId;
        registerData.captchaAnswer = captchaData.answer;
      }

      await registerUser(registerData);
      toast.success('Registration successful! Welcome to TradeSphere.');
      navigate(ROUTES.DASHBOARD);
    } catch (err: any) {
      toast.error(err.message || 'Registration failed. Please try again.');
      // Reload CAPTCHA on error if it was required
      if (captchaRequired) {
        setCaptchaData({ challengeId: '', answer: '' });
      }
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-wrapper" style={{ maxWidth: '520px' }}>
        <div className="auth-brand">
          <div className="auth-logo">
            <UserPlus size={40} />
          </div>
          <h1 className="auth-brand-name">TradeSphere</h1>
          <p className="auth-brand-tagline">Trade Asset Management Platform</p>
        </div>

        <Card variant="elevated" padding="lg" className="auth-card">
          <CardBody>
            <div className="auth-header">
              <h2 className="auth-title">Create Account</h2>
              <p className="auth-subtitle">Sign up to get started with TradeSphere</p>
            </div>


            <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--spacing-4)' }}>
                <Input
                  label="First Name"
                  type="text"
                  placeholder="John"
                  autoComplete="given-name"
                  error={errors.firstName?.message}
                  fullWidth
                  {...register('firstName')}
                />

                <Input
                  label="Last Name"
                  type="text"
                  placeholder="Doe"
                  autoComplete="family-name"
                  error={errors.lastName?.message}
                  fullWidth
                  {...register('lastName')}
                />
              </div>

              <Input
                label="Username"
                type="text"
                placeholder="johndoe"
                autoComplete="username"
                leftIcon={<User size={18} />}
                error={errors.username?.message}
                fullWidth
                {...register('username')}
              />

              <Input
                label="Email Address"
                type="email"
                placeholder="john.doe@example.com"
                autoComplete="email"
                leftIcon={<Mail size={18} />}
                error={errors.email?.message}
                fullWidth
                {...register('email')}
              />

              <Input
                label="Phone Number (Optional)"
                type="tel"
                placeholder="+1 (555) 000-0000"
                autoComplete="tel"
                leftIcon={<Phone size={18} />}
                error={errors.phoneNumber?.message}
                fullWidth
                {...register('phoneNumber')}
              />

              <div>
                <Input
                  label="Password"
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
                label="Confirm Password"
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

              {captchaRequired && (
                <Captcha
                  onCaptchaChange={handleCaptchaChange}
                  required={captchaRequired}
                />
              )}

              <label className="auth-checkbox">
                <input type="checkbox" {...register('acceptTerms')} />
                <span style={{ fontSize: '0.875rem' }}>
                  I agree to the{' '}
                  <Link to="/terms" className="auth-link">
                    Terms of Service
                  </Link>{' '}
                  and{' '}
                  <Link to="/privacy" className="auth-link">
                    Privacy Policy
                  </Link>
                </span>
              </label>
              {errors.acceptTerms && (
                <span style={{ color: 'var(--color-error)', fontSize: '0.875rem', marginTop: '-12px' }}>
                  {errors.acceptTerms.message}
                </span>
              )}

              <Button
                type="submit"
                variant="primary"
                size="lg"
                fullWidth
                loading={isLoading}
              >
                Create Account
              </Button>
            </form>

            <div className="auth-divider">
              <span>or</span>
            </div>

            <div className="auth-footer">
              <p>
                Already have an account?{' '}
                <Link to={ROUTES.LOGIN} className="auth-link">
                  Sign in
                </Link>
              </p>
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

