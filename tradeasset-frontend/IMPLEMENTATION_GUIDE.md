# TradeSphere AI - Frontend Implementation Guide
## Complete Setup & Configuration Instructions

**Version:** 1.0.0  
**Last Updated:** July 20, 2026  
**Document Type:** Implementation & Setup Guide

---

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Setup](#project-setup)
3. [Configuration Files](#configuration-files)
4. [Project Structure Setup](#project-structure-setup)
5. [Implementation Roadmap](#implementation-roadmap)
6. [Development Workflow](#development-workflow)
7. [Testing Guide](#testing-guide)
8. [Deployment Guide](#deployment-guide)
9. [Troubleshooting](#troubleshooting)

---

## 1. Prerequisites

### Required Software
```bash
Node.js: v18.x or v20.x (LTS)
npm: v10.x or higher
Git: v2.x or higher
VS Code or WebStorm (recommended IDEs)
```

### System Requirements
```
OS: Windows 10/11, macOS 12+, or Linux
RAM: 8GB minimum, 16GB recommended
Disk Space: 5GB free space
```

---

## 2. Project Setup

### 2.1 Initialize Project
```powershell
# Create project directory
cd C:\Users\sufyan.abdulghani\Downloads\Softwares\VP\asset\tradeasset-frontend

# Initialize Vite project with React + TypeScript
npm create vite@latest . -- --template react-ts

# Install dependencies
npm install
```

### 2.2 Install Core Dependencies
```powershell
# UI Framework & Components
npm install antd@5.19.1 @ant-design/icons@5.3.7

# Routing
npm install react-router-dom@6.24.1

# State Management
npm install zustand@4.5.4

# Server State Management
npm install @tanstack/react-query@5.49.2

# Forms
npm install react-hook-form@7.52.1 @hookform/resolvers@2.9.11 zod@3.23.8

# HTTP Client
npm install axios@1.7.2

# WebSocket
npm install socket.io-client@4.7.2

# Date Handling
npm install date-fns@3.6.0

# Styling
npm install tailwindcss@3.4.5 postcss@8.4.39 autoprefixer@10.4.19
npm install clsx@2.1.1

# Charts
npm install echarts@5.5.0 echarts-for-react@3.0.2
npm install recharts@2.12.7

# File Upload
npm install react-dropzone@14.2.3

# PDF & Excel
npm install jspdf@2.5.1 html2canvas@1.4.1
npm install xlsx@0.18.5

# Notifications
npm install react-hot-toast@2.4.1

# Animations
npm install framer-motion@11.2.12

# Icons
npm install lucide-react@0.400.0

# Internationalization
npm install react-i18next@14.1.2 i18next@23.11.5

# Utilities
npm install lodash@4.17.21 dayjs@1.11.11
```

### 2.3 Install Development Dependencies
```powershell
# TypeScript
npm install -D typescript@5.5.3 @types/react@18.3.3 @types/react-dom@18.3.0
npm install -D @types/node@20.14.10

# Linting
npm install -D eslint@9.6.0 @typescript-eslint/eslint-plugin@7.15.0 @typescript-eslint/parser@7.15.0
npm install -D eslint-plugin-react@7.34.3 eslint-plugin-react-hooks@4.6.2

# Formatting
npm install -D prettier@3.3.2 eslint-config-prettier@9.1.0 eslint-plugin-prettier@5.1.3

# Testing
npm install -D vitest@1.6.0 @vitest/ui@1.6.0
npm install -D @testing-library/react@16.0.0 @testing-library/jest-dom@6.4.6 @testing-library/user-event@14.5.2
npm install -D jsdom@24.1.0

# E2E Testing
npm install -D @playwright/test@1.45.0

# Build Tools
npm install -D vite@5.3.3 @vitejs/plugin-react@4.3.1
npm install -D rollup-plugin-visualizer@5.12.0
```

---

## 3. Configuration Files

### 3.1 package.json
```json
{
  "name": "tradeasset-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "lint:fix": "eslint . --ext ts,tsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx,css,md}\"",
    "type-check": "tsc --noEmit",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:e2e": "playwright test",
    "analyze": "vite build --mode analyze"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "antd": "^5.19.1",
    "@ant-design/icons": "^5.3.7",
    "react-router-dom": "^6.24.1",
    "zustand": "^4.5.4",
    "@tanstack/react-query": "^5.49.2",
    "react-hook-form": "^7.52.1",
    "@hookform/resolvers": "^2.9.11",
    "zod": "^3.23.8",
    "axios": "^1.7.2",
    "socket.io-client": "^4.7.2",
    "date-fns": "^3.6.0",
    "tailwindcss": "^3.4.5",
    "clsx": "^2.1.1",
    "echarts": "^5.5.0",
    "echarts-for-react": "^3.0.2",
    "recharts": "^2.12.7",
    "react-dropzone": "^14.2.3",
    "jspdf": "^2.5.1",
    "html2canvas": "^1.4.1",
    "xlsx": "^0.18.5",
    "react-hot-toast": "^2.4.1",
    "framer-motion": "^11.2.12",
    "lucide-react": "^0.400.0",
    "react-i18next": "^14.1.2",
    "i18next": "^23.11.5",
    "lodash": "^4.17.21",
    "dayjs": "^1.11.11"
  },
  "devDependencies": {
    "@types/react": "^18.3.3",
    "@types/react-dom": "^18.3.0",
    "@types/node": "^20.14.10",
    "@types/lodash": "^4.17.6",
    "@typescript-eslint/eslint-plugin": "^7.15.0",
    "@typescript-eslint/parser": "^7.15.0",
    "@vitejs/plugin-react": "^4.3.1",
    "typescript": "^5.5.3",
    "vite": "^5.3.3",
    "eslint": "^9.6.0",
    "eslint-config-prettier": "^9.1.0",
    "eslint-plugin-prettier": "^5.1.3",
    "eslint-plugin-react": "^7.34.3",
    "eslint-plugin-react-hooks": "^4.6.2",
    "prettier": "^3.3.2",
    "vitest": "^1.6.0",
    "@vitest/ui": "^1.6.0",
    "@testing-library/react": "^16.0.0",
    "@testing-library/jest-dom": "^6.4.6",
    "@testing-library/user-event": "^14.5.2",
    "jsdom": "^24.1.0",
    "@playwright/test": "^1.45.0",
    "rollup-plugin-visualizer": "^5.12.0",
    "postcss": "^8.4.39",
    "autoprefixer": "^10.4.19"
  }
}
```

### 3.2 vite.config.ts
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import { visualizer } from 'rollup-plugin-visualizer';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    visualizer({
      open: false,
      gzipSize: true,
      brotliSize: true,
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'antd-vendor': ['antd', '@ant-design/icons'],
          'query-vendor': ['@tanstack/react-query'],
          'chart-vendor': ['echarts', 'echarts-for-react', 'recharts'],
          'form-vendor': ['react-hook-form', '@hookform/resolvers', 'zod'],
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
  optimizeDeps: {
    include: ['react', 'react-dom', 'antd', '@ant-design/icons'],
  },
});
```

### 3.3 tsconfig.json
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,

    /* Bundler mode */
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",

    /* Linting */
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,

    /* Path mapping */
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### 3.4 tsconfig.node.json
```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

### 3.5 tailwind.config.js
```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#e6f0ff',
          100: '#bfdaff',
          200: '#99c4ff',
          300: '#70adff',
          400: '#4d9bff',
          500: '#1a77f2',
          600: '#0061db',
          700: '#004fb5',
          800: '#003d8f',
          900: '#002b69',
        },
        secondary: {
          50: '#e0f7fa',
          100: '#b3ecf2',
          200: '#80deea',
          300: '#4dd0e1',
          400: '#26c6da',
          500: '#00bcd4',
          600: '#00acc1',
          700: '#0097a7',
          800: '#00838f',
          900: '#006064',
        },
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false, // Disable Tailwind's base styles to avoid conflicts with Ant Design
  },
};
```

### 3.6 postcss.config.js
```javascript
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
};
```

### 3.7 .eslintrc.json
```json
{
  "env": {
    "browser": true,
    "es2021": true,
    "node": true
  },
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:prettier/recommended"
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "ecmaVersion": "latest",
    "sourceType": "module",
    "ecmaFeatures": {
      "jsx": true
    }
  },
  "plugins": [
    "react",
    "react-hooks",
    "@typescript-eslint",
    "prettier"
  ],
  "rules": {
    "react/react-in-jsx-scope": "off",
    "@typescript-eslint/no-explicit-any": "warn",
    "@typescript-eslint/no-unused-vars": ["warn", { "argsIgnorePattern": "^_" }],
    "prettier/prettier": "warn",
    "no-console": ["warn", { "allow": ["warn", "error"] }]
  },
  "settings": {
    "react": {
      "version": "detect"
    }
  }
}
```

### 3.8 .prettierrc
```json
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "arrowParens": "always",
  "endOfLine": "lf"
}
```

### 3.9 .env.development
```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_WS_URL=ws://localhost:8080/ws

# Application
VITE_APP_NAME=TradeSphere AI
VITE_APP_VERSION=1.0.0
VITE_APP_ENV=development

# Features
VITE_ENABLE_AI_COPILOT=true
VITE_ENABLE_ANALYTICS=true
VITE_ENABLE_NOTIFICATIONS=true

# Debug
VITE_DEBUG_MODE=true
VITE_LOG_LEVEL=debug
```

### 3.10 .env.production
```env
# API Configuration
VITE_API_BASE_URL=https://api.tradesphere.ai/api/v1
VITE_WS_URL=wss://api.tradesphere.ai/ws

# Application
VITE_APP_NAME=TradeSphere AI
VITE_APP_VERSION=1.0.0
VITE_APP_ENV=production

# Features
VITE_ENABLE_AI_COPILOT=true
VITE_ENABLE_ANALYTICS=true
VITE_ENABLE_NOTIFICATIONS=true

# Debug
VITE_DEBUG_MODE=false
VITE_LOG_LEVEL=error
```

### 3.11 .gitignore
```
# Dependencies
node_modules/
.pnp
.pnp.js

# Testing
coverage/
.nyc_output/

# Production
dist/
build/

# Environment
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Logs
npm-debug.log*
yarn-debug.log*
yarn-error.log*
lerna-debug.log*
.pnpm-debug.log*

# Editor
.vscode/
.idea/
*.suo
*.ntvs*
*.njsproj
*.sln
*.sw?

# OS
.DS_Store
Thumbs.db

# Vite
.vite/

# Playwright
playwright-report/
test-results/
```

### 3.12 index.html
```html
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/logo.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="description" content="TradeSphere AI - Enterprise Trade Finance Platform" />
    <meta name="theme-color" content="#1a77f2" />
    <title>TradeSphere AI</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

---

## 4. Project Structure Setup

### 4.1 Create Folder Structure
```powershell
# Create main directories
New-Item -ItemType Directory -Force -Path src/assets/images
New-Item -ItemType Directory -Force -Path src/assets/icons
New-Item -ItemType Directory -Force -Path src/assets/fonts

New-Item -ItemType Directory -Force -Path src/components/ui
New-Item -ItemType Directory -Force -Path src/components/common
New-Item -ItemType Directory -Force -Path src/components/layout
New-Item -ItemType Directory -Force -Path src/components/forms

New-Item -ItemType Directory -Force -Path src/features/auth/components
New-Item -ItemType Directory -Force -Path src/features/auth/hooks
New-Item -ItemType Directory -Force -Path src/features/auth/store
New-Item -ItemType Directory -Force -Path src/features/auth/services
New-Item -ItemType Directory -Force -Path src/features/auth/types
New-Item -ItemType Directory -Force -Path src/features/auth/pages

New-Item -ItemType Directory -Force -Path src/features/dashboard/components
New-Item -ItemType Directory -Force -Path src/features/dashboard/widgets
New-Item -ItemType Directory -Force -Path src/features/dashboard/services

New-Item -ItemType Directory -Force -Path src/features/lc
New-Item -ItemType Directory -Force -Path src/features/bank-guarantee
New-Item -ItemType Directory -Force -Path src/features/scf
New-Item -ItemType Directory -Force -Path src/features/collection
New-Item -ItemType Directory -Force -Path src/features/customer
New-Item -ItemType Directory -Force -Path src/features/compliance
New-Item -ItemType Directory -Force -Path src/features/risk
New-Item -ItemType Directory -Force -Path src/features/document
New-Item -ItemType Directory -Force -Path src/features/analytics
New-Item -ItemType Directory -Force -Path src/features/workflow
New-Item -ItemType Directory -Force -Path src/features/shipment
New-Item -ItemType Directory -Force -Path src/features/copilot
New-Item -ItemType Directory -Force -Path src/features/knowledge
New-Item -ItemType Directory -Force -Path src/features/security
New-Item -ItemType Directory -Force -Path src/features/settings

New-Item -ItemType Directory -Force -Path src/hooks
New-Item -ItemType Directory -Force -Path src/lib
New-Item -ItemType Directory -Force -Path src/routes
New-Item -ItemType Directory -Force -Path src/store
New-Item -ItemType Directory -Force -Path src/styles
New-Item -ItemType Directory -Force -Path src/types
```

### 4.2 Initial Files

#### src/main.tsx
```typescript
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { ConfigProvider } from 'antd';
import App from './App';
import { antdTheme } from './styles/antdTheme';
import './styles/globals.css';
import './styles/tailwind.css';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ConfigProvider theme={antdTheme}>
          <App />
        </ConfigProvider>
      </BrowserRouter>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </React.StrictMode>
);
```

#### src/App.tsx
```typescript
import React from 'react';
import { Toaster } from 'react-hot-toast';
import AppRoutes from './routes';
import ErrorBoundary from './components/common/ErrorBoundary';

function App() {
  return (
    <ErrorBoundary>
      <AppRoutes />
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
          success: {
            duration: 3000,
            iconTheme: {
              primary: '#52c41a',
              secondary: '#fff',
            },
          },
          error: {
            duration: 4000,
            iconTheme: {
              primary: '#ff4d4f',
              secondary: '#fff',
            },
          },
        }}
      />
    </ErrorBoundary>
  );
}

export default App;
```

#### src/styles/globals.css
```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&display=swap');

:root {
  /* Primary Colors */
  --primary-50: #e6f0ff;
  --primary-500: #1a77f2;
  --primary-600: #0061db;
  
  /* Secondary Colors */
  --secondary-500: #00bcd4;
  
  /* Success */
  --success-main: #28a745;
  
  /* Warning */
  --warning-main: #ffc107;
  
  /* Error */
  --error-main: #dc3545;
  
  /* Gray Scale */
  --gray-50: #fafafa;
  --gray-900: #171717;
  
  /* Background */
  --bg-primary: #ffffff;
  --bg-secondary: #f8fafc;
  
  /* Text */
  --text-primary: #1e293b;
  --text-secondary: #64748b;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  height: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
}

#root {
  height: 100%;
}

/* Custom Scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* Print Styles */
@media print {
  body {
    background: white;
  }
  .no-print {
    display: none !important;
  }
}
```

#### src/styles/tailwind.css
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

#### src/lib/api.ts
```typescript
import axios, { AxiosError, AxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/features/auth/store/authStore';
import { message } from 'antd';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    const { token, user } = useAuthStore.getState();
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    if (user?.tenantId) {
      config.headers['X-Tenant-ID'] = user.tenantId;
    }
    
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const { refreshAuth, logout } = useAuthStore.getState();
    
    if (error.response?.status === 401) {
      try {
        await refreshAuth();
        return apiClient(error.config as AxiosRequestConfig);
      } catch {
        logout();
        window.location.href = '/auth/login';
      }
    }
    
    if (error.response?.status === 403) {
      message.error('You do not have permission to perform this action');
    }
    
    if (error.response?.status === 500) {
      message.error('Server error. Please try again later.');
    }
    
    return Promise.reject(error);
  }
);

export const api = {
  get: <T>(url: string, config?: AxiosRequestConfig) =>
    apiClient.get<T>(url, config).then((res) => res.data),
  
  post: <T>(url: string, data?: any, config?: AxiosRequestConfig) =>
    apiClient.post<T>(url, data, config).then((res) => res.data),
  
  put: <T>(url: string, data?: any, config?: AxiosRequestConfig) =>
    apiClient.put<T>(url, data, config).then((res) => res.data),
  
  patch: <T>(url: string, data?: any, config?: AxiosRequestConfig) =>
    apiClient.patch<T>(url, data, config).then((res) => res.data),
  
  delete: <T>(url: string, config?: AxiosRequestConfig) =>
    apiClient.delete<T>(url, config).then((res) => res.data),
};
```

#### src/lib/utils.ts
```typescript
import { type ClassValue, clsx } from 'clsx';

export function cn(...inputs: ClassValue[]) {
  return clsx(inputs);
}

export function formatCurrency(value: number, currency: string = 'USD'): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
  }).format(value);
}

export function formatDate(date: string | Date): string {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

export function formatDateTime(date: string | Date): string {
  return new Date(date).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function calculateDaysUntilDue(dueDate: string): number {
  const now = new Date();
  const due = new Date(dueDate);
  const diffTime = due.getTime() - now.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

export function truncate(str: string, length: number): string {
  return str.length > length ? str.substring(0, length) + '...' : str;
}

export function downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
}
```

---

## 5. Implementation Roadmap

### Phase 1: Foundation (Week 1)
- ✅ Project setup and configuration
- ✅ Install all dependencies
- ✅ Setup folder structure
- ✅ Create base components (Loading, ErrorBoundary)
- ✅ Setup routing
- ✅ Configure API client
- ✅ Setup state management

### Phase 2: Authentication & Layout (Week 2)
- [ ] Implement authentication module
  - [ ] Login page
  - [ ] MFA setup
  - [ ] Password reset
  - [ ] Auth store
  - [ ] Auth service
- [ ] Create main layout
  - [ ] Header component
  - [ ] Sidebar component
  - [ ] Breadcrumb component
  - [ ] Footer component

### Phase 3: Dashboard (Week 3)
- [ ] Dashboard page
- [ ] Dashboard widgets
  - [ ] Key metrics cards
  - [ ] Transaction chart
  - [ ] LC status chart
  - [ ] Recent activities
  - [ ] Pending approvals
- [ ] Quick actions component

### Phase 4: Core Modules - Part 1 (Weeks 4-5)
- [ ] Customer Management
  - [ ] Customer list
  - [ ] Customer profile
  - [ ] KYC management
  - [ ] Credit limit management
- [ ] Letter of Credit
  - [ ] LC list
  - [ ] Create LC
  - [ ] LC details
  - [ ] Amendment requests
  - [ ] Document checking

### Phase 5: Core Modules - Part 2 (Weeks 6-7)
- [ ] Bank Guarantee
  - [ ] BG list
  - [ ] Create BG
  - [ ] BG details
  - [ ] Claims management
- [ ] Supply Chain Finance
  - [ ] SCF dashboard
  - [ ] Invoice discounting
  - [ ] Early payment

### Phase 6: Support Modules (Weeks 8-9)
- [ ] Document Management
  - [ ] Document library
  - [ ] Upload & OCR
  - [ ] AI extraction
  - [ ] Verification queue
- [ ] Compliance & Screening
  - [ ] Compliance dashboard
  - [ ] Screening checks
  - [ ] Hit management
- [ ] Risk Management
  - [ ] Risk dashboard
  - [ ] Risk assessments

### Phase 7: Advanced Features (Weeks 10-11)
- [ ] Analytics & Reporting
  - [ ] Analytics dashboard
  - [ ] Custom reports
  - [ ] Export functionality
- [ ] Workflow Management
  - [ ] Task list
  - [ ] Approval workflows
  - [ ] SLA monitoring
- [ ] AI Copilot
  - [ ] Chat interface
  - [ ] Intent recognition

### Phase 8: Testing & Optimization (Week 12)
- [ ] Unit tests for all components
- [ ] Integration tests
- [ ] E2E tests with Playwright
- [ ] Performance optimization
- [ ] Accessibility audit
- [ ] Security audit

### Phase 9: Deployment (Week 13)
- [ ] Production build
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Production deployment
- [ ] Monitoring setup

---

## 6. Development Workflow

### 6.1 Starting Development Server
```powershell
npm run dev
```
Application will be available at: http://localhost:3000

### 6.2 Running Tests
```powershell
# Unit tests
npm run test

# Unit tests with UI
npm run test:ui

# E2E tests
npm run test:e2e
```

### 6.3 Code Quality
```powershell
# Run linter
npm run lint

# Fix linting issues
npm run lint:fix

# Format code
npm run format

# Type checking
npm run type-check
```

### 6.4 Building for Production
```powershell
# Production build
npm run build

# Preview production build
npm run preview

# Analyze bundle size
npm run analyze
```

---

## 7. Testing Guide

### 7.1 Vitest Configuration
```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
    css: true,
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
```

### 7.2 Test Setup File
```typescript
// src/test/setup.ts
import '@testing-library/jest-dom';
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';

// Cleanup after each test
afterEach(() => {
  cleanup();
});
```

### 7.3 Playwright Configuration
```typescript
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

---

## 8. Deployment Guide

### 8.1 Docker Deployment
```dockerfile
# Dockerfile
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 8.2 Nginx Configuration
```nginx
# nginx.conf
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

### 8.3 Docker Compose
```yaml
# docker-compose.yml
version: '3.8'

services:
  frontend:
    build: .
    ports:
      - "80:80"
    environment:
      - API_BASE_URL=http://backend:8080/api/v1
    depends_on:
      - backend
    networks:
      - app-network

networks:
  app-network:
    external: true
```

---

## 9. Troubleshooting

### Common Issues

#### Issue 1: Port Already in Use
```powershell
# Find and kill process using port 3000
Get-Process -Id (Get-NetTCPConnection -LocalPort 3000).OwningProcess | Stop-Process
```

#### Issue 2: Module Not Found
```powershell
# Clear node_modules and reinstall
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json
npm install
```

#### Issue 3: TypeScript Errors
```powershell
# Clear TypeScript cache
Remove-Item -Recurse -Force .tsbuildinfo
npm run type-check
```

#### Issue 4: Build Fails
```powershell
# Clear Vite cache
Remove-Item -Recurse -Force node_modules/.vite
npm run build
```

---

## Summary

This implementation guide provides:

✅ **Complete Project Setup** - All configuration files  
✅ **Dependencies** - Full package.json with all libraries  
✅ **Configuration** - Vite, TypeScript, ESLint, Prettier  
✅ **Environment Setup** - Development and production configs  
✅ **Folder Structure** - Organized and scalable  
✅ **Implementation Roadmap** - 13-week development plan  
✅ **Testing Setup** - Unit, integration, and E2E tests  
✅ **Deployment Guide** - Docker and Nginx configuration  
✅ **Troubleshooting** - Common issues and solutions  

**Next Steps:**
1. Run the setup commands in PowerShell
2. Install all dependencies
3. Start the development server
4. Begin Phase 1 implementation
5. Follow the roadmap sequentially

**End of Implementation Guide**

