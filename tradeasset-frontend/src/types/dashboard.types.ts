/**
 * Main Dashboard Layout Type Definitions
 */

export interface NavItem {
  key: string;
  label: string;
  icon: React.ReactNode;
  path: string;
  badge?: number;
  children?: NavItem[];
}

export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  role: string;
}

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: 'info' | 'warning' | 'error' | 'success';
  timestamp: string;
  read: boolean;
  link?: string;
}

export interface DashboardMetric {
  title: string;
  value: string | number;
  change: number;
  trend: 'up' | 'down' | 'neutral';
  icon: React.ReactNode;
  color: string;
}

