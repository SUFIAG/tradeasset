/**
 * MFA Verification Component
 * For verifying MFA code during login
 */

import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Shield, RefreshCw } from 'lucide-react';
import { Button, Input, Card, CardBody, Alert } from '../ui';
import { mfaService } from '../../services/mfa.service';
import { MfaMethod } from '../../types/mfa.types';
import './MfaVerification.css';

const mfaVerifySchema = z.object({
  code: z.string()
    .length(6, 'Code must be 6 digits')
    .regex(/^\d+$/, 'Code must contain only numbers'),
});

type MfaVerifyFormData = z.infer<typeof mfaVerifySchema>;

export interface MfaVerificationProps {
  mfaToken: string;
  onSuccess: (accessToken: string, refreshToken: string) => void;
  onCancel: () => void;
}

export const MfaVerification: React.FC<MfaVerificationProps> = ({
  mfaToken,
  onSuccess,
  onCancel,
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isGenerating, setIsGenerating] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<MfaVerifyFormData>({
    resolver: zodResolver(mfaVerifySchema),
  });

  const onSubmit = async (data: MfaVerifyFormData) => {
    try {
      setIsLoading(true);
      setError(null);

      const response = await mfaService.verifyMfa({
        code: data.code,
        mfaToken,
      });

      if (response.valid && response.accessToken && response.refreshToken) {
        onSuccess(response.accessToken, response.refreshToken);
      } else {
        setError(response.message || 'Invalid verification code');
      }
    } catch (err: any) {
      setError(err.message || 'Verification failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleResendCode = async (method: MfaMethod) => {
    try {
      setIsGenerating(true);
      setError(null);

      await mfaService.generateOtp({ method });
      setError(null);
      // Show success message
      alert('A new code has been sent');
    } catch (err: any) {
      setError(err.message || 'Failed to send code. Please try again.');
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="mfa-verification-container">
      <Card variant="elevated" padding="lg">
        <CardBody>
          <div className="mfa-verification-header">
            <div className="mfa-verification-icon">
              <Shield size={48} />
            </div>
            <h2 className="mfa-verification-title">Two-Factor Authentication</h2>
            <p className="mfa-verification-subtitle">
              Enter the 6-digit code from your authenticator app or check your email/SMS
            </p>
          </div>

          {error && <Alert type="error" message={error} />}

          <form onSubmit={handleSubmit(onSubmit)} className="mfa-verification-form">
            <Input
              label="Verification Code"
              type="text"
              placeholder="000000"
              maxLength={6}
              autoComplete="one-time-code"
              error={errors.code?.message}
              fullWidth
              {...register('code')}
            />

            <div className="mfa-verification-actions">
              <Button
                type="submit"
                variant="primary"
                size="lg"
                fullWidth
                loading={isLoading}
              >
                Verify Code
              </Button>

              <Button
                type="button"
                variant="ghost"
                size="md"
                fullWidth
                onClick={onCancel}
                disabled={isLoading}
              >
                Cancel
              </Button>
            </div>
          </form>

          <div className="mfa-verification-resend">
            <p>Didn't receive a code?</p>
            <div className="mfa-resend-buttons">
              <Button
                type="button"
                variant="outline"
                size="sm"
                leftIcon={<RefreshCw size={14} />}
                onClick={() => handleResendCode(MfaMethod.EMAIL)}
                disabled={isGenerating}
              >
                Resend Email
              </Button>
              <Button
                type="button"
                variant="outline"
                size="sm"
                leftIcon={<RefreshCw size={14} />}
                onClick={() => handleResendCode(MfaMethod.SMS)}
                disabled={isGenerating}
              >
                Resend SMS
              </Button>
            </div>
          </div>
        </CardBody>
      </Card>
    </div>
  );
};

