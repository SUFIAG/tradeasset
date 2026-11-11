/**
 * The one HTTP client.
 *
 * - Bearer access token from memory (tokenStore). Identity is never sent as a header: tenant,
 *   user and roles come from the verified token server-side (REQ-SEC-002).
 * - On 401, a single in-flight refresh is shared by every waiting request; each retries once.
 *   The refresh token is rotated on every use (REQ-SEC-003).
 * - Errors are normalised to ApiError (RFC 7807 aware).
 * - Nothing here logs payloads (REQ-UI-007).
 */

import axios, { AxiosHeaders } from 'axios';
import type { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { apiConfig, authEndpoints, publicAuthPaths } from './config';
import { tokenStore } from './tokens';
import { toApiError } from './errors';

interface RetriableConfig extends InternalAxiosRequestConfig {
  _retried?: boolean;
}

interface RefreshResponse {
  accessToken: string;
  refreshToken?: string;
}

export const SESSION_EXPIRED_EVENT = 'ta:session-expired';

function isPublicAuthPath(url: string | undefined): boolean {
  if (!url) return false;
  const path = url.replace(apiConfig.baseUrl, '').split('?')[0];
  return publicAuthPaths.includes(path);
}

function newRequestId(): string {
  return typeof crypto !== 'undefined' && 'randomUUID' in crypto
    ? crypto.randomUUID()
    : `${Date.now().toString(36)}-${Math.random().toString(36).slice(2)}`;
}

/* ---------------- Single-flight refresh ---------------- */

let refreshInFlight: Promise<string> | null = null;

/** Exchange the refresh token for a new pair. Concurrent callers share one request. */
export function refreshAccessToken(): Promise<string> {
  if (refreshInFlight) return refreshInFlight;
  const refreshToken = tokenStore.getRefreshToken();
  if (!refreshToken) return Promise.reject(new Error('no-refresh-token'));

  refreshInFlight = axios
    .post<RefreshResponse>(
      `${apiConfig.baseUrl}${authEndpoints.refresh}`,
      { refreshToken },
      { timeout: apiConfig.timeoutMs, headers: { 'Content-Type': 'application/json' } },
    )
    .then((response) => {
      const { accessToken, refreshToken: rotated } = response.data;
      if (!accessToken) throw new Error('refresh-without-token');
      tokenStore.set({ accessToken, refreshToken: rotated ?? null });
      return accessToken;
    })
    .catch((error: unknown) => {
      // Only a definitive rejection ends the session. A network blip keeps the refresh token.
      const apiError = toApiError(error);
      if (apiError.kind === 'problem' && apiError.status !== null && apiError.status < 500) {
        tokenStore.clear();
        window.dispatchEvent(new CustomEvent(SESSION_EXPIRED_EVENT));
      }
      throw apiError;
    })
    .finally(() => {
      refreshInFlight = null;
    });

  return refreshInFlight;
}

/* ---------------- Instance ---------------- */

export const http: AxiosInstance = axios.create({
  baseURL: apiConfig.baseUrl,
  timeout: apiConfig.timeoutMs,
  headers: { Accept: 'application/json, application/problem+json' },
});

http.interceptors.request.use(async (config) => {
  const headers = AxiosHeaders.from(config.headers);
  headers.set('X-Request-Id', newRequestId());
  headers.set('Accept-Language', document.documentElement.lang || 'en');

  if (!isPublicAuthPath(config.url)) {
    let token = tokenStore.getAccessToken();
    // After a page reload only the refresh token survives: refresh before the first call.
    if (!token && tokenStore.getRefreshToken()) {
      try {
        token = await refreshAccessToken();
      } catch {
        token = null;
      }
    }
    if (token) headers.set('Authorization', `Bearer ${token}`);
  }
  config.headers = headers;
  return config;
});

http.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const config = error.config as RetriableConfig | undefined;
    const canRetry =
      error.response?.status === 401 &&
      config !== undefined &&
      !config._retried &&
      !isPublicAuthPath(config.url) &&
      tokenStore.getRefreshToken() !== null;

    if (canRetry) {
      config._retried = true;
      try {
        const token = await refreshAccessToken();
        const headers = AxiosHeaders.from(config.headers);
        headers.set('Authorization', `Bearer ${token}`);
        config.headers = headers;
        // The Idempotency-Key header is preserved, so a retried mutation is still safe.
        return http.request(config);
      } catch (refreshError) {
        return Promise.reject(toApiError(refreshError));
      }
    }

    if (error.response?.status === 401 && !isPublicAuthPath(config?.url)) {
      tokenStore.clear();
      window.dispatchEvent(new CustomEvent(SESSION_EXPIRED_EVENT));
    }
    return Promise.reject(toApiError(error));
  },
);
