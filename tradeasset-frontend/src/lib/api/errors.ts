/**
 * RFC 7807 problem details and the single error type the UI deals with.
 *
 * The UI never shows server `title`/`detail` text. It maps the stable `code` to an i18n message
 * (see `errors` namespace). Unknown codes fall back to an HTTP-status message.
 */

import { AxiosError } from 'axios';

export interface ProblemFieldError {
  field: string;
  code: string;
  /** Interpolation values for the field message, if the server sends any. */
  params?: Record<string, string | number>;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  /** Stable, versioned machine-readable code, e.g. `LC_ILLEGAL_TRANSITION`. */
  code?: string;
  traceId?: string;
  errors?: ProblemFieldError[];
  /** Legal next states, sent with illegal-transition errors (REQ-STA-001). */
  allowedActions?: string[];
  currentState?: string;
  [extension: string]: unknown;
}

export type ApiErrorKind =
  | 'problem' // server answered with an error status
  | 'network' // no response: offline, DNS, CORS, connection reset
  | 'timeout' // no answer in time: OUTCOME UNKNOWN, never treat as failed
  | 'cancelled'
  | 'unknown';

export class ApiError extends Error {
  readonly kind: ApiErrorKind;
  readonly status: number | null;
  readonly code: string | null;
  readonly problem: ProblemDetail | null;
  readonly fieldErrors: ProblemFieldError[];
  readonly traceId: string | null;

  constructor(init: {
    kind: ApiErrorKind;
    status?: number | null;
    problem?: ProblemDetail | null;
    traceId?: string | null;
  }) {
    const code = init.problem?.code ?? null;
    super(code ?? init.kind);
    this.name = 'ApiError';
    this.kind = init.kind;
    this.status = init.status ?? null;
    this.problem = init.problem ?? null;
    this.code = code;
    this.fieldErrors = init.problem?.errors ?? [];
    this.traceId = init.problem?.traceId ?? init.traceId ?? null;
  }

  /** Outcome unknown: the request may have taken effect. Retry with the SAME idempotency key. */
  get isOutcomeUnknown(): boolean {
    return this.kind === 'timeout' || this.kind === 'network' || this.status === 502 || this.status === 504;
  }

  get isNotFound(): boolean {
    return this.status === 404;
  }

  get isConflict(): boolean {
    return this.status === 409;
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

function str(value: unknown): string | undefined {
  return typeof value === 'string' && value.length > 0 ? value : undefined;
}

function normaliseFieldErrors(raw: unknown): ProblemFieldError[] {
  // Accept [{field, code}], [{field, message}] and {field: code} shapes.
  if (Array.isArray(raw)) {
    return raw.filter(isRecord).map((entry) => ({
      field: str(entry.field) ?? str(entry.path) ?? str(entry.name) ?? '',
      code: str(entry.code) ?? str(entry.errorCode) ?? 'INVALID',
      params: isRecord(entry.params) ? (entry.params as Record<string, string | number>) : undefined,
    }));
  }
  if (isRecord(raw)) {
    return Object.entries(raw).map(([field, code]) => ({ field, code: str(code) ?? 'INVALID' }));
  }
  return [];
}

/** Parse any response body into a ProblemDetail, tolerating non-standard shapes. */
export function parseProblem(body: unknown, status?: number): ProblemDetail | null {
  if (!isRecord(body)) return status ? { status } : null;
  const properties = isRecord(body.properties) ? body.properties : {};
  const code =
    str(body.code) ?? str(body.errorCode) ?? str(properties.code) ?? str(properties.errorCode) ?? undefined;
  return {
    ...body,
    type: str(body.type),
    title: str(body.title),
    status: typeof body.status === 'number' ? body.status : status,
    detail: str(body.detail),
    instance: str(body.instance),
    code,
    traceId: str(body.traceId) ?? str(properties.traceId) ?? str(body.correlationId),
    errors: normaliseFieldErrors(body.errors ?? body.fieldErrors ?? body.violations ?? properties.errors),
    allowedActions: Array.isArray(body.allowedActions)
      ? body.allowedActions.filter((a): a is string => typeof a === 'string')
      : undefined,
    currentState: str(body.currentState),
  };
}

export function toApiError(error: unknown): ApiError {
  if (error instanceof ApiError) return error;
  if (error instanceof AxiosError) {
    if (error.code === AxiosError.ERR_CANCELED) return new ApiError({ kind: 'cancelled' });
    if (error.code === AxiosError.ECONNABORTED || error.code === AxiosError.ETIMEDOUT) {
      return new ApiError({ kind: 'timeout' });
    }
    if (error.response) {
      const traceHeader = error.response.headers?.['x-trace-id'] ?? error.response.headers?.['x-request-id'];
      return new ApiError({
        kind: 'problem',
        status: error.response.status,
        problem: parseProblem(error.response.data, error.response.status),
        traceId: typeof traceHeader === 'string' ? traceHeader : null,
      });
    }
    if (error.request) return new ApiError({ kind: 'network' });
  }
  return new ApiError({ kind: 'unknown' });
}

/**
 * i18n keys to try, most specific first. Used by ErrorState, toasts and form errors:
 *   t(errorMessageKeys(err))   — i18next picks the first key that exists.
 */
export function errorMessageKeys(error: unknown): string[] {
  const apiError = toApiError(error);
  const keys: string[] = [];
  if (apiError.code) keys.push(`errors:codes.${apiError.code}`);
  if (apiError.kind !== 'problem') keys.push(`errors:client.${apiError.kind}`);
  if (apiError.status) keys.push(`errors:http.${apiError.status}`);
  if (apiError.status && apiError.status >= 500) keys.push('errors:http.5xx');
  keys.push('errors:client.unknown');
  return keys;
}
