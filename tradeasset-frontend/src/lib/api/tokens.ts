/**
 * Token store.
 *
 * - The access token lives in memory only. It is never written to storage, so an XSS payload
 *   that reads storage cannot lift a live bearer token.
 * - The refresh token is kept so a page refresh can restore the session. It goes to
 *   sessionStorage by default (tab-scoped) or localStorage when the user chose "keep me signed
 *   in". The server rotates it on every use (REQ-SEC-003), so a stolen copy is detectable.
 *
 * When the backend moves the refresh token to an httpOnly cookie, only this file changes.
 */

const REFRESH_KEY = 'ta.rt';

type Listener = () => void;

let accessToken: string | null = null;
const listeners = new Set<Listener>();

function storages(): Storage[] {
  const result: Storage[] = [];
  try {
    result.push(window.sessionStorage, window.localStorage);
  } catch {
    /* storage blocked: memory only */
  }
  return result;
}

function notify(): void {
  listeners.forEach((listener) => listener());
}

export interface TokenPair {
  accessToken: string;
  refreshToken?: string | null;
}

export const tokenStore = {
  getAccessToken(): string | null {
    return accessToken;
  },

  getRefreshToken(): string | null {
    for (const storage of storages()) {
      const value = storage.getItem(REFRESH_KEY);
      if (value) return value;
    }
    return null;
  },

  /** True when there is anything that could restore a session. */
  hasSession(): boolean {
    return accessToken !== null || this.getRefreshToken() !== null;
  },

  /**
   * Store a token pair. `persist` decides between tab-scoped and device-scoped storage of the
   * refresh token; when omitted the current location is kept (used by rotation).
   */
  set(pair: TokenPair, persist?: boolean): void {
    accessToken = pair.accessToken;
    if (pair.refreshToken) {
      const [session, local] = storages();
      const useLocal = persist ?? Boolean(local?.getItem(REFRESH_KEY));
      session?.removeItem(REFRESH_KEY);
      local?.removeItem(REFRESH_KEY);
      (useLocal ? local : session)?.setItem(REFRESH_KEY, pair.refreshToken);
    }
    notify();
  },

  clear(): void {
    accessToken = null;
    storages().forEach((storage) => storage.removeItem(REFRESH_KEY));
    notify();
  },

  subscribe(listener: Listener): () => void {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },
};
