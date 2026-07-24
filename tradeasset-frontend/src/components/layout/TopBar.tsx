/**
 * Top App Bar Component
 * Global search, notifications, and user menu
 */

import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Search,
  Bell,
  User,
  Settings,
  LogOut,
  Shield,
  HelpCircle,
  ChevronDown
} from 'lucide-react';
import { useAuthStore } from '../../store/auth.store';
import { ROUTES } from '../../config/constants';
import { DEFAULT_NOTIFICATIONS } from '../../pages/modules';
import type { NotificationItem } from '../../pages/modules';
import './TopBar.css';

interface TopBarProps {
  sidebarCollapsed: boolean;
}

export const TopBar: React.FC<TopBarProps> = ({ sidebarCollapsed }) => {
  const { user, logout } = useAuthStore();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [notifications, setNotifications] = useState<NotificationItem[]>(DEFAULT_NOTIFICATIONS);

  const markAllRead = () =>
    setNotifications((prev) => prev.map((n) => ({ ...n, unread: false })));

  const handleLogout = async () => {
    await logout();
  };

  const unreadCount = notifications.filter(n => n.unread).length;

  return (
    <header className="topbar" style={{ marginLeft: sidebarCollapsed ? '80px' : '280px' }}>
      <div className="topbar-content">
        {/* Search Bar */}
        <div className="topbar-search">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Search transactions, customers, documents..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
          <kbd className="search-shortcut">Ctrl K</kbd>
        </div>

        {/* Right Section */}
        <div className="topbar-actions">
          {/* Help */}
          <Link to={ROUTES.HELP} className="topbar-action-btn" title="Help">
            <HelpCircle size={20} />
          </Link>

          {/* Notifications */}
          <div className="topbar-notifications">
            <button
              className="topbar-action-btn"
              onClick={() => setShowNotifications(!showNotifications)}
            >
              <Bell size={20} />
              {unreadCount > 0 && (
                <span className="notification-badge">{unreadCount}</span>
              )}
            </button>

            {showNotifications && (
              <>
                <div
                  className="dropdown-overlay"
                  onClick={() => setShowNotifications(false)}
                />
                <div className="notifications-dropdown">
                  <div className="dropdown-header">
                    <h3>Notifications</h3>
                    <button className="mark-read-btn" onClick={markAllRead}>Mark all as read</button>
                  </div>
                  <div className="notifications-list">
                    {notifications.map(notif => (
                      <div
                        key={notif.id}
                        className={`notification-item ${notif.unread ? 'unread' : ''}`}
                      >
                        <div className={`notif-indicator ${notif.type}`} />
                        <div className="notif-content">
                          <div className="notif-title">{notif.title}</div>
                          <div className="notif-message">{notif.message}</div>
                          <div className="notif-time">{notif.time}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                  <Link
                    to={ROUTES.NOTIFICATIONS}
                    className="view-all-btn"
                    onClick={() => setShowNotifications(false)}
                  >
                    View All Notifications
                  </Link>
                </div>
              </>
            )}
          </div>

          {/* User Menu */}
          <div className="topbar-user-menu">
            <button
              className="user-menu-trigger"
              onClick={() => setShowUserMenu(!showUserMenu)}
            >
              <div className="user-avatar">
                {user?.username?.charAt(0).toUpperCase() || 'U'}
              </div>
              <div className="user-info">
                <div className="user-name">{user?.username || 'User'}</div>
                <div className="user-role">Administrator</div>
              </div>
              <ChevronDown size={16} />
            </button>

            {showUserMenu && (
              <>
                <div
                  className="dropdown-overlay"
                  onClick={() => setShowUserMenu(false)}
                />
                <div className="user-dropdown">
                  <div className="dropdown-user-info">
                    <div className="dropdown-avatar">
                      {user?.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div>
                      <div className="dropdown-username">{user?.username}</div>
                      <div className="dropdown-email">{user?.email}</div>
                    </div>
                  </div>

                  <div className="dropdown-divider" />

                  <Link
                    to={ROUTES.PROFILE}
                    className="dropdown-item"
                    onClick={() => setShowUserMenu(false)}
                  >
                    <User size={18} />
                    <span>My Profile</span>
                  </Link>

                  <Link
                    to={ROUTES.SETTINGS}
                    className="dropdown-item"
                    onClick={() => setShowUserMenu(false)}
                  >
                    <Settings size={18} />
                    <span>Settings</span>
                  </Link>

                  <Link
                    to={ROUTES.SECURITY}
                    className="dropdown-item"
                    onClick={() => setShowUserMenu(false)}
                  >
                    <Shield size={18} />
                    <span>Security</span>
                  </Link>

                  <div className="dropdown-divider" />

                  <button
                    className="dropdown-item logout"
                    onClick={handleLogout}
                  >
                    <LogOut size={18} />
                    <span>Logout</span>
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

