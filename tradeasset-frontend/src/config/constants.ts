/**
 * Application Constants
 * Centralized configuration for the TradeSphere application
 */

export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  TIMEOUT: 30000,
  ENDPOINTS: {
    // Authentication
    AUTH: {
      REGISTER: '/api/v1/auth/register',
      LOGIN: '/api/v1/auth/login',
      LOGOUT: '/api/v1/auth/logout',
      REFRESH: '/api/v1/auth/refresh',
      FORGOT_PASSWORD: '/api/v1/auth/forgot-password',
      RESET_PASSWORD: '/api/v1/auth/reset-password',
      VALIDATE_TOKEN: '/api/v1/auth/validate-token',
      CAPTCHA_GENERATE: '/api/v1/auth/captcha/generate',
      CAPTCHA_REQUIRED: '/api/v1/auth/captcha/required',
      DEVICES: '/api/v1/auth/devices',
      DEVICE_TRUST: '/api/v1/auth/devices',
    },
    // MFA
    MFA: {
      SETUP: '/api/v1/security/mfa/setup',
      ENABLE: '/api/v1/security/mfa/enable',
      VERIFY: '/api/v1/security/mfa/verify',
      DISABLE: '/api/v1/security/mfa/disable',
      GENERATE_OTP: '/api/v1/security/mfa/generate-otp',
      BACKUP_CODES: '/api/v1/security/mfa/backup-codes',
    },
    // SSO
    SSO: {
      INITIATE: '/api/v1/security/sso/initiate',
      CALLBACK: '/api/v1/security/sso/callback',
      VALIDATE: '/api/v1/security/sso/validate',
    },
    // Customers
    CUSTOMERS: '/api/v1/customers',
    // Letter of Credit
    LC: '/api/v1/trade/lc',
    // Bank Guarantees
    BG: '/api/v1/bank-guarantees',
    // Collections
    COLLECTIONS: '/api/v1/documentary-collections',
    // Supply Chain Finance
    SCF: '/api/v1/scf',
    // Documents
    DOCUMENTS: '/api/v1/documents',
    // Analytics
    ANALYTICS: '/api/v1/analytics',
    // Risk Assessment
    RISK: '/api/risk/assessments',
    // Compliance
    COMPLIANCE: '/api/compliance/checks',
    // Workflow
    WORKFLOW: '/api/v1/workflow',
    // Shipments
    SHIPMENTS: '/api/v1/shipments',
    // Knowledge Base
    KNOWLEDGE: '/api/v1/knowledge',
    // Audit
    AUDIT: '/api/v1/audit',
  },
} as const;

export const APP_CONFIG = {
  NAME: 'TradeSphere',
  DESCRIPTION: 'Trade Asset Management Platform',
  VERSION: '1.0.0',
  COMPANY: 'TradeSphere Inc.',
} as const;

export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'tradeasset_access_token',
  REFRESH_TOKEN: 'tradeasset_refresh_token',
  USER_DATA: 'tradeasset_user_data',
  THEME: 'tradeasset_theme',
  LANGUAGE: 'tradeasset_language',
  DEVICE_ID: 'tradeasset_device_id',
} as const;

export const ROUTES = {
  // Public routes
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  FORGOT_PASSWORD: '/forgot-password',
  RESET_PASSWORD: '/reset-password',
  
  // Protected routes
  DASHBOARD: '/dashboard',
  
  // Customer Management
  CUSTOMERS: '/customers',
  CUSTOMER_DETAILS: '/customers/:id',
  CUSTOMER_CREATE: '/customers/create',
  CUSTOMERS_PENDING_KYC: '/customers/pending-kyc',
  
  // Letter of Credit
  LC_LIST: '/lc',
  LC_DETAILS: '/lc/:id',
  LC_CREATE: '/lc/create',
  LC_DRAFTS: '/lc/drafts',
  LC_AMEND: '/lc/:id/amend',
  
  // Bank Guarantees
  BG_LIST: '/bank-guarantees',
  BG_DETAILS: '/bank-guarantees/:id',
  BG_CREATE: '/bank-guarantees/create',
  BG_EXPIRING: '/bank-guarantees/expiring',
  
  // Collections
  COLLECTIONS_LIST: '/collections',
  COLLECTION_DETAILS: '/collections/:id',
  COLLECTION_CREATE: '/collections/create',
  
  // Supply Chain Finance
  SCF_LIST: '/scf',
  SCF_DETAILS: '/scf/:id',
  SCF_CREATE: '/scf/create',
  SCF_PENDING: '/scf/pending',
  
  // Documents
  DOCUMENTS: '/documents',
  DOCUMENT_DETAILS: '/documents/:id',
  
  // Analytics
  ANALYTICS_DASHBOARD: '/analytics',
  ANALYTICS_REPORTS: '/analytics/reports',
  ANALYTICS_EXPORT: '/analytics/export',
  
  // Risk Management
  RISK_DASHBOARD: '/risk',
  RISK_ASSESSMENT: '/risk/assessment',
  RISK_HIGH: '/risk/high-risk',
  
  // Compliance
  COMPLIANCE_DASHBOARD: '/compliance',
  COMPLIANCE_CHECKS: '/compliance/checks',
  COMPLIANCE_ALERTS: '/compliance/alerts',
  
  // Workflow
  WORKFLOW_LIST: '/workflows',
  WORKFLOW_DETAILS: '/workflows/:id',
  
  // Shipments
  SHIPMENTS_LIST: '/shipments',
  SHIPMENT_DETAILS: '/shipments/:id',
  SHIPMENT_TRACKING: '/shipments/:id/tracking',
  
  // Knowledge Base
  KNOWLEDGE_BASE: '/knowledge',
  KNOWLEDGE_ARTICLE: '/knowledge/:id',
  
  // AI Copilot
  COPILOT: '/copilot',

  // Notifications & Help
  NOTIFICATIONS: '/notifications',
  HELP: '/help',

  // Settings
  SETTINGS: '/settings',
  PROFILE: '/settings/profile',
  SECURITY: '/settings/security',
  MFA_SETUP: '/settings/mfa',
} as const;

export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
  SYSTEM: 'system',
} as const;

export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],
} as const;

export const DATE_FORMATS = {
  DISPLAY: 'MMM dd, yyyy',
  DISPLAY_WITH_TIME: 'MMM dd, yyyy HH:mm',
  ISO: "yyyy-MM-dd'T'HH:mm:ss",
  SHORT: 'MM/dd/yyyy',
} as const;

export const VALIDATION = {
  PASSWORD: {
    MIN_LENGTH: 8,
    MAX_LENGTH: 128,
    REQUIRE_UPPERCASE: true,
    REQUIRE_LOWERCASE: true,
    REQUIRE_NUMBER: true,
    REQUIRE_SPECIAL: true,
  },
  USERNAME: {
    MIN_LENGTH: 3,
    MAX_LENGTH: 50,
    PATTERN: /^[a-zA-Z0-9_-]+$/,
  },
  EMAIL: {
    PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  },
} as const;

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500,
} as const;

export const NOTIFICATION_DURATION = {
  SUCCESS: 3000,
  ERROR: 5000,
  WARNING: 4000,
  INFO: 3000,
} as const;

