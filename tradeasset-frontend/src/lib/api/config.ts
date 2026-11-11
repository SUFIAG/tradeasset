/** Runtime API configuration, read once from Vite env. */

function readTimeout(): number {
  const raw = import.meta.env.VITE_API_TIMEOUT_MS;
  const parsed = raw ? Number.parseInt(String(raw), 10) : NaN;
  return Number.isFinite(parsed) && parsed > 0 ? parsed : 30_000;
}

export const apiConfig = {
  /** Empty string means same origin (dev server proxies /api). */
  baseUrl: String(import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/+$/, ''),
  timeoutMs: readTimeout(),
} as const;

/** Endpoints owned by the platform shell. Module endpoints live with their module. */
export const authEndpoints = {
  login: '/api/v1/auth/login',
  register: '/api/v1/auth/register',
  refresh: '/api/v1/auth/refresh',
  logout: '/api/v1/auth/logout',
  me: '/api/v1/auth/me',
  forgotPassword: '/api/v1/auth/forgot-password',
  resetPassword: '/api/v1/auth/reset-password',
  validateResetToken: '/api/v1/auth/validate-token',
  captchaRequired: '/api/v1/auth/captcha/required',
  captchaGenerate: '/api/v1/auth/captcha/generate',
  mfaVerify: '/api/v1/security/mfa/verify',
  mfaSetup: '/api/v1/security/mfa/setup',
  mfaEnable: '/api/v1/security/mfa/enable',
} as const;

/** Requests to these paths never carry a bearer token and never trigger a refresh. */
export const publicAuthPaths: readonly string[] = [
  authEndpoints.login,
  authEndpoints.register,
  authEndpoints.refresh,
  authEndpoints.forgotPassword,
  authEndpoints.resetPassword,
  authEndpoints.validateResetToken,
  authEndpoints.captchaRequired,
  authEndpoints.captchaGenerate,
];
