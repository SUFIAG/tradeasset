/**
 * Password Strength Indicator Component
 * Visual indicator for password strength
 */

import React from 'react';
import { Check, X } from 'lucide-react';
import { validatePassword, getPasswordStrength } from '../../../utils/validation.utils';
import './PasswordStrengthIndicator.css';

export interface PasswordStrengthIndicatorProps {
  password: string;
  showRequirements?: boolean;
}

export const PasswordStrengthIndicator: React.FC<PasswordStrengthIndicatorProps> = ({
  password,
  showRequirements = true,
}) => {
  const validation = validatePassword(password);
  const { strength, percentage } = getPasswordStrength(password);

  if (!password) return null;

  const strengthColors = {
    weak: '#ef4444',
    fair: '#f59e0b',
    good: '#10b981',
    strong: '#22c55e',
  };

  const strengthLabels = {
    weak: 'Weak',
    fair: 'Fair',
    good: 'Good',
    strong: 'Strong',
  };

  return (
    <div className="password-strength">
      <div className="password-strength-bar-container">
        <div
          className="password-strength-bar"
          style={{
            width: `${percentage}%`,
            backgroundColor: strengthColors[strength],
          }}
        />
      </div>
      <div className="password-strength-label" style={{ color: strengthColors[strength] }}>
        {strengthLabels[strength]}
      </div>

      {showRequirements && (
        <div className="password-requirements">
          <RequirementItem
            label="At least 8 characters"
            met={validation.hasMinLength}
          />
          <RequirementItem
            label="One uppercase letter"
            met={validation.hasUppercase}
          />
          <RequirementItem
            label="One lowercase letter"
            met={validation.hasLowercase}
          />
          <RequirementItem label="One number" met={validation.hasNumber} />
          <RequirementItem
            label="One special character"
            met={validation.hasSpecialChar}
          />
        </div>
      )}
    </div>
  );
};

interface RequirementItemProps {
  label: string;
  met: boolean;
}

const RequirementItem: React.FC<RequirementItemProps> = ({ label, met }) => {
  return (
    <div className={`password-requirement ${met ? 'met' : 'unmet'}`}>
      <span className="password-requirement-icon">
        {met ? <Check size={14} /> : <X size={14} />}
      </span>
      <span className="password-requirement-label">{label}</span>
    </div>
  );
};

