/**
 * Alert Component
 * Display contextual feedback messages
 */

import React from 'react';
import { X, CheckCircle, AlertCircle, AlertTriangle, Info } from 'lucide-react';
import './Alert.css';

export interface AlertProps {
  type?: 'success' | 'error' | 'warning' | 'info';
  title?: string;
  message: string;
  onClose?: () => void;
  className?: string;
}

export const Alert: React.FC<AlertProps> = ({
  type = 'info',
  title,
  message,
  onClose,
  className = '',
}) => {
  const icons = {
    success: <CheckCircle size={20} />,
    error: <AlertCircle size={20} />,
    warning: <AlertTriangle size={20} />,
    info: <Info size={20} />,
  };

  const classes = ['alert', `alert-${type}`, className].filter(Boolean).join(' ');

  return (
    <div className={classes} role="alert">
      <div className="alert-icon">{icons[type]}</div>
      <div className="alert-content">
        {title && <div className="alert-title">{title}</div>}
        <div className="alert-message">{message}</div>
      </div>
      {onClose && (
        <button className="alert-close" onClick={onClose} aria-label="Close alert">
          <X size={18} />
        </button>
      )}
    </div>
  );
};

