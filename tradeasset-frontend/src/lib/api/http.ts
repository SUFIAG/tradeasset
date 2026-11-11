/**
 * Typed request helpers. Modules call these, never axios directly.
 *
 * Every mutation takes an explicit `idempotencyKey`. Pass a key from `useIdempotentMutation`
 * (or `null` only for auth endpoints that are not idempotent by design). Making the argument
 * required keeps rule 6 of CLAUDE.md visible at every call site.
 */

import type { AxiosRequestConfig } from 'axios';
import { http } from './client';

export type QueryParams = Record<string, string | number | boolean | null | undefined | readonly string[]>;

export interface MutationOptions {
  idempotencyKey: string | null;
  signal?: AbortSignal;
  headers?: Record<string, string>;
}

function cleanParams(params?: QueryParams): Record<string, unknown> | undefined {
  if (!params) return undefined;
  const out: Record<string, unknown> = {};
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null || value === '') continue;
    out[key] = value;
  }
  return out;
}

function mutationConfig(options: MutationOptions): AxiosRequestConfig {
  const headers: Record<string, string> = { ...options.headers };
  if (options.idempotencyKey) headers['Idempotency-Key'] = options.idempotencyKey;
  return { headers, signal: options.signal };
}

export const api = {
  async get<T>(url: string, params?: QueryParams, signal?: AbortSignal): Promise<T> {
    const response = await http.get<T>(url, {
      params: cleanParams(params),
      signal,
      paramsSerializer: { indexes: null },
    });
    return response.data;
  },

  async post<T, B = unknown>(url: string, body: B, options: MutationOptions): Promise<T> {
    const response = await http.post<T>(url, body, mutationConfig(options));
    return response.data;
  },

  async put<T, B = unknown>(url: string, body: B, options: MutationOptions): Promise<T> {
    const response = await http.put<T>(url, body, mutationConfig(options));
    return response.data;
  },

  async patch<T, B = unknown>(url: string, body: B, options: MutationOptions): Promise<T> {
    const response = await http.patch<T>(url, body, mutationConfig(options));
    return response.data;
  },

  async delete<T>(url: string, options: MutationOptions): Promise<T> {
    const response = await http.delete<T>(url, mutationConfig(options));
    return response.data;
  },

  /** Multipart upload with progress (0..1 fraction of bytes; not money). */
  async upload<T>(
    url: string,
    form: FormData,
    options: MutationOptions & { onProgress?: (fraction: number) => void },
  ): Promise<T> {
    const config = mutationConfig(options);
    const response = await http.post<T>(url, form, {
      ...config,
      onUploadProgress: (event) => {
        if (options.onProgress && event.total) options.onProgress(event.loaded / event.total);
      },
    });
    return response.data;
  },
};
