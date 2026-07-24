/**
 * MFA Setup Page
 * Allow users to set up Multi-Factor Authentication
 */

import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { Smartphone, Mail, Key, Copy, Check } from 'lucide-react';
import { Button, Input, Card, CardHeader, CardBody, Alert } from '../../components/ui';
import { mfaService } from '../../services/mfa.service';
import { MfaMethod } from '../../types/mfa.types';
import type { MfaSetupResponse } from '../../types/mfa.types';
import { ROUTES } from '../../config/constants';
import './MfaSetup.css';

const mfaEnableSchema = z.object({
  code: z.string()
    .length(6, 'Code must be 6 digits')
    .regex(/^\d+$/, 'Code must contain only numbers'),
});

type MfaEnableFormData = z.infer<typeof mfaEnableSchema>;

export const MfaSetupPage: React.FC = () => {
  const navigate = useNavigate();
  const [selectedMethod, setSelectedMethod] = useState<MfaMethod | null>(null);
  const [setupResponse, setSetupResponse] = useState<MfaSetupResponse | null>(null);
  const [phoneNumber, setPhoneNumber] = useState('');
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copiedCodes, setCopiedCodes] = useState<Set<number>>(new Set());
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<MfaEnableFormData>({
    resolver: zodResolver(mfaEnableSchema),
  });

  const handleMethodSelect = async (method: MfaMethod) => {
    setSelectedMethod(method);
    setError(null);

    if (method === MfaMethod.TOTP) {
      // TOTP doesn't need phone/email
      await initiateSetup(method);
    }
  };

  const initiateSetup = async (method: MfaMethod) => {
    try {
      setIsLoading(true);
      setError(null);

      const setupData: any = { method };
      if (method === MfaMethod.SMS && phoneNumber) {
        setupData.phoneNumber = phoneNumber;
      } else if (method === MfaMethod.EMAIL && email) {
        setupData.email = email;
      }

      const response = await mfaService.setupMfa(setupData);
      setSetupResponse(response);
    } catch (err: any) {
      setError(err.message || 'Failed to setup MFA');
    } finally {
      setIsLoading(false);
    }
  };

  const onSubmit = async (data: MfaEnableFormData) => {
    try {
      setIsLoading(true);
      setError(null);

      await mfaService.enableMfa({ code: data.code });
      setSuccess(true);

      // Navigate to dashboard after 2 seconds
      setTimeout(() => {
        navigate(ROUTES.DASHBOARD);
      }, 2000);
    } catch (err: any) {
      setError(err.message || 'Failed to enable MFA. Please check your code.');
    } finally {
      setIsLoading(false);
    }
  };

  const copyToClipboard = (text: string, index: number) => {
    navigator.clipboard.writeText(text);
    setCopiedCodes(prev => new Set(prev).add(index));
    setTimeout(() => {
      setCopiedCodes(prev => {
        const newSet = new Set(prev);
        newSet.delete(index);
        return newSet;
      });
    }, 2000);
  };

  if (success) {
    return (
      <div className="mfa-setup-container">
        <div className="mfa-setup-wrapper">
          <Card variant="elevated" padding="lg">
            <CardBody>
              <div className="mfa-setup-success">
                <div className="mfa-success-icon">
                  <Check size={48} />
                </div>
                <h2>MFA Enabled Successfully!</h2>
                <p>Your account is now protected with two-factor authentication.</p>
              </div>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  if (!selectedMethod) {
    return (
      <div className="mfa-setup-container">
        <div className="mfa-setup-wrapper">
          <Card variant="elevated" padding="lg">
            <CardHeader
              title="Set Up Two-Factor Authentication"
              subtitle="Choose your preferred method for receiving verification codes"
            />
            <CardBody>
              <div className="mfa-methods">
                <button
                  className="mfa-method-card"
                  onClick={() => handleMethodSelect(MfaMethod.TOTP)}
                >
                  <div className="mfa-method-icon">
                    <Smartphone size={32} />
                  </div>
                  <h3>Authenticator App</h3>
                  <p>Use Google Authenticator, Authy, or similar apps (Recommended)</p>
                </button>

                <button
                  className="mfa-method-card"
                  onClick={() => handleMethodSelect(MfaMethod.SMS)}
                >
                  <div className="mfa-method-icon">
                    <Smartphone size={32} />
                  </div>
                  <h3>SMS Text Message</h3>
                  <p>Receive codes via text message to your phone</p>
                </button>

                <button
                  className="mfa-method-card"
                  onClick={() => handleMethodSelect(MfaMethod.EMAIL)}
                >
                  <div className="mfa-method-icon">
                    <Mail size={32} />
                  </div>
                  <h3>Email</h3>
                  <p>Receive codes via email</p>
                </button>
              </div>

              <div className="mfa-setup-footer">
                <Button
                  variant="ghost"
                  onClick={() => navigate(ROUTES.DASHBOARD)}
                >
                  Skip for Now
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  if (selectedMethod === MfaMethod.SMS && !setupResponse) {
    return (
      <div className="mfa-setup-container">
        <div className="mfa-setup-wrapper">
          <Card variant="elevated" padding="lg">
            <CardHeader title="Enter Phone Number" />
            <CardBody>
              {error && <Alert type="error" message={error} />}

              <div className="mfa-input-form">
                <Input
                  label="Phone Number"
                  type="tel"
                  placeholder="+1 (555) 000-0000"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  fullWidth
                />
                <Button
                  variant="primary"
                  size="lg"
                  fullWidth
                  onClick={() => initiateSetup(MfaMethod.SMS)}
                  loading={isLoading}
                  disabled={!phoneNumber}
                >
                  Continue
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  if (selectedMethod === MfaMethod.EMAIL && !setupResponse) {
    return (
      <div className="mfa-setup-container">
        <div className="mfa-setup-wrapper">
          <Card variant="elevated" padding="lg">
            <CardHeader title="Enter Email Address" />
            <CardBody>
              {error && <Alert type="error" message={error} />}

              <div className="mfa-input-form">
                <Input
                  label="Email Address"
                  type="email"
                  placeholder="your.email@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  fullWidth
                />
                <Button
                  variant="primary"
                  size="lg"
                  fullWidth
                  onClick={() => initiateSetup(MfaMethod.EMAIL)}
                  loading={isLoading}
                  disabled={!email}
                >
                  Continue
                </Button>
              </div>
            </CardBody>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="mfa-setup-container">
      <div className="mfa-setup-wrapper">
        <Card variant="elevated" padding="lg">
          <CardHeader title="Complete MFA Setup" />
          <CardBody>
            {error && <Alert type="error" message={error} />}

            {setupResponse && (
              <>
                {selectedMethod === MfaMethod.TOTP && (
                  <div className="mfa-qr-section">
                    <p className="mfa-instruction">
                      Scan this QR code with your authenticator app:
                    </p>
                    <div className="mfa-qr-code">
                      <img src={setupResponse.qrCodeUrl} alt="QR Code" />
                    </div>
                    <div className="mfa-secret">
                      <p>Or enter this code manually:</p>
                      <code>{setupResponse.secret}</code>
                      <Button
                        variant="ghost"
                        size="sm"
                        leftIcon={<Copy size={14} />}
                        onClick={() => copyToClipboard(setupResponse.secret, -1)}
                      >
                        Copy
                      </Button>
                    </div>
                  </div>
                )}

                <div className="mfa-backup-codes">
                  <h3>
                    <Key size={20} />
                    Backup Codes
                  </h3>
                  <p>Save these codes in a safe place. You can use them to access your account if you lose your device.</p>
                  <div className="backup-codes-grid">
                    {setupResponse.backupCodes.map((code, index) => (
                      <div key={index} className="backup-code">
                        <code>{code}</code>
                        <button
                          onClick={() => copyToClipboard(code, index)}
                          className="backup-code-copy"
                        >
                          {copiedCodes.has(index) ? (
                            <Check size={14} />
                          ) : (
                            <Copy size={14} />
                          )}
                        </button>
                      </div>
                    ))}
                  </div>
                </div>

                <form onSubmit={handleSubmit(onSubmit)} className="mfa-verify-form">
                  <Input
                    label="Enter Verification Code"
                    type="text"
                    placeholder="000000"
                    maxLength={6}
                    error={errors.code?.message}
                    helperText="Enter the 6-digit code from your authenticator app"
                    fullWidth
                    {...register('code')}
                  />

                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    fullWidth
                    loading={isLoading}
                  >
                    Enable MFA
                  </Button>
                </form>
              </>
            )}
          </CardBody>
        </Card>
      </div>
    </div>
  );
};

