# TradeSphere AI - Frontend Architecture Document
## Comprehensive Banking & Trade Finance Platform

**Version:** 1.0.0  
**Last Updated:** July 20, 2026  
**Document Type:** Technical Architecture Specification

---

## Table of Contents
1. [Executive Summary](#executive-summary)
2. [Technology Stack](#technology-stack)
3. [Architecture Overview](#architecture-overview)
4. [Design System & Theme](#design-system--theme)
5. [Application Layout Structure](#application-layout-structure)
6. [Dashboard Design](#dashboard-design)
7. [Module-by-Module Specifications](#module-by-module-specifications)
8. [Routing Architecture](#routing-architecture)
9. [API Integration Layer](#api-integration-layer)
10. [State Management](#state-management)
11. [Authentication & Security](#authentication--security)
12. [Performance Optimization](#performance-optimization)
13. [Testing Strategy](#testing-strategy)
14. [Deployment & Build](#deployment--build)

---

## 1. Executive Summary

### 1.1 Application Overview
TradeSphere AI is an enterprise-grade, AI-native trade finance platform designed for banks and financial institutions. The frontend application provides a comprehensive interface for managing:
- **Letters of Credit (LC)** - Complete LC lifecycle management
- **Bank Guarantees (BG)** - BG issuance and management
- **Supply Chain Finance (SCF)** - Invoice financing and early payment
- **Documentary Collections** - Trade document processing
- **Compliance & KYC** - Customer screening and compliance
- **Risk Management** - AI-powered risk assessment
- **Document Management** - AI OCR and document extraction
- **Analytics & Reporting** - Business intelligence dashboards
- **Workflow Management** - Multi-level approval workflows
- **Customer Management** - Complete customer lifecycle
- **AI Copilot** - Conversational AI assistant

### 1.2 Core Principles
- **Enterprise-Grade Security**: JWT authentication, RBAC, MFA support
- **AI-First Approach**: Integrated AI copilot and intelligent automation
- **Responsive Design**: Mobile-first, accessible, and performant
- **Real-time Updates**: WebSocket integration for live data
- **Offline Capability**: Progressive Web App (PWA) features
- **Compliance Ready**: Audit trails, data privacy, regulatory reporting

---

## 2. Technology Stack

### 2.1 Core Framework
```json
{
  "framework": "React 18.3.1",
  "language": "TypeScript 5.5.3",
  "buildTool": "Vite 5.3.3",
  "packageManager": "npm 10.x"
}
```

**Rationale for React:**
- Strong ecosystem for enterprise applications
- Excellent TypeScript support
- Large talent pool and community
- Rich component libraries
- Virtual DOM for performance
- Hooks for clean state management

### 2.2 UI Component Libraries

#### Primary: Ant Design (antd) 5.19.1
```typescript
// Enterprise-grade components with banking themes
import { Layout, Menu, Form, Table, Modal, Drawer, Card } from 'antd';
```
**Features:**
- 50+ high-quality components
- Comprehensive form handling
- Advanced data tables with sorting/filtering
- Built-in theming system
- Excellent TypeScript support
- i18n internationalization
- Accessibility (ARIA) compliant

#### Secondary: shadcn/ui (Radix UI + Tailwind CSS)
```typescript
// Modern, customizable components for advanced UI
import { Button, Dialog, DropdownMenu, Toast } from '@/components/ui';
```
**Features:**
- Headless component architecture
- Full design control
- Copy-paste component model
- Zero runtime overhead
- Modern aesthetics

### 2.3 State Management

#### Primary: Zustand 4.5.4
```typescript
// Lightweight, TypeScript-first state management
import { create } from 'zustand';
import { persist, devtools } from 'zustand/middleware';
```
**Why Zustand:**
- Minimal boilerplate (vs Redux)
- Excellent TypeScript inference
- No providers/context needed
- DevTools integration
- Persistence middleware
- Small bundle size (1KB)

#### Server State: TanStack Query (React Query) 5.49.2
```typescript
// Powerful data fetching and caching
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
```
**Features:**
- Automatic caching and invalidation
- Background refetching
- Optimistic updates
- Pagination and infinite scrolling
- Request deduplication
- DevTools for debugging

### 2.4 Routing
```typescript
// React Router v6.24.1
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
```
**Features:**
- Nested routing
- Lazy loading with Suspense
- Protected routes
- Route-based code splitting
- Dynamic breadcrumbs

### 2.5 Form Management
```typescript
// React Hook Form 7.52.1
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
```
**Features:**
- Minimal re-renders
- Schema validation with Zod
- Easy integration with Ant Design
- Complex form handling
- Field arrays for dynamic forms

### 2.6 Styling Solution
```typescript
// Tailwind CSS 3.4.5 + CSS Modules
import styles from './Component.module.css';
import clsx from 'clsx'; // Conditional classes
```
**Architecture:**
- Tailwind for utility-first styling
- CSS Modules for component-specific styles
- CSS Variables for theming
- Ant Design theme customization

### 2.7 Data Visualization
```typescript
// Apache ECharts 5.5.0
import ReactECharts from 'echarts-for-react';

// Recharts 2.12.7 (fallback)
import { LineChart, BarChart, PieChart } from 'recharts';
```
**Features:**
- Interactive charts with zoom/pan
- Real-time data updates
- Export to PNG/SVG/PDF
- Mobile-responsive
- Dark mode support

### 2.8 Utilities & Tools
```json
{
  "dateHandling": "date-fns 3.6.0",
  "httpClient": "axios 1.7.2",
  "websocket": "socket.io-client 4.7.2",
  "fileUpload": "react-dropzone 14.2.3",
  "pdfGeneration": "jsPDF 2.5.1 + html2canvas",
  "excel": "xlsx 0.18.5",
  "notifications": "react-hot-toast 2.4.1",
  "animations": "framer-motion 11.2.12",
  "icons": "@ant-design/icons 5.3.7 + lucide-react",
  "i18n": "react-i18next 14.1.2"
}
```

### 2.9 Development Tools
```json
{
  "linting": "ESLint 9.6.0 + TypeScript ESLint",
  "formatting": "Prettier 3.3.2",
  "testing": "Vitest 1.6.0 + Testing Library",
  "e2e": "Playwright 1.45.0",
  "devServer": "Vite Dev Server with HMR",
  "bundleAnalysis": "rollup-plugin-visualizer"
}
```

---

## 3. Architecture Overview

### 3.1 High-Level Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                     TradeSphere AI Frontend                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐   │
│  │  Presentation  │  │   Application  │  │  Infrastructure │   │
│  │     Layer      │  │     Layer      │  │      Layer      │   │
│  └────────────────┘  └────────────────┘  └────────────────┘   │
│          │                   │                    │             │
│          │                   │                    │             │
│  ┌───────▼────────┐  ┌──────▼────────┐  ┌───────▼────────┐   │
│  │   Components   │  │  State Stores │  │   API Client   │   │
│  │   - Pages      │  │  - Zustand    │  │   - Axios      │   │
│  │   - Layouts    │  │  - React      │  │   - Interceptor│   │
│  │   - UI         │  │    Query      │  │   - Error      │   │
│  └────────────────┘  └───────────────┘  │     Handling   │   │
│                                          └────────────────┘   │
│  ┌────────────────────────────────────────────────────────┐   │
│  │                   Shared Services                       │   │
│  │  - Auth   - Validation   - Notifications   - Utils     │   │
│  └────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ REST APIs / WebSocket
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                    TradeSphere Backend API                       │
│        http://localhost:8080/api/v1/ (Spring Boot)             │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Folder Structure
```
tradeasset-frontend/
├── public/
│   ├── favicon.ico
│   ├── logo.svg
│   ├── manifest.json              # PWA manifest
│   └── robots.txt
│
├── src/
│   ├── assets/                    # Static assets
│   │   ├── images/
│   │   ├── icons/
│   │   └── fonts/
│   │
│   ├── components/                # Reusable components
│   │   ├── ui/                    # shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── dialog.tsx
│   │   │   ├── dropdown-menu.tsx
│   │   │   └── toast.tsx
│   │   ├── common/                # Shared components
│   │   │   ├── ErrorBoundary.tsx
│   │   │   ├── Loading.tsx
│   │   │   ├── ProtectedRoute.tsx
│   │   │   ├── PageHeader.tsx
│   │   │   ├── StatusBadge.tsx
│   │   │   └── DataTable.tsx
│   │   ├── layout/                # Layout components
│   │   │   ├── MainLayout.tsx
│   │   │   ├── AuthLayout.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Header.tsx
│   │   │   └── Breadcrumb.tsx
│   │   └── forms/                 # Form components
│   │       ├── FormInput.tsx
│   │       ├── FormSelect.tsx
│   │       ├── FormDatePicker.tsx
│   │       └── FormUpload.tsx
│   │
│   ├── features/                  # Feature modules
│   │   ├── auth/                  # Authentication
│   │   │   ├── components/
│   │   │   │   ├── LoginForm.tsx
│   │   │   │   ├── MfaSetup.tsx
│   │   │   │   └── PasswordReset.tsx
│   │   │   ├── hooks/
│   │   │   │   └── useAuth.ts
│   │   │   ├── store/
│   │   │   │   └── authStore.ts
│   │   │   ├── services/
│   │   │   │   └── authService.ts
│   │   │   └── types/
│   │   │       └── auth.types.ts
│   │   │
│   │   ├── dashboard/             # Main Dashboard
│   │   │   ├── components/
│   │   │   ├── Dashboard.tsx
│   │   │   └── widgets/
│   │   │
│   │   ├── lc/                    # Letter of Credit
│   │   │   ├── components/
│   │   │   ├── pages/
│   │   │   ├── hooks/
│   │   │   ├── services/
│   │   │   └── types/
│   │   │
│   │   ├── bank-guarantee/        # Bank Guarantee
│   │   ├── scf/                   # Supply Chain Finance
│   │   ├── collection/            # Documentary Collection
│   │   ├── customer/              # Customer Management
│   │   ├── compliance/            # Compliance & KYC
│   │   ├── risk/                  # Risk Assessment
│   │   ├── document/              # Document Management
│   │   ├── analytics/             # Analytics & Reports
│   │   ├── workflow/              # Workflow Management
│   │   ├── shipment/              # Shipment Tracking
│   │   ├── copilot/               # AI Copilot
│   │   ├── knowledge/             # Knowledge Base
│   │   ├── security/              # SSO & MFA
│   │   └── settings/              # User Settings
│   │
│   ├── hooks/                     # Shared hooks
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   ├── usePermissions.ts
│   │   └── useNotification.ts
│   │
│   ├── lib/                       # Utilities & config
│   │   ├── api.ts                 # Axios instance
│   │   ├── constants.ts           # App constants
│   │   ├── utils.ts               # Helper functions
│   │   └── validators.ts          # Zod schemas
│   │
│   ├── routes/                    # Route definitions
│   │   ├── index.tsx              # Main routes
│   │   ├── ProtectedRoutes.tsx
│   │   └── routeConfig.ts
│   │
│   ├── store/                     # Global state
│   │   ├── index.ts
│   │   ├── appStore.ts
│   │   └── notificationStore.ts
│   │
│   ├── styles/                    # Global styles
│   │   ├── globals.css
│   │   ├── theme.css
│   │   ├── antd-overrides.css
│   │   └── tailwind.css
│   │
│   ├── types/                     # TypeScript types
│   │   ├── api.types.ts
│   │   ├── common.types.ts
│   │   └── index.ts
│   │
│   ├── App.tsx                    # Root component
│   ├── main.tsx                   # Entry point
│   └── vite-env.d.ts              # Vite types
│
├── .env.development               # Dev environment
├── .env.production                # Prod environment
├── .eslintrc.json                 # ESLint config
├── .prettierrc                    # Prettier config
├── index.html                     # HTML entry
├── package.json                   # Dependencies
├── tsconfig.json                  # TypeScript config
├── tailwind.config.js             # Tailwind config
├── vite.config.ts                 # Vite config
└── README.md                      # Project docs
```

---

## 4. Design System & Theme

### 4.1 Color Palette

#### Primary Colors (Banking Professional)
```css
:root {
  /* Primary - Deep Blue (Trust & Stability) */
  --primary-50: #e6f0ff;
  --primary-100: #bfdaff;
  --primary-200: #99c4ff;
  --primary-300: #70adff;
  --primary-400: #4d9bff;
  --primary-500: #1a77f2;  /* Main Primary */
  --primary-600: #0061db;
  --primary-700: #004fb5;
  --primary-800: #003d8f;
  --primary-900: #002b69;

  /* Secondary - Teal (Innovation & Growth) */
  --secondary-50: #e0f7fa;
  --secondary-100: #b3ecf2;
  --secondary-200: #80deea;
  --secondary-300: #4dd0e1;
  --secondary-400: #26c6da;
  --secondary-500: #00bcd4;  /* Main Secondary */
  --secondary-600: #00acc1;
  --secondary-700: #0097a7;
  --secondary-800: #00838f;
  --secondary-900: #006064;

  /* Accent - Gold (Premium & Value) */
  --accent-50: #fff9e5;
  --accent-100: #fff0b3;
  --accent-200: #ffe680;
  --accent-300: #ffdd4d;
  --accent-400: #ffd426;
  --accent-500: #ffca00;  /* Main Accent */
  --accent-600: #ffb700;
  --accent-700: #ffa000;
  --accent-800: #ff8900;
  --accent-900: #ff6d00;
}
```

#### Semantic Colors
```css
:root {
  /* Success - Green */
  --success-light: #d4edda;
  --success-main: #28a745;
  --success-dark: #1e7e34;

  /* Warning - Amber */
  --warning-light: #fff3cd;
  --warning-main: #ffc107;
  --warning-dark: #e5a100;

  /* Error - Red */
  --error-light: #f8d7da;
  --error-main: #dc3545;
  --error-dark: #bd2130;

  /* Info - Blue */
  --info-light: #d1ecf1;
  --info-main: #17a2b8;
  --info-dark: #138496;
}
```

#### Neutral Colors (Gray Scale)
```css
:root {
  --gray-50: #fafafa;
  --gray-100: #f5f5f5;
  --gray-200: #e5e5e5;
  --gray-300: #d4d4d4;
  --gray-400: #a3a3a3;
  --gray-500: #737373;
  --gray-600: #525252;
  --gray-700: #404040;
  --gray-800: #262626;
  --gray-900: #171717;

  /* Background Colors */
  --bg-primary: #ffffff;
  --bg-secondary: #f8fafc;
  --bg-tertiary: #f1f5f9;
  --bg-elevated: #ffffff;

  /* Text Colors */
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --text-tertiary: #94a3b8;
  --text-disabled: #cbd5e1;
}
```

#### Dark Mode Theme
```css
[data-theme='dark'] {
  --bg-primary: #0f172a;
  --bg-secondary: #1e293b;
  --bg-tertiary: #334155;
  --bg-elevated: #1e293b;

  --text-primary: #f1f5f9;
  --text-secondary: #cbd5e1;
  --text-tertiary: #94a3b8;
  --text-disabled: #64748b;

  /* Adjust primary colors for dark mode */
  --primary-500: #3b82f6;
  --secondary-500: #22d3ee;
}
```

### 4.2 Typography

#### Font Family
```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&display=swap');

:root {
  --font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
  --font-mono: 'JetBrains Mono', 'Courier New', monospace;
}
```

#### Font Scales
```css
:root {
  /* Font Sizes */
  --text-xs: 0.75rem;      /* 12px */
  --text-sm: 0.875rem;     /* 14px */
  --text-base: 1rem;       /* 16px */
  --text-lg: 1.125rem;     /* 18px */
  --text-xl: 1.25rem;      /* 20px */
  --text-2xl: 1.5rem;      /* 24px */
  --text-3xl: 1.875rem;    /* 30px */
  --text-4xl: 2.25rem;     /* 36px */
  --text-5xl: 3rem;        /* 48px */

  /* Font Weights */
  --font-light: 300;
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;
  --font-extrabold: 800;

  /* Line Heights */
  --leading-none: 1;
  --leading-tight: 1.25;
  --leading-normal: 1.5;
  --leading-relaxed: 1.625;
  --leading-loose: 2;
}
```

### 4.3 Spacing System (8px Base)
```css
:root {
  --space-0: 0;
  --space-1: 0.25rem;   /* 4px */
  --space-2: 0.5rem;    /* 8px */
  --space-3: 0.75rem;   /* 12px */
  --space-4: 1rem;      /* 16px */
  --space-5: 1.25rem;   /* 20px */
  --space-6: 1.5rem;    /* 24px */
  --space-8: 2rem;      /* 32px */
  --space-10: 2.5rem;   /* 40px */
  --space-12: 3rem;     /* 48px */
  --space-16: 4rem;     /* 64px */
  --space-20: 5rem;     /* 80px */
  --space-24: 6rem;     /* 96px */
}
```

### 4.4 Border & Shadows
```css
:root {
  /* Border Radius */
  --radius-sm: 0.25rem;   /* 4px */
  --radius-md: 0.375rem;  /* 6px */
  --radius-lg: 0.5rem;    /* 8px */
  --radius-xl: 0.75rem;   /* 12px */
  --radius-2xl: 1rem;     /* 16px */
  --radius-full: 9999px;

  /* Shadows */
  --shadow-xs: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-sm: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
  --shadow-2xl: 0 25px 50px -12px rgb(0 0 0 / 0.25);
  --shadow-inner: inset 0 2px 4px 0 rgb(0 0 0 / 0.05);
}
```

### 4.5 Ant Design Theme Configuration
```typescript
// src/styles/antdTheme.ts
import type { ThemeConfig } from 'antd';

export const antdTheme: ThemeConfig = {
  token: {
    // Color Tokens
    colorPrimary: '#1a77f2',
    colorSuccess: '#28a745',
    colorWarning: '#ffc107',
    colorError: '#dc3545',
    colorInfo: '#17a2b8',
    colorTextBase: '#1e293b',
    colorBgBase: '#ffffff',

    // Typography
    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
    fontSize: 14,
    fontSizeHeading1: 38,
    fontSizeHeading2: 30,
    fontSizeHeading3: 24,
    fontSizeHeading4: 20,
    fontSizeHeading5: 16,

    // Spacing
    marginXS: 8,
    marginSM: 12,
    margin: 16,
    marginMD: 20,
    marginLG: 24,
    marginXL: 32,
    marginXXL: 48,

    // Border & Radius
    borderRadius: 6,
    borderRadiusLG: 8,
    borderRadiusSM: 4,

    // Layout
    controlHeight: 38,
    controlHeightLG: 42,
    controlHeightSM: 30,
  },
  components: {
    Layout: {
      headerBg: '#ffffff',
      headerHeight: 64,
      siderBg: '#001529',
      bodyBg: '#f0f2f5',
    },
    Menu: {
      darkItemBg: '#001529',
      darkItemColor: 'rgba(255, 255, 255, 0.65)',
      darkItemHoverBg: '#1890ff',
      darkItemSelectedBg: '#1890ff',
    },
    Button: {
      controlHeight: 38,
      controlHeightLG: 42,
      controlHeightSM: 30,
      fontWeight: 500,
    },
    Table: {
      headerBg: '#fafafa',
      headerColor: '#1e293b',
      rowHoverBg: '#f5f5f5',
    },
    Card: {
      borderRadiusLG: 8,
      paddingLG: 24,
    },
  },
};
```

---

## 5. Application Layout Structure

### 5.1 Main Layout Component
```typescript
// src/components/layout/MainLayout.tsx
import React, { useState } from 'react';
import { Layout, theme } from 'antd';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import Breadcrumb from './Breadcrumb';
import CopilotWidget from '@/features/copilot/components/CopilotWidget';

const { Content } = Layout;

const MainLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {/* Sidebar Navigation */}
      <Sidebar collapsed={collapsed} onCollapse={setCollapsed} />

      <Layout>
        {/* Top Header */}
        <Header onMenuToggle={() => setCollapsed(!collapsed)} />

        {/* Main Content Area */}
        <Content style={{ margin: '16px 24px', overflow: 'initial' }}>
          {/* Breadcrumb Navigation */}
          <Breadcrumb />

          {/* Page Content */}
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
              marginTop: 16,
            }}
          >
            <Outlet />
          </div>
        </Content>

        {/* AI Copilot Widget (Floating) */}
        <CopilotWidget />
      </Layout>
    </Layout>
  );
};

export default MainLayout;
```

### 5.2 Header Component
```typescript
// src/components/layout/Header.tsx
import React from 'react';
import { Layout, Avatar, Badge, Dropdown, Button, Space } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  BellOutlined,
  SearchOutlined,
  GlobalOutlined,
  SettingOutlined,
  UserOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import NotificationPanel from './NotificationPanel';
import GlobalSearch from './GlobalSearch';

const { Header: AntHeader } = Layout;

interface HeaderProps {
  onMenuToggle: () => void;
}

const Header: React.FC<HeaderProps> = ({ onMenuToggle }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [searchVisible, setSearchVisible] = useState(false);
  const [notificationVisible, setNotificationVisible] = useState(false);

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: 'My Profile',
      onClick: () => navigate('/settings/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: 'Settings',
      onClick: () => navigate('/settings'),
    },
    { type: 'divider' },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Logout',
      onClick: logout,
    },
  ];

  return (
    <AntHeader
      style={{
        padding: '0 24px',
        background: '#fff',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        boxShadow: '0 1px 4px rgba(0,21,41,.08)',
      }}
    >
      {/* Left Section */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={onMenuToggle}
          style={{ fontSize: '16px', width: 48, height: 48 }}
        />
        
        {/* Global Search */}
        <Button
          type="text"
          icon={<SearchOutlined />}
          onClick={() => setSearchVisible(true)}
          style={{ fontSize: '16px' }}
        >
          Search (Ctrl+K)
        </Button>
      </div>

      {/* Right Section */}
      <Space size="large">
        {/* Language Selector */}
        <Dropdown
          menu={{
            items: [
              { key: 'en', label: 'English' },
              { key: 'ar', label: 'العربية' },
              { key: 'zh', label: '中文' },
            ],
          }}
        >
          <Button type="text" icon={<GlobalOutlined />} />
        </Dropdown>

        {/* Notifications */}
        <Badge count={5} offset={[-5, 5]}>
          <Button
            type="text"
            icon={<BellOutlined />}
            onClick={() => setNotificationVisible(true)}
          />
        </Badge>

        {/* User Profile Dropdown */}
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
          <Space style={{ cursor: 'pointer' }}>
            <Avatar src={user?.avatarUrl} icon={<UserOutlined />} />
            <span style={{ fontWeight: 500 }}>{user?.name}</span>
          </Space>
        </Dropdown>
      </Space>

      {/* Global Search Modal */}
      <GlobalSearch
        visible={searchVisible}
        onClose={() => setSearchVisible(false)}
      />

      {/* Notification Panel */}
      <NotificationPanel
        visible={notificationVisible}
        onClose={() => setNotificationVisible(false)}
      />
    </AntHeader>
  );
};

export default Header;
```

### 5.3 Sidebar Navigation
```typescript
// src/components/layout/Sidebar.tsx
import React from 'react';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  BankOutlined,
  SafetyOutlined,
  DollarOutlined,
  FileTextOutlined,
  UserOutlined,
  CheckCircleOutlined,
  AlertOutlined,
  FileSearchOutlined,
  BarChartOutlined,
  ApartmentOutlined,
  CarOutlined,
  RobotOutlined,
  BookOutlined,
  LockOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { usePermissions } from '@/hooks/usePermissions';

const { Sider } = Layout;

interface SidebarProps {
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ collapsed, onCollapse }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { hasPermission } = usePermissions();

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: 'Dashboard',
      permission: 'VIEW_DASHBOARD',
    },
    {
      key: '/lc',
      icon: <BankOutlined />,
      label: 'Letter of Credit',
      permission: 'VIEW_LC',
      children: [
        { key: '/lc/list', label: 'LC List' },
        { key: '/lc/create', label: 'New LC Application' },
        { key: '/lc/amendments', label: 'Amendments' },
        { key: '/lc/documents', label: 'Document Checking' },
        { key: '/lc/expiring', label: 'Expiring LCs' },
      ],
    },
    {
      key: '/bank-guarantee',
      icon: <SafetyOutlined />,
      label: 'Bank Guarantee',
      permission: 'VIEW_BG',
      children: [
        { key: '/bank-guarantee/list', label: 'BG List' },
        { key: '/bank-guarantee/create', label: 'New BG Request' },
        { key: '/bank-guarantee/claims', label: 'Claims Management' },
        { key: '/bank-guarantee/expiring', label: 'Expiring BGs' },
      ],
    },
    {
      key: '/scf',
      icon: <DollarOutlined />,
      label: 'Supply Chain Finance',
      permission: 'VIEW_SCF',
      children: [
        { key: '/scf/list', label: 'SCF List' },
        { key: '/scf/create', label: 'New Financing Request' },
        { key: '/scf/invoices', label: 'Invoice Discounting' },
        { key: '/scf/early-payment', label: 'Early Payment' },
      ],
    },
    {
      key: '/collection',
      icon: <FileTextOutlined />,
      label: 'Documentary Collection',
      permission: 'VIEW_COLLECTION',
      children: [
        { key: '/collection/list', label: 'Collection List' },
        { key: '/collection/create', label: 'New Collection' },
      ],
    },
    {
      key: '/customer',
      icon: <UserOutlined />,
      label: 'Customer Management',
      permission: 'VIEW_CUSTOMER',
      children: [
        { key: '/customer/list', label: 'Customer List' },
        { key: '/customer/create', label: 'New Customer' },
        { key: '/customer/kyc', label: 'KYC Management' },
        { key: '/customer/credit-limit', label: 'Credit Limits' },
      ],
    },
    {
      key: '/compliance',
      icon: <CheckCircleOutlined />,
      label: 'Compliance & Screening',
      permission: 'VIEW_COMPLIANCE',
      children: [
        { key: '/compliance/checks', label: 'Screening Checks' },
        { key: '/compliance/hits', label: 'Screening Hits' },
        { key: '/compliance/reports', label: 'Compliance Reports' },
      ],
    },
    {
      key: '/risk',
      icon: <AlertOutlined />,
      label: 'Risk Management',
      permission: 'VIEW_RISK',
      children: [
        { key: '/risk/assessments', label: 'Risk Assessments' },
        { key: '/risk/monitoring', label: 'Risk Monitoring' },
        { key: '/risk/reports', label: 'Risk Reports' },
      ],
    },
    {
      key: '/document',
      icon: <FileSearchOutlined />,
      label: 'Document Management',
      permission: 'VIEW_DOCUMENT',
      children: [
        { key: '/document/library', label: 'Document Library' },
        { key: '/document/upload', label: 'Upload Documents' },
        { key: '/document/ocr', label: 'AI Extraction' },
        { key: '/document/verification', label: 'Verification Queue' },
      ],
    },
    {
      key: '/analytics',
      icon: <BarChartOutlined />,
      label: 'Analytics & Reports',
      permission: 'VIEW_ANALYTICS',
      children: [
        { key: '/analytics/dashboard', label: 'Analytics Dashboard' },
        { key: '/analytics/transactions', label: 'Transaction Analytics' },
        { key: '/analytics/compliance', label: 'Compliance Analytics' },
        { key: '/analytics/reports', label: 'Custom Reports' },
      ],
    },
    {
      key: '/workflow',
      icon: <ApartmentOutlined />,
      label: 'Workflow Management',
      permission: 'VIEW_WORKFLOW',
      children: [
        { key: '/workflow/tasks', label: 'My Tasks' },
        { key: '/workflow/approvals', label: 'Pending Approvals' },
        { key: '/workflow/monitoring', label: 'Workflow Monitor' },
      ],
    },
    {
      key: '/shipment',
      icon: <CarOutlined />,
      label: 'Shipment Tracking',
      permission: 'VIEW_SHIPMENT',
    },
    { type: 'divider' },
    {
      key: '/copilot',
      icon: <RobotOutlined />,
      label: 'AI Copilot',
    },
    {
      key: '/knowledge',
      icon: <BookOutlined />,
      label: 'Knowledge Base',
    },
    {
      key: '/security',
      icon: <LockOutlined />,
      label: 'Security',
      permission: 'ADMIN',
      children: [
        { key: '/security/sso', label: 'Single Sign-On' },
        { key: '/security/mfa', label: 'Multi-Factor Auth' },
      ],
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: 'Settings',
    },
  ].filter(item => !item.permission || hasPermission(item.permission));

  return (
    <Sider
      collapsible
      collapsed={collapsed}
      onCollapse={onCollapse}
      width={260}
      theme="dark"
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'sticky',
        top: 0,
        left: 0,
      }}
    >
      {/* Logo */}
      <div
        style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
        }}
      >
        {collapsed ? (
          <BankOutlined style={{ fontSize: 24, color: '#1890ff' }} />
        ) : (
          <div style={{ color: '#fff', fontSize: 18, fontWeight: 700 }}>
            TradeSphere AI
          </div>
        )}
      </div>

      {/* Navigation Menu */}
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  );
};

export default Sidebar;
```

---

## 6. Dashboard Design

### 6.1 Dashboard Overview
The dashboard is the heart of the application, providing real-time insights and quick access to key functionalities.

### 6.2 Dashboard Layout
```typescript
// src/features/dashboard/Dashboard.tsx
import React, { useMemo } from 'react';
import { Row, Col, Card, Statistic, Typography, Space, Button } from 'antd';
import {
  ArrowUpOutlined,
  ArrowDownOutlined,
  DollarOutlined,
  BankOutlined,
  SafetyOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import { useQuery } from '@tanstack/react-query';
import QuickActions from './widgets/QuickActions';
import TransactionChart from './widgets/TransactionChart';
import LCStatusChart from './widgets/LCStatusChart';
import RecentActivities from './widgets/RecentActivities';
import PendingApprovals from './widgets/PendingApprovals';
import ExpiringInstruments from './widgets/ExpiringInstruments';
import ComplianceAlerts from './widgets/ComplianceAlerts';
import RiskIndicators from './widgets/RiskIndicators';
import { dashboardService } from './services/dashboardService';

const { Title } = Typography;

const Dashboard: React.FC = () => {
  // Fetch dashboard metrics
  const { data: metrics, isLoading } = useQuery({
    queryKey: ['dashboard-metrics'],
    queryFn: dashboardService.getDashboardMetrics,
    refetchInterval: 30000, // Refresh every 30 seconds
  });

  return (
    <div className="dashboard-container">
      {/* Page Header */}
      <div className="dashboard-header">
        <Title level={2}>Dashboard</Title>
        <Space>
          <Button type="primary">Export Report</Button>
          <Button>Customize View</Button>
        </Space>
      </div>

      {/* Key Metrics - Top Row */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Transaction Value"
              value={metrics?.totalTransactionValue || 0}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<DollarOutlined />}
              suffix="M USD"
            />
            <div style={{ marginTop: 8 }}>
              <ArrowUpOutlined style={{ color: '#3f8600' }} />
              <span style={{ color: '#3f8600' }}> +15.3%</span>
              <span style={{ color: '#999', marginLeft: 8 }}>vs last month</span>
            </div>
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Active LCs"
              value={metrics?.activeLCCount || 0}
              prefix={<BankOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
            <div style={{ marginTop: 8 }}>
              <span style={{ color: '#999' }}>
                {metrics?.pendingLCCount || 0} pending approval
              </span>
            </div>
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Bank Guarantees"
              value={metrics?.activeBGCount || 0}
              prefix={<SafetyOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
            <div style={{ marginTop: 8 }}>
              <span style={{ color: '#999' }}>
                {metrics?.expiringBGCount || 0} expiring soon
              </span>
            </div>
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Pending Tasks"
              value={metrics?.pendingTaskCount || 0}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
            <div style={{ marginTop: 8 }}>
              <span style={{ color: '#999' }}>
                {metrics?.highPriorityTaskCount || 0} high priority
              </span>
            </div>
          </Card>
        </Col>
      </Row>

      {/* Quick Actions */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24}>
          <QuickActions />
        </Col>
      </Row>

      {/* Charts & Visualizations */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} lg={16}>
          <Card title="Transaction Volume Trend" extra={<Button size="small">View Details</Button>}>
            <TransactionChart />
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card title="LC Status Distribution">
            <LCStatusChart />
          </Card>
        </Col>
      </Row>

      {/* Activity & Approvals */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} lg={12}>
          <Card title="Recent Activities" extra={<Button size="small" type="link">View All</Button>}>
            <RecentActivities />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Pending Approvals" extra={<Button size="small" type="link">View All</Button>}>
            <PendingApprovals />
          </Card>
        </Col>
      </Row>

      {/* Alerts & Risk Indicators */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} lg={12}>
          <Card title="Expiring Instruments" extra={<Button size="small" danger>View All</Button>}>
            <ExpiringInstruments />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Compliance Alerts" extra={<Button size="small" danger>View All</Button>}>
            <ComplianceAlerts />
          </Card>
        </Col>
      </Row>

      {/* Risk Monitoring */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24}>
          <Card title="Risk Indicators">
            <RiskIndicators />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
```

### 6.3 Dashboard Widgets

#### Quick Actions Widget
```typescript
// src/features/dashboard/widgets/QuickActions.tsx
import React from 'react';
import { Card, Row, Col, Button } from 'antd';
import {
  PlusOutlined,
  FileAddOutlined,
  SafetyOutlined,
  UserAddOutlined,
  FileSearchOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const QuickActions: React.FC = () => {
  const navigate = useNavigate();

  const actions = [
    {
      icon: <PlusOutlined />,
      title: 'New LC Application',
      color: '#1890ff',
      path: '/lc/create',
    },
    {
      icon: <SafetyOutlined />,
      title: 'New Bank Guarantee',
      color: '#52c41a',
      path: '/bank-guarantee/create',
    },
    {
      icon: <FileAddOutlined />,
      title: 'New SCF Request',
      color: '#faad14',
      path: '/scf/create',
    },
    {
      icon: <UserAddOutlined />,
      title: 'Add Customer',
      color: '#722ed1',
      path: '/customer/create',
    },
    {
      icon: <FileSearchOutlined />,
      title: 'Upload Document',
      color: '#13c2c2',
      path: '/document/upload',
    },
    {
      icon: <CheckCircleOutlined />,
      title: 'Compliance Check',
      color: '#eb2f96',
      path: '/compliance/checks',
    },
  ];

  return (
    <Card title="Quick Actions" bordered={false}>
      <Row gutter={[16, 16]}>
        {actions.map((action, index) => (
          <Col xs={12} sm={8} md={6} lg={4} key={index}>
            <Button
              type="dashed"
              block
              style={{
                height: 100,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 8,
              }}
              onClick={() => navigate(action.path)}
            >
              <div style={{ fontSize: 28, color: action.color }}>
                {action.icon}
              </div>
              <div style={{ fontSize: 12, textAlign: 'center' }}>
                {action.title}
              </div>
            </Button>
          </Col>
        ))}
      </Row>
    </Card>
  );
};

export default QuickActions;
```

#### Transaction Chart Widget
```typescript
// src/features/dashboard/widgets/TransactionChart.tsx
import React, { useMemo } from 'react';
import ReactECharts from 'echarts-for-react';
import { useQuery } from '@tanstack/react-query';
import { Spin } from 'antd';
import { dashboardService } from '../services/dashboardService';

const TransactionChart: React.FC = () => {
  const { data, isLoading } = useQuery({
    queryKey: ['transaction-analytics'],
    queryFn: dashboardService.getTransactionAnalytics,
  });

  const option = useMemo(() => ({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
    },
    legend: {
      data: ['LC', 'Bank Guarantee', 'SCF', 'Collection'],
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      data: data?.months || [],
    },
    yAxis: {
      type: 'value',
      name: 'Transaction Value (USD M)',
    },
    series: [
      {
        name: 'LC',
        type: 'bar',
        stack: 'total',
        data: data?.lcValues || [],
        itemStyle: { color: '#1890ff' },
      },
      {
        name: 'Bank Guarantee',
        type: 'bar',
        stack: 'total',
        data: data?.bgValues || [],
        itemStyle: { color: '#52c41a' },
      },
      {
        name: 'SCF',
        type: 'bar',
        stack: 'total',
        data: data?.scfValues || [],
        itemStyle: { color: '#faad14' },
      },
      {
        name: 'Collection',
        type: 'bar',
        stack: 'total',
        data: data?.collectionValues || [],
        itemStyle: { color: '#722ed1' },
      },
    ],
  }), [data]);

  if (isLoading) return <Spin />;

  return <ReactECharts option={option} style={{ height: 350 }} />;
};

export default TransactionChart;
```

---

## 7. Module-by-Module Specifications

### MODULE 1: AUTHENTICATION & IDENTITY

#### 7.1.1 Login Page
```typescript
// src/features/auth/pages/LoginPage.tsx
import React, { useState } from 'react';
import { Form, Input, Button, Checkbox, Card, Typography, Alert, Divider } from 'antd';
import { UserOutlined, LockOutlined, GoogleOutlined, MicrosoftOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import styles from './LoginPage.module.css';

const { Title, Text, Link } = Typography;

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { login, loginWithSSO } = useAuth();
  const navigate = useNavigate();

  const onFinish = async (values: { username: string; password: string; remember: boolean }) => {
    try {
      setLoading(true);
      await login(values.username, values.password, values.remember);
      navigate('/dashboard');
    } catch (error) {
      message.error('Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.loginContainer}>
      <Card className={styles.loginCard}>
        <div className={styles.logo}>
          <BankOutlined style={{ fontSize: 48, color: '#1890ff' }} />
        </div>
        
        <Title level={2} style={{ textAlign: 'center' }}>TradeSphere AI</Title>
        <Text type="secondary" style={{ display: 'block', textAlign: 'center', marginBottom: 24 }}>
          Enterprise Trade Finance Platform
        </Text>

        <Form
          name="login"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: 'Please enter your username' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Username" />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Please enter your password' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Password" />
          </Form.Item>

          <Form.Item>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Form.Item name="remember" valuePropName="checked" noStyle>
                <Checkbox>Remember me</Checkbox>
              </Form.Item>
              <Link onClick={() => navigate('/auth/forgot-password')}>
                Forgot password?
              </Link>
            </div>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading}>
              Sign In
            </Button>
          </Form.Item>
        </Form>

        <Divider>Or continue with</Divider>

        <div style={{ display: 'flex', gap: 8 }}>
          <Button
            icon={<GoogleOutlined />}
            block
            onClick={() => loginWithSSO('google')}
          >
            Google
          </Button>
          <Button
            icon={<MicrosoftOutlined />}
            block
            onClick={() => loginWithSSO('microsoft')}
          >
            Microsoft
          </Button>
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;
```

#### 7.1.2 Auth Store
```typescript
// src/features/auth/store/authStore.ts
import { create } from 'zustand';
import { persist, devtools } from 'zustand/middleware';
import { authService } from '../services/authService';

interface User {
  id: string;
  username: string;
  name: string;
  email: string;
  roles: string[];
  permissions: string[];
  avatarUrl?: string;
  tenantId: string;
}

interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  
  // Actions
  login: (username: string, password: string, remember: boolean) => Promise<void>;
  logout: () => Promise<void>;
  refreshAuth: () => Promise<void>;
  updateUser: (user: Partial<User>) => void;
}

export const useAuthStore = create<AuthState>()(
  devtools(
    persist(
      (set, get) => ({
        user: null,
        token: null,
        refreshToken: null,
        isAuthenticated: false,
        isLoading: false,

        login: async (username, password, remember) => {
          set({ isLoading: true });
          try {
            const response = await authService.login(username, password);
            set({
              user: response.user,
              token: response.token,
              refreshToken: response.refreshToken,
              isAuthenticated: true,
              isLoading: false,
            });
          } catch (error) {
            set({ isLoading: false });
            throw error;
          }
        },

        logout: async () => {
          await authService.logout();
          set({
            user: null,
            token: null,
            refreshToken: null,
            isAuthenticated: false,
          });
        },

        refreshAuth: async () => {
          const { refreshToken } = get();
          if (!refreshToken) throw new Error('No refresh token');
          
          const response = await authService.refreshToken(refreshToken);
          set({
            token: response.token,
            refreshToken: response.refreshToken,
          });
        },

        updateUser: (userData) => {
          set((state) => ({
            user: state.user ? { ...state.user, ...userData } : null,
          }));
        },
      }),
      {
        name: 'auth-storage',
        partialize: (state) => ({
          user: state.user,
          token: state.token,
          refreshToken: state.refreshToken,
          isAuthenticated: state.isAuthenticated,
        }),
      }
    )
  )
);
```

---

### MODULE 2: LETTER OF CREDIT (LC)

#### 7.2.1 LC List Page
```typescript
// src/features/lc/pages/LCListPage.tsx
import React, { useState } from 'react';
import { Table, Button, Space, Tag, Input, Select, DatePicker, Card } from 'antd';
import { PlusOutlined, SearchOutlined, FilterOutlined, ExportOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { lcService } from '../services/lcService';
import { LCStatus, LCType } from '../types/lc.types';
import PageHeader from '@/components/common/PageHeader';
import StatusBadge from '@/components/common/StatusBadge';

const { RangePicker } = DatePicker;

const LCListPage: React.FC = () => {
  const navigate = useNavigate();
  const [filters, setFilters] = useState({
    search: '',
    status: undefined,
    type: undefined,
    dateRange: undefined,
  });

  const { data, isLoading } = useQuery({
    queryKey: ['lc-list', filters],
    queryFn: () => lcService.getAll(filters),
  });

  const columns = [
    {
      title: 'LC Number',
      dataIndex: 'lcNumber',
      key: 'lcNumber',
      render: (text: string, record: any) => (
        <Button type="link" onClick={() => navigate(`/lc/${record.id}`)}>
          {text}
        </Button>
      ),
    },
    {
      title: 'Applicant',
      dataIndex: ['applicant', 'name'],
      key: 'applicant',
    },
    {
      title: 'Beneficiary',
      dataIndex: ['beneficiary', 'name'],
      key: 'beneficiary',
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount: any) => `${amount.currency} ${amount.value.toLocaleString()}`,
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (type: LCType) => <Tag>{type}</Tag>,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: LCStatus) => <StatusBadge status={status} />,
    },
    {
      title: 'Issue Date',
      dataIndex: 'issueDate',
      key: 'issueDate',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Expiry Date',
      dataIndex: 'expiryDate',
      key: 'expiryDate',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: any) => (
        <Space>
          <Button size="small" onClick={() => navigate(`/lc/${record.id}`)}>
            View
          </Button>
          <Button size="small" onClick={() => navigate(`/lc/${record.id}/amend`)}>
            Amend
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Letter of Credit"
        extra={[
          <Button key="export" icon={<ExportOutlined />}>Export</Button>,
          <Button key="create" type="primary" icon={<PlusOutlined />} onClick={() => navigate('/lc/create')}>
            New LC Application
          </Button>,
        ]}
      />

      <Card>
        {/* Filters */}
        <Space style={{ marginBottom: 16 }} wrap>
          <Input
            placeholder="Search LC Number, Applicant..."
            prefix={<SearchOutlined />}
            style={{ width: 300 }}
            onChange={(e) => setFilters({ ...filters, search: e.target.value })}
          />
          <Select
            placeholder="Status"
            style={{ width: 150 }}
            allowClear
            onChange={(value) => setFilters({ ...filters, status: value })}
          >
            <Select.Option value="DRAFT">Draft</Select.Option>
            <Select.Option value="SUBMITTED">Submitted</Select.Option>
            <Select.Option value="APPROVED">Approved</Select.Option>
            <Select.Option value="ISSUED">Issued</Select.Option>
            <Select.Option value="ACTIVE">Active</Select.Option>
            <Select.Option value="DOCUMENTS_RECEIVED">Documents Received</Select.Option>
            <Select.Option value="COMPLETED">Completed</Select.Option>
          </Select>
          <Select
            placeholder="Type"
            style={{ width: 150 }}
            allowClear
            onChange={(value) => setFilters({ ...filters, type: value })}
          >
            <Select.Option value="SIGHT">Sight</Select.Option>
            <Select.Option value="USANCE">Usance</Select.Option>
            <Select.Option value="STANDBY">Standby</Select.Option>
          </Select>
          <RangePicker onChange={(dates) => setFilters({ ...filters, dateRange: dates })} />
          <Button icon={<FilterOutlined />}>More Filters</Button>
        </Space>

        {/* Table */}
        <Table
          columns={columns}
          dataSource={data?.content}
          loading={isLoading}
          rowKey="id"
          pagination={{
            total: data?.totalElements,
            pageSize: 20,
            showSizeChanger: true,
            showTotal: (total) => `Total ${total} items`,
          }}
        />
      </Card>
    </>
  );
};

export default LCListPage;
```

#### 7.2.2 LC Create Form
```typescript
// src/features/lc/pages/CreateLCPage.tsx
import React from 'react';
import { Form, Input, InputNumber, Select, DatePicker, Button, Card, Steps, Row, Col, message } from 'antd';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { lcService } from '../services/lcService';
import { CreateLCSchema } from '../validators/lc.validators';
import PageHeader from '@/components/common/PageHeader';
import CustomerSearch from '@/features/customer/components/CustomerSearch';

const CreateLCPage: React.FC = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(0);
  
  const { control, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(CreateLCSchema),
  });

  const createMutation = useMutation({
    mutationFn: lcService.create,
    onSuccess: () => {
      message.success('LC application created successfully');
      navigate('/lc/list');
    },
    onError: () => {
      message.error('Failed to create LC application');
    },
  });

  const onSubmit = (data: any) => {
    createMutation.mutate(data);
  };

  const steps = [
    { title: 'Parties', description: 'Applicant & Beneficiary' },
    { title: 'Terms', description: 'LC Details & Terms' },
    { title: 'Documents', description: 'Required Documents' },
    { title: 'Review', description: 'Review & Submit' },
  ];

  return (
    <>
      <PageHeader title="New LC Application" onBack={() => navigate('/lc/list')} />

      <Card>
        <Steps current={currentStep} items={steps} style={{ marginBottom: 32 }} />

        <Form layout="vertical" onFinish={handleSubmit(onSubmit)}>
          {/* Step 1: Parties */}
          {currentStep === 0 && (
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="Applicant" required>
                  <Controller
                    name="applicantId"
                    control={control}
                    render={({ field }) => (
                      <CustomerSearch
                        {...field}
                        placeholder="Search applicant..."
                        error={errors.applicantId?.message}
                      />
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Beneficiary" required>
                  <Controller
                    name="beneficiaryId"
                    control={control}
                    render={({ field }) => (
                      <CustomerSearch
                        {...field}
                        placeholder="Search beneficiary..."
                        error={errors.beneficiaryId?.message}
                      />
                    )}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}

          {/* Step 2: Terms */}
          {currentStep === 1 && (
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="LC Type" required>
                  <Controller
                    name="type"
                    control={control}
                    render={({ field }) => (
                      <Select {...field} placeholder="Select LC type">
                        <Select.Option value="SIGHT">Sight</Select.Option>
                        <Select.Option value="USANCE">Usance</Select.Option>
                        <Select.Option value="STANDBY">Standby</Select.Option>
                      </Select>
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Currency" required>
                  <Controller
                    name="currency"
                    control={control}
                    render={({ field }) => (
                      <Select {...field} placeholder="Select currency">
                        <Select.Option value="USD">USD</Select.Option>
                        <Select.Option value="EUR">EUR</Select.Option>
                        <Select.Option value="GBP">GBP</Select.Option>
                        <Select.Option value="AED">AED</Select.Option>
                      </Select>
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Amount" required>
                  <Controller
                    name="amount"
                    control={control}
                    render={({ field }) => (
                      <InputNumber
                        {...field}
                        style={{ width: '100%' }}
                        min={0}
                        precision={2}
                        formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                      />
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Expiry Date" required>
                  <Controller
                    name="expiryDate"
                    control={control}
                    render={({ field }) => (
                      <DatePicker {...field} style={{ width: '100%' }} />
                    )}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}

          {/* Navigation Buttons */}
          <div style={{ marginTop: 24, textAlign: 'right' }}>
            {currentStep > 0 && (
              <Button style={{ marginRight: 8 }} onClick={() => setCurrentStep(currentStep - 1)}>
                Previous
              </Button>
            )}
            {currentStep < steps.length - 1 && (
              <Button type="primary" onClick={() => setCurrentStep(currentStep + 1)}>
                Next
              </Button>
            )}
            {currentStep === steps.length - 1 && (
              <Button type="primary" htmlType="submit" loading={createMutation.isPending}>
                Submit Application
              </Button>
            )}
          </div>
        </Form>
      </Card>
    </>
  );
};

export default CreateLCPage;
```

---

**(Continuing in next part due to length...)**

This document will continue with detailed specifications for:

### MODULE 3: BANK GUARANTEE
- BG List Page
- BG Create Form
- Claims Management
- Amendment Requests

### MODULE 4: SUPPLY CHAIN FINANCE (SCF)
- SCF Dashboard
- Invoice Discounting
- Early Payment Options
- Batch Processing

### MODULE 5: DOCUMENTARY COLLECTION
- Collection List
- Collection Form
- Document Tracking

### MODULE 6: CUSTOMER MANAGEMENT
- Customer List
- Customer Profile
- KYC Management
- Credit Limit Management

### MODULE 7: COMPLIANCE & SCREENING
- Compliance Dashboard
- Screening Checks
- Hit Management
- Compliance Reports

### MODULE 8: RISK MANAGEMENT
- Risk Dashboard
- Risk Assessments
- Risk Monitoring
- Risk Reports

### MODULE 9: DOCUMENT MANAGEMENT
- Document Library
- Upload & OCR
- AI Extraction
- Verification Queue

### MODULE 10: ANALYTICS & REPORTING
- Analytics Dashboard
- Custom Reports
- Export Functionality
- Scheduled Reports

### MODULE 11: WORKFLOW MANAGEMENT
- Task List
- Approval Workflows
- SLA Monitoring
- Workflow Designer

### MODULE 12: SHIPMENT TRACKING
- Shipment List
- Real-time Tracking
- Carrier Integration

### MODULE 13: AI COPILOT
- Chat Interface
- Intent Recognition
- Conversation History

### MODULE 14: KNOWLEDGE BASE
- Document Search
- Semantic Search
- AI-Powered Answers

### MODULE 15: SECURITY (SSO & MFA)
- SSO Configuration
- MFA Setup
- Security Settings

### MODULE 16: SETTINGS
- User Profile
- Preferences
- System Configuration

---

## 8. Routing Architecture

### 8.1 Route Configuration
```typescript
// src/routes/index.tsx
import React, { Suspense, lazy } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from '@/components/layout/MainLayout';
import AuthLayout from '@/components/layout/AuthLayout';
import ProtectedRoute from '@/components/common/ProtectedRoute';
import Loading from '@/components/common/Loading';

// Lazy load pages
const Dashboard = lazy(() => import('@/features/dashboard/Dashboard'));
const LoginPage = lazy(() => import('@/features/auth/pages/LoginPage'));
const LCListPage = lazy(() => import('@/features/lc/pages/LCListPage'));
const CreateLCPage = lazy(() => import('@/features/lc/pages/CreateLCPage'));
// ... other imports

const AppRoutes: React.FC = () => {
  return (
    <Suspense fallback={<Loading />}>
      <Routes>
        {/* Auth Routes */}
        <Route element={<AuthLayout />}>
          <Route path="/auth/login" element={<LoginPage />} />
          <Route path="/auth/register" element={<RegisterPage />} />
          <Route path="/auth/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/auth/reset-password" element={<ResetPasswordPage />} />
          <Route path="/auth/mfa-setup" element={<MfaSetupPage />} />
        </Route>

        {/* Protected Routes */}
        <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
          <Route path="/dashboard" element={<Dashboard />} />
          
          {/* LC Routes */}
          <Route path="/lc/list" element={<LCListPage />} />
          <Route path="/lc/create" element={<CreateLCPage />} />
          <Route path="/lc/:id" element={<LCDetailPage />} />
          <Route path="/lc/:id/amend" element={<LCAmendmentPage />} />
          
          {/* ... other module routes */}
        </Route>

        {/* Redirect */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Suspense>
  );
};

export default AppRoutes;
```

---

## 9. API Integration Layer

### 9.1 Axios Configuration
```typescript
// src/lib/api.ts
import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from '@/features/auth/store/authStore';
import { message } from 'antd';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

// Create axios instance
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
        // Retry original request
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

// Generic API methods
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

### 9.2 API Service Example
```typescript
// src/features/lc/services/lcService.ts
import { api } from '@/lib/api';
import type { LetterOfCredit, CreateLCRequest, UpdateLCRequest } from '../types/lc.types';

export const lcService = {
  // Get all LCs with filters
  getAll: (filters?: any) =>
    api.get<{ content: LetterOfCredit[]; totalElements: number }>('/lc', {
      params: filters,
    }),

  // Get LC by ID
  getById: (id: string) =>
    api.get<LetterOfCredit>(`/lc/${id}`),

  // Create new LC
  create: (data: CreateLCRequest) =>
    api.post<LetterOfCredit>('/lc', data),

  // Update LC
  update: (id: string, data: UpdateLCRequest) =>
    api.put<LetterOfCredit>(`/lc/${id}`, data),

  // Submit LC for approval
  submit: (id: string) =>
    api.post<void>(`/lc/${id}/submit`),

  // Approve LC
  approve: (id: string, comments?: string) =>
    api.post<void>(`/lc/${id}/approve`, { comments }),

  // Reject LC
  reject: (id: string, reason: string) =>
    api.post<void>(`/lc/${id}/reject`, { reason }),

  // Issue LC
  issue: (id: string, data: any) =>
    api.post<void>(`/lc/${id}/issue`, data),

  // Request amendment
  requestAmendment: (id: string, data: any) =>
    api.post<void>(`/lc/${id}/amendment`, data),

  // Get expiring LCs
  getExpiring: (days: number) =>
    api.get<LetterOfCredit[]>(`/lc/expiring?days=${days}`),

  // Export LC data
  export: (format: 'pdf' | 'excel') =>
    api.get(`/lc/export`, {
      params: { format },
      responseType: 'blob',
    }),
};
```

---

## 10. State Management

### 10.1 Global App Store
```typescript
// src/store/appStore.ts
import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

interface AppState {
  theme: 'light' | 'dark';
  sidebarCollapsed: boolean;
  language: string;
  notifications: Notification[];
  
  // Actions
  setTheme: (theme: 'light' | 'dark') => void;
  toggleSidebar: () => void;
  setLanguage: (lang: string) => void;
  addNotification: (notification: Notification) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
}

export const useAppStore = create<AppState>()(
  devtools((set) => ({
    theme: 'light',
    sidebarCollapsed: false,
    language: 'en',
    notifications: [],

    setTheme: (theme) => set({ theme }),
    
    toggleSidebar: () =>
      set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed })),
    
    setLanguage: (lang) => set({ language: lang }),
    
    addNotification: (notification) =>
      set((state) => ({
        notifications: [...state.notifications, notification],
      })),
    
    removeNotification: (id) =>
      set((state) => ({
        notifications: state.notifications.filter((n) => n.id !== id),
      })),
    
    clearNotifications: () => set({ notifications: [] }),
  }))
);
```

---

## 11. Authentication & Security

### 11.1 Protected Route Component
```typescript
// src/components/common/ProtectedRoute.tsx
import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/features/auth/store/authStore';
import { Spin } from 'antd';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredPermission?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredPermission,
}) => {
  const { isAuthenticated, user, isLoading } = useAuthStore();
  const location = useLocation();

  if (isLoading) {
    return <Spin fullscreen />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/auth/login" state={{ from: location }} replace />;
  }

  if (requiredPermission && !user?.permissions.includes(requiredPermission)) {
    return <Navigate to="/403" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
```

---

## 12. Performance Optimization

### 12.1 Code Splitting Strategy
- Route-based code splitting with React.lazy()
- Component-level code splitting for heavy components
- Dynamic imports for large libraries

### 12.2 Caching Strategy
- React Query for server state caching
- LocalStorage for user preferences
- Service Worker for offline support

### 12.3 Bundle Optimization
- Tree shaking for unused code
- Lazy loading of routes
- Image optimization with WebP format
- Vite build optimization

---

## 13. Testing Strategy

### 13.1 Unit Tests (Vitest)
```typescript
// src/features/lc/services/__tests__/lcService.test.ts
import { describe, it, expect, vi } from 'vitest';
import { lcService } from '../lcService';

describe('lcService', () => {
  it('should fetch all LCs', async () => {
    const result = await lcService.getAll();
    expect(result).toBeDefined();
  });
});
```

### 13.2 Integration Tests (Testing Library)
### 13.3 E2E Tests (Playwright)

---

## 14. Deployment & Build

### 14.1 Build Configuration
```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          antd: ['antd', '@ant-design/icons'],
          charts: ['echarts', 'echarts-for-react'],
        },
      },
    },
  },
});
```

### 14.2 Environment Variables
```env
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_WS_URL=ws://localhost:8080/ws
VITE_APP_NAME=TradeSphere AI
VITE_APP_VERSION=1.0.0

# .env.production
VITE_API_BASE_URL=https://api.tradesphere.ai/api/v1
VITE_WS_URL=wss://api.tradesphere.ai/ws
```

---

## 15. Summary

This architecture document provides a comprehensive blueprint for the TradeSphere AI frontend application. It covers:

✅ **Complete Technology Stack** - Modern React 18 with TypeScript  
✅ **Design System** - Professional banking theme with Ant Design  
✅ **17 Feature Modules** - Full coverage of all backend modules  
✅ **Dashboard Design** - Rich analytics and insights  
✅ **Routing Architecture** - Protected routes with lazy loading  
✅ **API Integration** - Complete REST API integration layer  
✅ **State Management** - Zustand + React Query  
✅ **Authentication & Security** - JWT, RBAC, MFA support  
✅ **Performance Optimization** - Code splitting, caching  
✅ **Testing Strategy** - Unit, integration, E2E tests  
✅ **Deployment Ready** - Vite build configuration  

The implementation should follow this architecture strictly to ensure consistency, maintainability, and scalability.

---

**Next Steps:**
1. Set up the project with Vite + React + TypeScript
2. Install all dependencies (package.json)
3. Configure Ant Design theme
4. Implement authentication module first
5. Build dashboard and main layout
6. Implement feature modules one by one
7. Integration testing with backend APIs
8. Performance optimization
9. Production deployment

**End of Architecture Document**

