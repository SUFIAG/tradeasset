/**
 * Input Component
 * Reusable input field with validation support
 */

import React, { forwardRef } from 'react';
import './Input.css';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  fullWidth?: boolean;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      label,
      error,
      helperText,
      leftIcon,
      rightIcon,
      fullWidth = false,
      className = '',
      id,
      ...props
    },
    ref
  ) => {
    const inputId = id || `input-${Math.random().toString(36).substr(2, 9)}`;
    const hasError = !!error;

    const containerClasses = ['input-container', fullWidth && 'input-full-width']
      .filter(Boolean)
      .join(' ');

    const wrapperClasses = [
      'input-wrapper',
      hasError && 'input-error',
      props.disabled && 'input-disabled',
      leftIcon && 'input-with-left-icon',
      rightIcon && 'input-with-right-icon',
    ]
      .filter(Boolean)
      .join(' ');

    return (
      <div className={containerClasses}>
        {label && (
          <label htmlFor={inputId} className="input-label">
            {label}
            {props.required && <span className="input-required">*</span>}
          </label>
        )}
        <div className={wrapperClasses}>
          {leftIcon && <span className="input-icon-left">{leftIcon}</span>}
          <input
            ref={ref}
            id={inputId}
            className={`input ${className}`}
            {...props}
          />
          {rightIcon && <span className="input-icon-right">{rightIcon}</span>}
        </div>
        {error && <span className="input-error-text">{error}</span>}
        {helperText && !error && <span className="input-helper-text">{helperText}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';

