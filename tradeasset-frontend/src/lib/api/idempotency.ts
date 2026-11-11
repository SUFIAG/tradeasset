/**
 * Idempotency keys that survive a page refresh (REQ-REL-001, REQ-UI-003).
 *
 * A key is stored in sessionStorage per *scope* (one form or one action on one record) together
 * with a fingerprint of the request body:
 *   - same scope + same body  -> same key  (double tap, retry after timeout, refresh mid-submit:
 *                                           the server replays the first response)
 *   - same scope + new body   -> new key   (the user changed their intent; avoids a 409)
 *   - success                 -> key cleared, the next submission is a new operation
 */

const PREFIX = 'ta.idem.';

interface StoredKey {
  key: string;
  fingerprint: string;
}

export function generateIdempotencyKey(): string {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) return crypto.randomUUID();
  const bytes = new Uint8Array(16);
  globalThis.crypto.getRandomValues(bytes);
  bytes[6] = (bytes[6] & 0x0f) | 0x40;
  bytes[8] = (bytes[8] & 0x3f) | 0x80;
  const hex = Array.from(bytes, (b) => b.toString(16).padStart(2, '0')).join('');
  return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
}

/** Deterministic JSON with sorted keys, so field order does not change the fingerprint. */
function stableStringify(value: unknown): string {
  if (value === null || typeof value !== 'object') return JSON.stringify(value) ?? 'undefined';
  if (value instanceof File) return `file:${value.name}:${value.size}:${value.lastModified}`;
  if (value instanceof FormData) {
    const entries: string[] = [];
    value.forEach((v, k) => entries.push(`${k}=${stableStringify(v)}`));
    return `form{${entries.sort().join(',')}}`;
  }
  if (Array.isArray(value)) return `[${value.map(stableStringify).join(',')}]`;
  const keys = Object.keys(value as Record<string, unknown>).sort();
  return `{${keys.map((k) => `${JSON.stringify(k)}:${stableStringify((value as Record<string, unknown>)[k])}`).join(',')}}`;
}

/** FNV-1a 32-bit. Local change detection only; not a security primitive. */
export function fingerprint(value: unknown): string {
  const text = stableStringify(value);
  let hash = 0x811c9dc5;
  for (let i = 0; i < text.length; i += 1) {
    hash ^= text.charCodeAt(i);
    hash = Math.imul(hash, 0x01000193);
  }
  return (hash >>> 0).toString(16).padStart(8, '0');
}

function read(scope: string): StoredKey | null {
  try {
    const raw = window.sessionStorage.getItem(PREFIX + scope);
    return raw ? (JSON.parse(raw) as StoredKey) : null;
  } catch {
    return null;
  }
}

function write(scope: string, value: StoredKey): void {
  try {
    window.sessionStorage.setItem(PREFIX + scope, JSON.stringify(value));
  } catch {
    /* storage full or blocked: the key still works for this page life */
  }
}

/** Return the key for this scope and body, creating or rotating as needed. */
export function idempotencyKeyFor(scope: string, body: unknown): string {
  const print = fingerprint(body);
  const stored = read(scope);
  if (stored && stored.fingerprint === print) return stored.key;
  const next = { key: generateIdempotencyKey(), fingerprint: print };
  write(scope, next);
  return next.key;
}

/** Forget the key after a definitive outcome (success, or a 4xx the user must fix). */
export function clearIdempotencyKey(scope: string): void {
  try {
    window.sessionStorage.removeItem(PREFIX + scope);
  } catch {
    /* ignore */
  }
}
