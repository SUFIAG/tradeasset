/**
 * Sidebar Navigation Component
 * Futuristic collapsible sidebar with icons and nested menus
 */

import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { ChevronDown, ChevronRight, Menu, X } from 'lucide-react';
import { NAVIGATION_ITEMS } from '../../config/navigation';
import type { NavItem } from '../../types/dashboard.types';
import './Sidebar.css';

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ collapsed, onToggle }) => {
  const location = useLocation();
  const [expandedItems, setExpandedItems] = useState<string[]>([]);

  const toggleExpand = (key: string) => {
    setExpandedItems(prev =>
      prev.includes(key) ? prev.filter(k => k !== key) : [...prev, key]
    );
  };

  const isActive = (path: string) => location.pathname === path;

  const isParentActive = (item: NavItem) => {
    if (item.children) {
      return item.children.some(child => location.pathname === child.path);
    }
    return false;
  };

  const renderNavItem = (item: NavItem, level: number = 0) => {
    const hasChildren = item.children && item.children.length > 0;
    const isExpanded = expandedItems.includes(item.key);
    const active = isActive(item.path) || isParentActive(item);

    return (
      <div key={item.key} className="nav-item-wrapper">
        {hasChildren ? (
          <>
            <button
              className={`nav-item ${active ? 'active' : ''} ${level > 0 ? 'nested' : ''}`}
              onClick={() => toggleExpand(item.key)}
            >
              <div className="nav-item-content">
                {item.icon && <span className="nav-icon">{item.icon}</span>}
                {!collapsed && (
                  <>
                    <span className="nav-label">{item.label}</span>
                    {item.badge !== undefined && (
                      <span className="nav-badge">{item.badge}</span>
                    )}
                    <span className="nav-expand-icon">
                      {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                    </span>
                  </>
                )}
              </div>
            </button>
            {!collapsed && isExpanded && (
              <div className="nav-children">
                {item.children!.map(child => renderNavItem(child, level + 1))}
              </div>
            )}
          </>
        ) : (
          <Link
            to={item.path}
            className={`nav-item ${active ? 'active' : ''} ${level > 0 ? 'nested' : ''}`}
          >
            <div className="nav-item-content">
              {item.icon && <span className="nav-icon">{item.icon}</span>}
              {!collapsed && (
                <>
                  <span className="nav-label">{item.label}</span>
                  {item.badge !== undefined && (
                    <span className="nav-badge">{item.badge}</span>
                  )}
                </>
              )}
            </div>
          </Link>
        )}
      </div>
    );
  };

  return (
    <aside className={`sidebar ${collapsed ? 'collapsed' : ''}`}>
      <div className="sidebar-header">
        <div className="sidebar-brand">
          {!collapsed && (
            <>
              <div className="brand-logo">TS</div>
              <div className="brand-text">
                <div className="brand-name">TradeSphere</div>
                <div className="brand-tagline">Trade Finance</div>
              </div>
            </>
          )}
          {collapsed && <div className="brand-logo-small">TS</div>}
        </div>
        <button className="sidebar-toggle" onClick={onToggle}>
          {collapsed ? <Menu size={20} /> : <X size={20} />}
        </button>
      </div>

      <nav className="sidebar-nav">
        {NAVIGATION_ITEMS.map(item => renderNavItem(item))}
      </nav>

      <div className="sidebar-footer">
        {!collapsed && (
          <div className="sidebar-version">
            <span className="version-label">Version</span>
            <span className="version-number">1.0.0</span>
          </div>
        )}
      </div>
    </aside>
  );
};

