/**
 * CAPTCHA Component
 * Math-based CAPTCHA challenge for authentication forms
 */

import React, { useState, useEffect } from 'react';
import { RefreshCw, AlertCircle } from 'lucide-react';
import { Input, Button } from '../ui';
import { authService } from '../../services/auth.service';
import type { CaptchaChallenge } from '../../types/auth.types';
import './Captcha.css';

export interface CaptchaProps {
  onCaptchaChange: (challengeId: string, answer: string) => void;
  error?: string;
  required?: boolean;
}

export const Captcha: React.FC<CaptchaProps> = ({ onCaptchaChange, error, required = false }) => {
  const [challenge, setChallenge] = useState<CaptchaChallenge | null>(null);
  const [answer, setAnswer] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  const loadCaptcha = async () => {
    try {
      setIsLoading(true);
      setLoadError(null);
      const captcha = await authService.getCaptcha();
      setChallenge(captcha);
      setAnswer('');
      onCaptchaChange(captcha.challengeId, '');
    } catch (err: any) {
      setLoadError('CAPTCHA service is currently unavailable');
      console.error('Failed to load CAPTCHA:', err);
      // Don't block the user - CAPTCHA is optional when backend is not available
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (required) {
      loadCaptcha();
    }
  }, [required]);

  const handleAnswerChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setAnswer(value);
    if (challenge) {
      onCaptchaChange(challenge.challengeId, value);
    }
  };

  if (!required) {
    return null;
  }

  return (
    <div className="captcha-container">
      <div className="captcha-header">
        <label className="captcha-label">
          Security Verification <span className="captcha-required">*</span>
        </label>
        <Button
          type="button"
          variant="ghost"
          size="sm"
          onClick={loadCaptcha}
          disabled={isLoading}
          leftIcon={<RefreshCw size={14} className={isLoading ? 'animate-spin' : ''} />}
        >
          Refresh
        </Button>
      </div>

      {loadError && (
        <div className="captcha-error">
          <AlertCircle size={16} />
          <span>{loadError}</span>
        </div>
      )}

      {challenge && (
        <>
          <div className="captcha-question">
            <div className="captcha-question-text">{challenge.question}</div>
          </div>

          <Input
            type="text"
            placeholder="Enter your answer"
            value={answer}
            onChange={handleAnswerChange}
            error={error}
            autoComplete="off"
            fullWidth
          />
        </>
      )}

      <p className="captcha-help-text">
        Please solve the math problem above to verify you're human
      </p>
    </div>
  );
};

