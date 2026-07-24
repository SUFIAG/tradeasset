/**
 * Generic Module Create Page
 * Renders a configurable form and submits it to the backend create endpoint.
 */

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle2, AlertTriangle, ArrowLeft } from 'lucide-react';
import './ModulePage.css';

export interface FormField {
  name: string;
  label: string;
  type?: 'text' | 'number' | 'date' | 'select' | 'textarea';
  options?: { value: string; label: string }[];
  required?: boolean;
  placeholder?: string;
  defaultValue?: string;
  helpText?: string;
}

interface ModuleCreatePageProps {
  title: string;
  subtitle?: string;
  icon?: React.ReactNode;
  fields: FormField[];
  onSubmit: (payload: Record<string, any>) => Promise<any>;
  backTo: string;
}

export const ModuleCreatePage: React.FC<ModuleCreatePageProps> = ({
  title,
  subtitle,
  icon,
  fields,
  onSubmit,
  backTo,
}) => {
  const navigate = useNavigate();

  // Initialize values with default values from fields
  const [values, setValues] = useState<Record<string, any>>(() => {
    const initial: Record<string, any> = {};
    fields.forEach(field => {
      if (field.defaultValue !== undefined) {
        initial[field.name] = field.defaultValue;
      }
    });
    return initial;
  });

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const setField = (name: string, value: any) =>
    setValues((prev) => ({ ...prev, [name]: value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    setSuccess(false);
    try {
      const payload: Record<string, any> = {};
      fields.forEach((f) => {
        const v = values[f.name];
        if (v === undefined || v === '') return;
        payload[f.name] = f.type === 'number' ? Number(v) : v;
      });
      await onSubmit(payload);
      setSuccess(true);
      setTimeout(() => navigate(backTo), 1200);
    } catch (err: any) {
      setError(err?.message || 'Failed to create record.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          {icon && <span className="module-page-icon">{icon}</span>}
          <div>
            <h1>{title}</h1>
            {subtitle && <p>{subtitle}</p>}
          </div>
        </div>
        <button className="module-refresh-btn" onClick={() => navigate(backTo)}>
          <ArrowLeft size={16} /> Back
        </button>
      </div>

      <div className="module-content">
        {success && (
          <div className="module-state success">
            <CheckCircle2 size={32} />
            <p>Created successfully. Redirecting…</p>
          </div>
        )}

        {!success && (
          <form className="module-form" onSubmit={handleSubmit}>
            {error && (
              <div className="module-form-error">
                <AlertTriangle size={18} /> {error}
              </div>
            )}
            <div className="module-form-grid">
              {fields.map((f) => (
                <div className="module-form-field" key={f.name}>
                  <label>
                    {f.label}
                    {f.required && <span className="req"> *</span>}
                  </label>
                  {f.type === 'select' ? (
                    <select
                      value={values[f.name] ?? ''}
                      required={f.required}
                      onChange={(e) => setField(f.name, e.target.value)}
                    >
                      <option value="">{f.placeholder || 'Select…'}</option>
                      {f.options?.map((o) => (
                        <option key={o.value} value={o.value}>
                          {o.label}
                        </option>
                      ))}
                    </select>
                  ) : f.type === 'textarea' ? (
                    <textarea
                      rows={3}
                      value={values[f.name] ?? ''}
                      required={f.required}
                      placeholder={f.placeholder}
                      onChange={(e) => setField(f.name, e.target.value)}
                    />
                  ) : (
                    <input
                      type={f.type ?? 'text'}
                      value={values[f.name] ?? ''}
                      required={f.required}
                      placeholder={f.placeholder}
                      onChange={(e) => setField(f.name, e.target.value)}
                    />
                  )}
                  {f.helpText && (
                    <small className="module-form-help">{f.helpText}</small>
                  )}
                </div>
              ))}
            </div>
            <div className="module-form-actions">
              <button type="submit" className="module-primary-btn" disabled={submitting}>
                {submitting ? 'Saving…' : 'Create'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
