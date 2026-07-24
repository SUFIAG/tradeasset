/**
 * Login Page
 * User authentication with username/password, CAPTCHA, and MFA support
 */

import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Eye, EyeOff, LogIn, User, Lock } from 'lucide-react';
import { Button, Input, Card, CardBody } from '../../components/ui';
import { Captcha } from '../../components/auth/Captcha';
import { MfaVerification } from '../../components/auth/MfaVerification';
import { loginSchema, type LoginFormData } from '../../schemas/auth.schemas';
import { useAuthStore } from '../../store/auth.store';
import { authService } from '../../services/auth.service';
import { ROUTES } from '../../config/constants';
import './Login.css';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login, isLoading, mfaRequired, mfaToken, setTokens } = useAuthStore();

  const [showPassword, setShowPassword] = useState(false);
  const [captchaRequired, setCaptchaRequired] = useState(false);
  const [captchaData, setCaptchaData] = useState<{ challengeId: string; answer: string }>({ challengeId: '', answer: '' });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  // Check if CAPTCHA is required on mount
  useEffect(() => {
    const checkCaptcha = async () => {
      try {
        const result = await authService.isCaptchaRequired();
        setCaptchaRequired(result.required);
      } catch (err) {
        // If backend is not available, don't require CAPTCHA
        console.log('CAPTCHA check skipped - backend not available');
        setCaptchaRequired(false);
      }
    };
    checkCaptcha();
  }, []);

  const handleCaptchaChange = (challengeId: string, answer: string) => {
    setCaptchaData({ challengeId, answer });
  };

  const onSubmit = async (data: LoginFormData) => {
    try {
      const loginData: any = {
        username: data.username,
        password: data.password,
      };

      // Add CAPTCHA data if required and provided
      if (captchaRequired && captchaData.challengeId && captchaData.answer) {
        loginData.captchaChallengeId = captchaData.challengeId;
        loginData.captchaAnswer = captchaData.answer;
      }

      await login(loginData);

      // If MFA not required, navigate to dashboard
      if (!mfaRequired) {
        toast.success('Login successful! Welcome back.');
        navigate(ROUTES.DASHBOARD);
      }
    } catch (err: any) {
      toast.error(err.message || 'Login failed. Please check your credentials.');
      // Reload CAPTCHA on error if it was required
      if (captchaRequired) {
        setCaptchaData({ challengeId: '', answer: '' });
      }
    }
  };

  const handleMfaSuccess = (accessToken: string, refreshToken: string) => {
    setTokens(accessToken, refreshToken);
    navigate(ROUTES.DASHBOARD);
  };

  const handleMfaCancel = () => {
    useAuthStore.getState().setMfaRequired(false);
  };

  // Show MFA verification if required
  if (mfaRequired && mfaToken) {
    return (
      <div className="auth-container">
        <div className="auth-wrapper">
          <div className="auth-brand">
            <div className="auth-logo">
              <LogIn size={40} />
            </div>
            <h1 className="auth-brand-name">TradeSphere</h1>
            <p className="auth-brand-tagline">Trade Asset Management Platform</p>
          </div>

          <MfaVerification
            mfaToken={mfaToken}
            onSuccess={handleMfaSuccess}
            onCancel={handleMfaCancel}
          />

          <div className="auth-footer-text">
            <p>&copy; 2026 TradeSphere Inc. All rights reserved.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-container">
      <div className="auth-wrapper">
        <div className="auth-brand">
          <div className="auth-logo">
            <LogIn size={40} />
          </div>
          <h1 className="auth-brand-name">TradeSphere</h1>
          <p className="auth-brand-tagline">Trade Asset Management Platform</p>
        </div>

        <Card variant="elevated" padding="lg" className="auth-card">
          <CardBody>
            <div className="auth-header">
              <h2 className="auth-title">Welcome Back</h2>
              <p className="auth-subtitle">Sign in to your account to continue</p>
            </div>


            <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
              <Input
                label="Username"
                type="text"
                placeholder="Enter your username"
                autoComplete="username"
                leftIcon={<User size={18} />}
                error={errors.username?.message}
                fullWidth
                {...register('username')}
              />

              <Input
                label="Password"
                type={showPassword ? 'text' : 'password'}
                placeholder="Enter your password"
                autoComplete="current-password"
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

              {captchaRequired && (
                <Captcha
                  onCaptchaChange={handleCaptchaChange}
                  required={captchaRequired}
                />
              )}

              <div className="auth-options">
                <label className="auth-checkbox">
                  <input type="checkbox" {...register('rememberMe')} />
                  <span>Remember me</span>
                </label>
                <Link to={ROUTES.FORGOT_PASSWORD} className="auth-link">
                  Forgot password?
                </Link>
              </div>

              <Button
                type="submit"
                variant="primary"
                size="lg"
                fullWidth
                loading={isLoading}
              >
                Sign In
              </Button>
            </form>

            <div className="auth-divider">
              <span>or</span>
            </div>

            <div className="auth-footer">
              <p>
                Don't have an account?{' '}
                <Link to={ROUTES.REGISTER} className="auth-link">
                  Sign up
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

