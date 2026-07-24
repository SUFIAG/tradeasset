/**
 * Dashboard Layout Constants
 * Navigation structure and configuration
 */

import {
  LayoutDashboard,
  FileText,
  Shield,
  Wallet,
  Package,
  Users,
  ClipboardCheck,
  AlertTriangle,
  FolderOpen,
  BarChart3,
  GitBranch,
  Ship,
  Bot,
  BookOpen,
  Settings,
} from 'lucide-react';
import { ROUTES } from './constants';
import type { NavItem } from '../types/dashboard.types';

export const NAVIGATION_ITEMS: NavItem[] = [
  {
    key: 'dashboard',
    label: 'Dashboard',
    icon: <LayoutDashboard size={20} />,
    path: ROUTES.DASHBOARD,
  },
  {
    key: 'lc',
    label: 'Letters of Credit',
    icon: <FileText size={20} />,
    path: ROUTES.LC_LIST,
    children: [
      { key: 'lc-list', label: 'All LCs', icon: null, path: ROUTES.LC_LIST },
      { key: 'lc-create', label: 'Create New', icon: null, path: ROUTES.LC_CREATE },
      { key: 'lc-drafts', label: 'Drafts', icon: null, path: '/lc/drafts' },
    ],
  },
  {
    key: 'bg',
    label: 'Bank Guarantees',
    icon: <Shield size={20} />,
    path: ROUTES.BG_LIST,
    children: [
      { key: 'bg-list', label: 'All Guarantees', icon: null, path: ROUTES.BG_LIST },
      { key: 'bg-create', label: 'Create New', icon: null, path: ROUTES.BG_CREATE },
      { key: 'bg-expiring', label: 'Expiring Soon', icon: null, path: '/bank-guarantees/expiring' },
    ],
  },
  {
    key: 'scf',
    label: 'Supply Chain Finance',
    icon: <Wallet size={20} />,
    path: ROUTES.SCF_LIST,
    children: [
      { key: 'scf-list', label: 'All Programs', icon: null, path: ROUTES.SCF_LIST },
      { key: 'scf-create', label: 'Create New', icon: null, path: ROUTES.SCF_CREATE },
      { key: 'scf-pending', label: 'Pending Approval', icon: null, path: '/scf/pending' },
    ],
  },
  {
    key: 'collections',
    label: 'Collections',
    icon: <Package size={20} />,
    path: ROUTES.COLLECTIONS_LIST,
    children: [
      { key: 'collections-list', label: 'All Collections', icon: null, path: ROUTES.COLLECTIONS_LIST },
      { key: 'collections-create', label: 'Create New', icon: null, path: ROUTES.COLLECTION_CREATE },
    ],
  },
  {
    key: 'customers',
    label: 'Customers',
    icon: <Users size={20} />,
    path: ROUTES.CUSTOMERS,
    children: [
      { key: 'customers-list', label: 'All Customers', icon: null, path: ROUTES.CUSTOMERS },
      { key: 'customers-create', label: 'Add Customer', icon: null, path: ROUTES.CUSTOMER_CREATE },
      { key: 'customers-pending', label: 'Pending KYC', icon: null, path: '/customers/pending-kyc' },
    ],
  },
  {
    key: 'compliance',
    label: 'Compliance',
    icon: <ClipboardCheck size={20} />,
    path: ROUTES.COMPLIANCE_DASHBOARD,
    children: [
      { key: 'compliance-dashboard', label: 'Overview', icon: null, path: ROUTES.COMPLIANCE_DASHBOARD },
      { key: 'compliance-checks', label: 'Screening', icon: null, path: ROUTES.COMPLIANCE_CHECKS },
      { key: 'compliance-alerts', label: 'Alerts', icon: null, path: '/compliance/alerts' },
    ],
  },
  {
    key: 'risk',
    label: 'Risk Management',
    icon: <AlertTriangle size={20} />,
    path: ROUTES.RISK_DASHBOARD,
    children: [
      { key: 'risk-dashboard', label: 'Overview', icon: null, path: ROUTES.RISK_DASHBOARD },
      { key: 'risk-assessment', label: 'Assessments', icon: null, path: ROUTES.RISK_ASSESSMENT },
      { key: 'risk-high', label: 'High Risk', icon: null, path: '/risk/high-risk' },
    ],
  },
  {
    key: 'documents',
    label: 'Documents',
    icon: <FolderOpen size={20} />,
    path: ROUTES.DOCUMENTS,
  },
  {
    key: 'analytics',
    label: 'Analytics',
    icon: <BarChart3 size={20} />,
    path: ROUTES.ANALYTICS_DASHBOARD,
    children: [
      { key: 'analytics-dashboard', label: 'Overview', icon: null, path: ROUTES.ANALYTICS_DASHBOARD },
      { key: 'analytics-reports', label: 'Reports', icon: null, path: ROUTES.ANALYTICS_REPORTS },
      { key: 'analytics-export', label: 'Export Data', icon: null, path: '/analytics/export' },
    ],
  },
  {
    key: 'workflow',
    label: 'Workflows',
    icon: <GitBranch size={20} />,
    path: ROUTES.WORKFLOW_LIST,
  },
  {
    key: 'shipments',
    label: 'Shipments',
    icon: <Ship size={20} />,
    path: ROUTES.SHIPMENTS_LIST,
  },
  {
    key: 'copilot',
    label: 'AI Copilot',
    icon: <Bot size={20} />,
    path: '/copilot',
  },
  {
    key: 'knowledge',
    label: 'Knowledge Base',
    icon: <BookOpen size={20} />,
    path: ROUTES.KNOWLEDGE_BASE,
  },
  {
    key: 'settings',
    label: 'Settings',
    icon: <Settings size={20} />,
    path: ROUTES.SETTINGS,
    children: [
      { key: 'settings-profile', label: 'Profile', icon: null, path: ROUTES.PROFILE },
      { key: 'settings-security', label: 'Security', icon: null, path: ROUTES.SECURITY },
      { key: 'settings-mfa', label: 'Two-Factor Auth', icon: null, path: ROUTES.MFA_SETUP },
    ],
  },
];

export const DASHBOARD_CONFIG = {
  SIDEBAR_WIDTH: 280,
  SIDEBAR_COLLAPSED_WIDTH: 80,
  HEADER_HEIGHT: 64,
  CONTENT_PADDING: 24,
};

