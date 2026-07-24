/**
 * Authentication Store
 * Global state management for authentication using Zustand
 */

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { AuthState, User, LoginRequest, RegisterRequest } from '../types/auth.types';
import { authService } from '../services/auth.service';
import { STORAGE_KEYS } from '../config/constants';

interface AuthActions {
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  setUser: (user: User) => void;
  setTokens: (accessToken: string, refreshToken: string) => void;
  clearAuth: () => void;
  setError: (error: string | null) => void;
  setLoading: (isLoading: boolean) => void;
  setMfaRequired: (required: boolean, token?: string) => void;
  refreshUserProfile: () => Promise<void>;
}

type AuthStore = AuthState & AuthActions;

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      mfaRequired: false,
      mfaToken: null,

      // Actions
      login: async (credentials: LoginRequest) => {
        try {
          set({ isLoading: true, error: null });

          const response = await authService.login(credentials);

          if (response.mfaRequired && response.mfaToken) {
            // MFA required - don't set as authenticated yet
            set({
              mfaRequired: true,
              mfaToken: response.mfaToken,
              isLoading: false,
            });
            return;
          }

          // Successful login without MFA
          const user: User = {
            id: response.userId,
            username: response.username,
            email: response.email,
            firstName: '',
            lastName: '',
            roles: [],
            status: 'ACTIVE' as any,
            mfaEnabled: false,
            emailVerified: true,
            createdAt: new Date().toISOString(),
          };

          set({
            user,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
            mfaRequired: false,
            mfaToken: null,
          });

          // Fetch full user profile
          get().refreshUserProfile();
        } catch (error: any) {
          set({
            error: error.message || 'Login failed',
            isLoading: false,
            isAuthenticated: false,
          });
          throw error;
        }
      },

      register: async (data: RegisterRequest) => {
        try {
          set({ isLoading: true, error: null });

          const response = await authService.register(data);

          // After registration, automatically log in
          const user: User = {
            id: response.userId,
            username: response.username,
            email: response.email,
            firstName: data.firstName,
            lastName: data.lastName,
            roles: [],
            status: 'ACTIVE' as any,
            mfaEnabled: false,
            emailVerified: false,
            createdAt: new Date().toISOString(),
          };

          set({
            user,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            error: error.message || 'Registration failed',
            isLoading: false,
          });
          throw error;
        }
      },

      logout: async () => {
        try {
          await authService.logout();
        } catch (error) {
          console.error('Logout error:', error);
        } finally {
          get().clearAuth();
        }
      },

      setUser: (user: User) => {
        set({ user, isAuthenticated: true });
      },

      setTokens: (accessToken: string, refreshToken: string) => {
        set({ accessToken, refreshToken, isAuthenticated: true });
      },

      clearAuth: () => {
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          mfaRequired: false,
          mfaToken: null,
          error: null,
        });
        localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
        localStorage.removeItem(STORAGE_KEYS.USER_DATA);
      },

      setError: (error: string | null) => {
        set({ error });
      },

      setLoading: (isLoading: boolean) => {
        set({ isLoading });
      },

      setMfaRequired: (required: boolean, token?: string) => {
        set({ mfaRequired: required, mfaToken: token || null });
      },

      refreshUserProfile: async () => {
        try {
          const user = await authService.getCurrentUser();
          set({ user });
        } catch (error) {
          console.error('Failed to refresh user profile:', error);
        }
      },
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

