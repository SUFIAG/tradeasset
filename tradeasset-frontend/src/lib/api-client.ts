/**
 * Axios API Client Configuration
 * Handles HTTP requests with interceptors for authentication and error handling
 */

import axios, { AxiosError } from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { API_CONFIG, HTTP_STATUS, STORAGE_KEYS } from '../config/constants';

class ApiClient {
  private axiosInstance: AxiosInstance;
  private refreshTokenPromise: Promise<string> | null = null;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // Request interceptor
    this.axiosInstance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const token = this.getAccessToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }

        // Add tenant ID header (required by backend multi-tenancy)
        // Using default tenant from seed data
        config.headers['X-Tenant-ID'] = '00000000-0000-0000-0000-000000000001';
        config.headers['X-Tenant-Id'] = '00000000-0000-0000-0000-000000000001';

        // Add user ID header (required by backend for audit trails)
        const userId = this.getUserId();
        if (userId) {
          config.headers['X-User-Id'] = userId;
        }

        // Add device fingerprint if available
        const deviceId = this.getDeviceId();
        if (deviceId) {
          config.headers['X-Device-Id'] = deviceId;
        }

        return config;
      },
      (error: AxiosError) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      async (error: AxiosError) => {
        const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

        // Handle 401 Unauthorized - Token expired
        if (error.response?.status === HTTP_STATUS.UNAUTHORIZED && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const newAccessToken = await this.refreshAccessToken();

            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            }

            return this.axiosInstance(originalRequest);
          } catch (refreshError) {
            // Refresh token failed, redirect to login
            this.handleAuthenticationFailure();
            return Promise.reject(refreshError);
          }
        }

        // Handle other errors
        return Promise.reject(this.handleError(error));
      }
    );
  }

  private async refreshAccessToken(): Promise<string> {
    // Prevent multiple simultaneous refresh requests
    if (this.refreshTokenPromise) {
      return this.refreshTokenPromise;
    }

    this.refreshTokenPromise = (async () => {
      try {
        const refreshToken = this.getRefreshToken();
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const response = await axios.post(
          `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTH.REFRESH}`,
          { refreshToken }
        );

        const { accessToken, refreshToken: newRefreshToken } = response.data;

        this.setAccessToken(accessToken);
        if (newRefreshToken) {
          this.setRefreshToken(newRefreshToken);
        }

        return accessToken;
      } catch (error) {
        this.clearAuthTokens();
        throw error;
      } finally {
        this.refreshTokenPromise = null;
      }
    })();

    return this.refreshTokenPromise;
  }

  private handleError(error: AxiosError): Error {
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      const message = (data as any)?.message || error.message;

      switch (status) {
        case HTTP_STATUS.BAD_REQUEST:
          return new Error(`Validation Error: ${message}`);
        case HTTP_STATUS.UNAUTHORIZED:
          return new Error('Authentication failed. Please login again.');
        case HTTP_STATUS.FORBIDDEN:
          return new Error('You do not have permission to perform this action.');
        case HTTP_STATUS.NOT_FOUND:
          return new Error('The requested resource was not found.');
        case HTTP_STATUS.CONFLICT:
          return new Error(`Conflict: ${message}`);
        case HTTP_STATUS.INTERNAL_SERVER_ERROR:
          return new Error('An internal server error occurred. Please try again later.');
        default:
          return new Error(message || 'An unexpected error occurred.');
      }
    } else if (error.request) {
      // Request was made but no response received
      return new Error('Network error. Please check your internet connection.');
    } else {
      // Something else happened
      return new Error(error.message || 'An unexpected error occurred.');
    }
  }

  private handleAuthenticationFailure(): void {
    this.clearAuthTokens();
    // Redirect to login page
    window.location.href = '/login';
  }

  // Token management
  private getAccessToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
  }

  private setAccessToken(token: string): void {
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, token);
  }

  private getRefreshToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  }

  private setRefreshToken(token: string): void {
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, token);
  }

  private clearAuthTokens(): void {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  }

  private getUserId(): string | null {
    try {
      const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
      if (userData) {
        const user = JSON.parse(userData);
        return user.id || null;
      }
      // Fallback: check auth-storage for user ID
      const authStorage = localStorage.getItem('auth-storage');
      if (authStorage) {
        const auth = JSON.parse(authStorage);
        return auth?.state?.user?.id || null;
      }
    } catch (e) {
      console.error('Error getting user ID:', e);
    }
    return null;
  }

  private getDeviceId(): string | null {
    let deviceId = localStorage.getItem(STORAGE_KEYS.DEVICE_ID);
    if (!deviceId) {
      deviceId = this.generateDeviceId();
      localStorage.setItem(STORAGE_KEYS.DEVICE_ID, deviceId);
    }
    return deviceId;
  }

  private generateDeviceId(): string {
    return `device_${Date.now()}_${Math.random().toString(36).substring(2, 15)}`;
  }

  // Public methods
  public get<T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.get<T>(url, config);
  }

  public post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.post<T>(url, data, config);
  }

  public put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.put<T>(url, data, config);
  }

  public patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.patch<T>(url, data, config);
  }

  public delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.delete<T>(url, config);
  }

  public setAuthTokens(accessToken: string, refreshToken: string): void {
    this.setAccessToken(accessToken);
    this.setRefreshToken(refreshToken);
  }

  public clearTokens(): void {
    this.clearAuthTokens();
  }

  public getDeviceFingerprint(): string {
    return this.getDeviceId() || '';
  }
}

export const apiClient = new ApiClient();

