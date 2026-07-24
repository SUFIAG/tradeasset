/**
 * Main Dashboard Layout
 * Wrapper component with sidebar and top bar
 */

import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { TopBar } from './TopBar';
import './DashboardLayout.css';

export const DashboardLayout: React.FC = () => {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  const toggleSidebar = () => {
    setSidebarCollapsed(!sidebarCollapsed);
  };

  return (
    <div className="dashboard-layout">
      <Sidebar collapsed={sidebarCollapsed} onToggle={toggleSidebar} />
      <TopBar sidebarCollapsed={sidebarCollapsed} />

      <main
        className="dashboard-main"
        style={{
          marginLeft: sidebarCollapsed ? '80px' : '280px',
          marginTop: '64px'
        }}
      >
        <div className="dashboard-content">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

