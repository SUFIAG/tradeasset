/**
 * Extra Module Pages
 * Create forms, filtered lists and account/utility pages that back every
 * sidebar sub-item and top-bar action.
 */

import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  FileText,
  Shield,
  TrendingUp,
  Package,
  Users,
  ShieldCheck,
  AlertTriangle,
  BarChart3,
  Bot,
  User as UserIcon,
  Lock,
  Bell,
  HelpCircle,
  Search,
  CheckCircle2,
} from 'lucide-react';
import { ModuleListPage } from './ModuleListPage';
import { ModuleCreatePage } from './ModuleCreatePage';
import {
  lcService,
  bgService,
  scfService,
  collectionsService,
  customersService,
  complianceService,
  riskService,
  analyticsService,
  knowledgeService,
} from '../../services/modules.service';
import { useAuthStore } from '../../store/auth.store';
import { ROUTES } from '../../config/constants';
import './ModulePage.css';

const CURRENCIES = ['USD', 'EUR', 'GBP', 'AED', 'SAR', 'JPY', 'CNY'].map((c) => ({
  value: c,
  label: c,
}));

// ===========================================================================
// LETTERS OF CREDIT
// ===========================================================================
export const LCCreatePage: React.FC = () => {

  // Calculate default expiry date (90 days from today)
  const getDefaultExpiryDate = () => {
    const date = new Date();
    date.setDate(date.getDate() + 90);
    return date.toISOString().split('T')[0];
  };

  // Calculate default last shipment date (30 days before expiry)
  const getDefaultShipmentDate = () => {
    const date = new Date();
    date.setDate(date.getDate() + 60); // 90 - 30 = 60 days from today
    return date.toISOString().split('T')[0];
  };

  return (
    <ModuleCreatePage
      title="Create Letter of Credit"
      subtitle="Draft a new letter of credit"
      icon={<FileText size={24} />}
      backTo={ROUTES.LC_LIST}
      onSubmit={(p) => lcService.create(p)}
      fields={[
        {
          name: 'lcType',
          label: 'LC Type',
          type: 'select',
          required: true,
          options: [
            { value: 'SIGHT', label: 'Sight LC (Payment on Presentation)' },
            { value: 'USANCE', label: 'Usance LC (Deferred Payment)' },
            { value: 'DEFERRED_PAYMENT', label: 'Deferred Payment LC' },
            { value: 'ACCEPTANCE', label: 'Acceptance LC' },
            { value: 'NEGOTIATION', label: 'Negotiation LC' },
            { value: 'REVOLVING', label: 'Revolving LC' },
            { value: 'STANDBY', label: 'Standby LC' },
          ],
          placeholder: 'Select LC type'
        },
        {
          name: 'applicantId',
          label: 'Applicant (Buyer) - Customer ID',
          required: true,
          placeholder: 'Enter or paste customer UUID (e.g., from Customers page)',
          helpText: 'Tip: Open Customers page in another tab to copy the UUID'
        },
        {
          name: 'applicantName',
          label: 'Applicant Name',
          required: true,
          placeholder: 'e.g., Acme Corporation Ltd.'
        },
        {
          name: 'beneficiaryId',
          label: 'Beneficiary (Seller) - Customer ID',
          required: true,
          placeholder: 'Enter or paste customer UUID (e.g., from Customers page)',
          helpText: 'The seller/exporter who will receive payment'
        },
        {
          name: 'beneficiaryName',
          label: 'Beneficiary Name',
          required: true,
          placeholder: 'e.g., Global Exports Inc.'
        },
        {
          name: 'amount',
          label: 'LC Amount',
          type: 'number',
          required: true,
          placeholder: 'e.g., 100000.00',
          helpText: 'Maximum amount the bank will pay'
        },
        {
          name: 'currency',
          label: 'Currency',
          type: 'select',
          options: CURRENCIES,
          required: true,
          defaultValue: 'USD'
        },
        {
          name: 'expiryDate',
          label: 'Expiry Date',
          type: 'date',
          required: true,
          defaultValue: getDefaultExpiryDate(),
          helpText: 'Last date for document presentation (default: 90 days from today)'
        },
        {
          name: 'lastShipmentDate',
          label: 'Last Shipment Date',
          type: 'date',
          defaultValue: getDefaultShipmentDate(),
          helpText: 'Latest date goods can be shipped (default: 30 days before expiry)'
        },
        {
          name: 'goodsDescription',
          label: 'Description of Goods',
          type: 'textarea',
          placeholder: 'e.g., 1000 cartons of electronic components as per commercial invoice...',
          helpText: 'Detailed description of goods being shipped'
        },
        {
          name: 'portOfLoading',
          label: 'Port of Loading',
          placeholder: 'e.g., Shanghai, China'
        },
        {
          name: 'portOfDischarge',
          label: 'Port of Discharge',
          placeholder: 'e.g., Los Angeles, USA'
        },
      ]}
    />
  );
};

export const LCDraftsPage: React.FC = () => (
  <ModuleListPage
    title="Letters of Credit — Drafts"
    subtitle="Letters of credit still in draft status"
    icon={<FileText size={24} />}
    tabs={[
      {
        key: 'drafts',
        label: 'Drafts',
        fetcher: async () =>
          (await lcService.list()).filter(
            (r: any) => String(r.status).toUpperCase() === 'DRAFT'
          ),
      },
    ]}
    preferredColumns={['lcNumber', 'applicantName', 'beneficiaryName', 'amount', 'currency', 'status', 'expiryDate']}
  />
);

// ===========================================================================
// BANK GUARANTEES
// ===========================================================================
export const BGCreatePage: React.FC = () => (
  <ModuleCreatePage
    title="Create Bank Guarantee"
    subtitle="Issue a new bank guarantee"
    icon={<Shield size={24} />}
    backTo={ROUTES.BG_LIST}
    onSubmit={(p) => bgService.create(p)}
    fields={[
      { name: 'applicantName', label: 'Applicant Name', required: true },
      { name: 'beneficiaryName', label: 'Beneficiary Name', required: true },
      { name: 'guaranteeType', label: 'Guarantee Type', type: 'select', required: true, options: [
        { value: 'BID_BOND', label: 'Bid Bond' },
        { value: 'PERFORMANCE', label: 'Performance' },
        { value: 'ADVANCE_PAYMENT', label: 'Advance Payment' },
        { value: 'FINANCIAL', label: 'Financial' },
      ] },
      { name: 'amount', label: 'Amount', type: 'number', required: true },
      { name: 'currency', label: 'Currency', type: 'select', options: CURRENCIES, required: true },
      { name: 'expiryDate', label: 'Expiry Date', type: 'date' },
    ]}
  />
);

export const BGExpiringPage: React.FC = () => (
  <ModuleListPage
    title="Bank Guarantees — Expiring Soon"
    subtitle="Guarantees approaching their expiry date"
    icon={<Shield size={24} />}
    tabs={[{ key: 'expiring', label: 'Expiring Soon', fetcher: () => bgService.expiring() }]}
    preferredColumns={['guaranteeNumber', 'applicantName', 'beneficiaryName', 'amount', 'currency', 'status', 'expiryDate']}
  />
);

// ===========================================================================
// SUPPLY CHAIN FINANCE
// ===========================================================================
export const ScfCreatePage: React.FC = () => (
  <ModuleCreatePage
    title="Create SCF Facility"
    subtitle="Set up a new supply chain finance facility"
    icon={<TrendingUp size={24} />}
    backTo={ROUTES.SCF_LIST}
    onSubmit={(p) => scfService.create(p)}
    fields={[
      { name: 'supplierName', label: 'Supplier Name', required: true },
      { name: 'buyerName', label: 'Buyer Name', required: true },
      { name: 'amount', label: 'Amount', type: 'number', required: true },
      { name: 'currency', label: 'Currency', type: 'select', options: CURRENCIES, required: true },
      { name: 'dueDate', label: 'Due Date', type: 'date' },
    ]}
  />
);

export const ScfPendingPage: React.FC = () => (
  <ModuleListPage
    title="SCF — Pending Approval"
    subtitle="Facilities awaiting approval"
    icon={<TrendingUp size={24} />}
    tabs={[
      {
        key: 'pending',
        label: 'Pending Approval',
        fetcher: async () =>
          (await scfService.list()).filter((r: any) =>
            /PENDING|SUBMITTED|REVIEW/i.test(String(r.status))
          ),
      },
    ]}
    preferredColumns={['reference', 'type', 'supplierName', 'buyerName', 'amount', 'status', 'dueDate']}
  />
);

// ===========================================================================
// COLLECTIONS
// ===========================================================================
export const CollectionCreatePage: React.FC = () => (
  <ModuleCreatePage
    title="Create Documentary Collection"
    subtitle="Register a new documentary collection"
    icon={<Package size={24} />}
    backTo={ROUTES.COLLECTIONS_LIST}
    onSubmit={(p) => collectionsService.create(p)}
    fields={[
      { name: 'drawerName', label: 'Drawer Name', required: true },
      { name: 'draweeName', label: 'Drawee Name', required: true },
      { name: 'amount', label: 'Amount', type: 'number', required: true },
      { name: 'currency', label: 'Currency', type: 'select', options: CURRENCIES, required: true },
      { name: 'collectionType', label: 'Collection Type', type: 'select', options: [
        { value: 'DOCUMENTS_AGAINST_PAYMENT', label: 'Documents Against Payment' },
        { value: 'DOCUMENTS_AGAINST_ACCEPTANCE', label: 'Documents Against Acceptance' },
      ] },
    ]}
  />
);

// ===========================================================================
// CUSTOMERS
// ===========================================================================
export const CustomerCreatePage: React.FC = () => (
  <ModuleCreatePage
    title="Add Customer"
    subtitle="Onboard a new customer"
    icon={<Users size={24} />}
    backTo={ROUTES.CUSTOMERS}
    onSubmit={(p) => customersService.create(p)}
    fields={[
      { name: 'name', label: 'Customer Name', required: true },
      { name: 'email', label: 'Email', type: 'text', required: true },
      { name: 'phone', label: 'Phone' },
      { name: 'country', label: 'Country' },
      { name: 'customerType', label: 'Customer Type', type: 'select', options: [
        { value: 'CORPORATE', label: 'Corporate' },
        { value: 'SME', label: 'SME' },
        { value: 'INDIVIDUAL', label: 'Individual' },
      ] },
    ]}
  />
);

export const CustomerPendingKycPage: React.FC = () => (
  <ModuleListPage
    title="Customers — Pending KYC"
    subtitle="Customers with pending KYC verification"
    icon={<Users size={24} />}
    tabs={[
      {
        key: 'pending',
        label: 'Pending KYC',
        fetcher: async () =>
          (await customersService.list()).filter((r: any) =>
            /PENDING|IN_PROGRESS|REVIEW/i.test(String(r.kycStatus))
          ),
      },
    ]}
    preferredColumns={['customerNumber', 'name', 'email', 'country', 'kycStatus', 'riskRating', 'status']}
  />
);

// ===========================================================================
// COMPLIANCE
// ===========================================================================
export const ComplianceChecksPage: React.FC = () => (
  <ModuleListPage
    title="Compliance — Screening"
    subtitle="Screening checks requiring review"
    icon={<ShieldCheck size={24} />}
    tabs={[{ key: 'review', label: 'Review Required', fetcher: () => complianceService.reviewRequired() }]}
    preferredColumns={['checkId', 'entityType', 'entityId', 'checkType', 'score', 'status', 'createdAt']}
  />
);

export const ComplianceAlertsPage: React.FC = () => (
  <ModuleListPage
    title="Compliance — Alerts"
    subtitle="Flagged or failed compliance checks"
    icon={<AlertTriangle size={24} />}
    tabs={[
      {
        key: 'alerts',
        label: 'Alerts',
        fetcher: async () =>
          (await complianceService.reviewRequired()).filter((r: any) =>
            /FLAG|FAIL|REJECT|ALERT/i.test(String(r.status))
          ),
      },
    ]}
    preferredColumns={['checkId', 'entityType', 'entityId', 'checkType', 'score', 'status', 'createdAt']}
  />
);

// ===========================================================================
// RISK
// ===========================================================================
export const RiskHighPage: React.FC = () => (
  <ModuleListPage
    title="Risk — High Risk"
    subtitle="High-risk assessments across the portfolio"
    icon={<AlertTriangle size={24} />}
    tabs={[{ key: 'high', label: 'High Risk', fetcher: () => riskService.highRisk() }]}
    preferredColumns={['assessmentId', 'entityType', 'entityId', 'riskLevel', 'score', 'status', 'assessedAt']}
  />
);

// ===========================================================================
// ANALYTICS — REPORTS / EXPORT
// ===========================================================================
export const AnalyticsReportsPage: React.FC = () => {
  const [data, setData] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const [perf, tx] = await Promise.allSettled([
          analyticsService.performance(),
          analyticsService.transactions(),
        ]);
        const merged: any = {};
        if (perf.status === 'fulfilled') Object.assign(merged, perf.value);
        if (tx.status === 'fulfilled') Object.assign(merged, { transactions: tx.value });
        setData(merged);
        if (perf.status === 'rejected' && tx.status === 'rejected')
          setError('Failed to load reports.');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const entries: [string, any][] =
    data && typeof data === 'object' ? Object.entries(data) : [];

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><BarChart3 size={24} /></span>
          <div>
            <h1>Analytics Reports</h1>
            <p>Operational performance and transaction reports</p>
          </div>
        </div>
      </div>
      <div className="module-content">
        {loading && <div className="module-state"><div className="module-spinner" /><p>Loading reports…</p></div>}
        {!loading && error && <div className="module-state error"><AlertTriangle size={32} /><p>{error}</p></div>}
        {!loading && !error && entries.length === 0 && (
          <div className="module-state"><p>No report data available.</p></div>
        )}
        {!loading && !error && entries.length > 0 && (
          <div className="settings-section">
            {entries.map(([k, v]) => (
              <div className="settings-row" key={k}>
                <span className="label">{k.replace(/([a-z0-9])([A-Z])/g, '$1 $2')}</span>
                <span className="value">{typeof v === 'object' ? JSON.stringify(v) : String(v)}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export const AnalyticsExportPage: React.FC = () => {
  const [status, setStatus] = useState<string | null>(null);

  const exportData = async () => {
    setStatus('Preparing export…');
    try {
      const data = await analyticsService.dashboard();
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `analytics-export-${new Date().toISOString().slice(0, 10)}.json`;
      a.click();
      URL.revokeObjectURL(url);
      setStatus('Export downloaded successfully.');
    } catch (e: any) {
      setStatus(e?.message || 'Export failed.');
    }
  };

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><BarChart3 size={24} /></span>
          <div>
            <h1>Export Data</h1>
            <p>Download analytics data for offline reporting</p>
          </div>
        </div>
      </div>
      <div className="module-content">
        <div className="settings-section">
          <p style={{ marginBottom: '1rem', color: 'var(--color-text-secondary)' }}>
            Export the current analytics dashboard snapshot as a JSON file.
          </p>
          <button className="module-primary-btn" onClick={exportData}>Export Analytics (JSON)</button>
          {status && <p style={{ marginTop: '1rem' }}>{status}</p>}
        </div>
      </div>
    </div>
  );
};

// ===========================================================================
// AI COPILOT (uses the Knowledge RAG endpoint)
// ===========================================================================
export const CopilotPage: React.FC = () => {
  const [messages, setMessages] = useState<{ role: 'user' | 'ai'; text: string }[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  const send = async (e: React.FormEvent) => {
    e.preventDefault();
    const q = input.trim();
    if (!q || loading) return;
    setMessages((m) => [...m, { role: 'user', text: q }]);
    setInput('');
    setLoading(true);
    try {
      const res = await knowledgeService.ask(q);
      const answer =
        typeof res === 'string' ? res : res?.answer ?? res?.response ?? JSON.stringify(res);
      setMessages((m) => [...m, { role: 'ai', text: answer }]);
    } catch (err: any) {
      setMessages((m) => [
        ...m,
        { role: 'ai', text: err?.message || 'Sorry, I could not process that request.' },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><Bot size={24} /></span>
          <div>
            <h1>AI Copilot</h1>
            <p>Your AI assistant for trade-finance operations</p>
          </div>
        </div>
      </div>
      <div className="module-content">
        <div className="copilot-messages">
          {messages.length === 0 && (
            <div className="module-state">
              <Bot size={32} />
              <p>Ask me anything about your trade-finance operations.</p>
            </div>
          )}
          {messages.map((m, i) => (
            <div key={i} className={`copilot-bubble ${m.role}`}>
              {m.text}
            </div>
          ))}
          {loading && <div className="copilot-bubble ai">Thinking…</div>}
        </div>
        <form className="knowledge-form" onSubmit={send}>
          <input
            type="text"
            placeholder="Ask the AI copilot…"
            value={input}
            onChange={(e) => setInput(e.target.value)}
          />
          <button className="module-refresh-btn" type="submit" disabled={loading || !input.trim()}>
            <Search size={16} /> Send
          </button>
        </form>
      </div>
    </div>
  );
};

// ===========================================================================
// PROFILE
// ===========================================================================
export const ProfilePage: React.FC = () => {
  const { user } = useAuthStore();
  const u = user as any;
  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><UserIcon size={24} /></span>
          <div>
            <h1>My Profile</h1>
            <p>Your account information</p>
          </div>
        </div>
      </div>
      <div className="module-content">
        <div className="profile-hero">
          <div className="profile-avatar">{u?.username?.charAt(0).toUpperCase() || 'U'}</div>
          <div>
            <div className="profile-name">
              {[u?.firstName, u?.lastName].filter(Boolean).join(' ') || u?.username || 'User'}
            </div>
            <div className="profile-email">{u?.email || '—'}</div>
          </div>
        </div>
        <div className="settings-section">
          <h3>Account Details</h3>
          <div className="settings-row"><span className="label">Username</span><span className="value">{u?.username ?? '—'}</span></div>
          <div className="settings-row"><span className="label">Email</span><span className="value">{u?.email ?? '—'}</span></div>
          <div className="settings-row"><span className="label">First Name</span><span className="value">{u?.firstName || '—'}</span></div>
          <div className="settings-row"><span className="label">Last Name</span><span className="value">{u?.lastName || '—'}</span></div>
          <div className="settings-row"><span className="label">Status</span><span className="value">{u?.status ?? '—'}</span></div>
          <div className="settings-row"><span className="label">Roles</span><span className="value">{Array.isArray(u?.roles) && u.roles.length ? u.roles.join(', ') : '—'}</span></div>
        </div>
        <div className="settings-section">
          <h3>Quick Actions</h3>
          <div className="settings-row">
            <span className="label">Security & Sessions</span>
            <Link className="module-refresh-btn" to={ROUTES.SECURITY}>Manage Security</Link>
          </div>
          <div className="settings-row">
            <span className="label">Two-Factor Authentication</span>
            <Link className="module-refresh-btn" to={ROUTES.MFA_SETUP}>Configure MFA</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

// ===========================================================================
// SECURITY
// ===========================================================================
export const SecurityPage: React.FC = () => {
  const { user } = useAuthStore();
  const u = user as any;
  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><Lock size={24} /></span>
          <div>
            <h1>Security</h1>
            <p>Manage your security settings</p>
          </div>
        </div>
      </div>
      <div className="module-content">
        <div className="settings-section">
          <h3>Multi-Factor Authentication</h3>
          <div className="settings-row">
            <span className="label">MFA Status</span>
            <span className="value">
              <span className={`status-badge ${u?.mfaEnabled ? 'status-active' : 'status-pending'}`}>
                {u?.mfaEnabled ? 'Enabled' : 'Not Enabled'}
              </span>
            </span>
          </div>
          <div className="settings-row">
            <span className="label">Configure Authenticator</span>
            <Link className="module-refresh-btn" to={ROUTES.MFA_SETUP}>Set up MFA</Link>
          </div>
        </div>
        <div className="settings-section">
          <h3>Account Security</h3>
          <div className="settings-row"><span className="label">Email Verified</span><span className="value">{u?.emailVerified ? 'Yes' : 'No'}</span></div>
          <div className="settings-row">
            <span className="label">Password</span>
            <Link className="module-refresh-btn" to={ROUTES.FORGOT_PASSWORD}>Change Password</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

// ===========================================================================
// NOTIFICATIONS
// ===========================================================================
export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: string;
  time: string;
  unread: boolean;
}

export const DEFAULT_NOTIFICATIONS: NotificationItem[] = [
  { id: '1', title: 'LC Approval Required', message: 'LC-2024-001 pending your approval', type: 'warning', time: '5 min ago', unread: true },
  { id: '2', title: 'BG Issued Successfully', message: 'Bank Guarantee BG-2024-045 has been issued', type: 'success', time: '1 hour ago', unread: true },
  { id: '3', title: 'Document Verification', message: 'Document verification completed', type: 'info', time: '2 hours ago', unread: false },
  { id: '4', title: 'Compliance Screening', message: 'A transaction requires compliance review', type: 'warning', time: 'Yesterday', unread: false },
];

export const NotificationsPage: React.FC = () => {
  const [items, setItems] = useState<NotificationItem[]>(DEFAULT_NOTIFICATIONS);

  const markAllRead = () => setItems((prev) => prev.map((n) => ({ ...n, unread: false })));
  const toggle = (id: string) =>
    setItems((prev) => prev.map((n) => (n.id === id ? { ...n, unread: !n.unread } : n)));

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><Bell size={24} /></span>
          <div>
            <h1>Notifications</h1>
            <p>All your recent alerts and updates</p>
          </div>
        </div>
        <button className="module-refresh-btn" onClick={markAllRead}>
          <CheckCircle2 size={16} /> Mark all as read
        </button>
      </div>
      <div className="module-content">
        <div className="notifications-page-list">
          {items.map((n) => (
            <button
              key={n.id}
              className={`notification-page-item ${n.unread ? 'unread' : ''}`}
              onClick={() => toggle(n.id)}
            >
              <span className={`notif-indicator ${n.type}`} />
              <span className="notif-content">
                <span className="notif-title">{n.title}</span>
                <span className="notif-message">{n.message}</span>
                <span className="notif-time">{n.time}</span>
              </span>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

// ===========================================================================
// HELP
// ===========================================================================
export const HelpPage: React.FC = () => {
  const faqs = [
    { q: 'How do I create a Letter of Credit?', a: 'Open "Letters of Credit" in the sidebar and choose "Create New". Fill in the applicant, beneficiary and amount details and submit.' },
    { q: 'Where can I see expiring Bank Guarantees?', a: 'Go to "Bank Guarantees" → "Expiring Soon" to view guarantees approaching their expiry date.' },
    { q: 'How do I review compliance alerts?', a: 'Open "Compliance" → "Alerts" to see flagged or failed screening checks.' },
    { q: 'How do I enable Two-Factor Authentication?', a: 'Go to Settings → Security → Set up MFA and follow the on-screen steps.' },
    { q: 'Who do I contact for support?', a: 'Reach the TradeSphere support team at support@tradesphere.example.' },
  ];
  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><HelpCircle size={24} /></span>
          <div>
            <h1>Help & Support</h1>
            <p>Frequently asked questions and guidance</p>
          </div>
        </div>
      </div>
      <div className="module-content">
        <div className="settings-section">
          {faqs.map((f, i) => (
            <div key={i} className="help-faq">
              <div className="help-q">{f.q}</div>
              <div className="help-a">{f.a}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
