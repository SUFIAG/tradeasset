/**
 * Module Pages
 * Backend-integrated pages for every trade-finance module.
 * List pages reuse the generic ModuleListPage; Analytics, Workflows,
 * Knowledge and Settings have dedicated layouts.
 */

import React, { useCallback, useEffect, useState } from 'react';
import {
  FileText,
  Shield,
  Clock,
  TrendingUp,
  Users,
  ShieldCheck,
  AlertTriangle,
  FolderOpen,
  BarChart3,
  GitBranch,
  Ship,
  BookOpen,
  Settings as SettingsIcon,
  Search,
  RefreshCw,
} from 'lucide-react';
import { ModuleListPage } from './ModuleListPage';
import {
  lcService,
  bgService,
  collectionsService,
  scfService,
  customersService,
  complianceService,
  riskService,
  documentsService,
  analyticsService,
  workflowService,
  shipmentsService,
  knowledgeService,
} from '../../services/modules.service';
import { useAuthStore } from '../../store/auth.store';
import './ModulePage.css';

// ---------------------------------------------------------------------------
// Letters of Credit
// ---------------------------------------------------------------------------
export const LCListPage: React.FC = () => (
  <ModuleListPage
    title="Letters of Credit"
    subtitle="Issue, amend and track letters of credit"
    icon={<FileText size={24} />}
    tabs={[{ key: 'all', label: 'All LCs', fetcher: () => lcService.list() }]}
    preferredColumns={['lcNumber', 'applicantName', 'beneficiaryName', 'amount', 'currency', 'status', 'expiryDate']}
  />
);

// ---------------------------------------------------------------------------
// Bank Guarantees
// ---------------------------------------------------------------------------
export const BankGuaranteesPage: React.FC = () => (
  <ModuleListPage
    title="Bank Guarantees"
    subtitle="Manage guarantees, claims and amendments"
    icon={<Shield size={24} />}
    tabs={[
      { key: 'all', label: 'All Guarantees', fetcher: () => bgService.list() },
      { key: 'expiring', label: 'Expiring Soon', fetcher: () => bgService.expiring() },
    ]}
    preferredColumns={['guaranteeNumber', 'applicantName', 'beneficiaryName', 'amount', 'currency', 'status', 'expiryDate']}
  />
);

// ---------------------------------------------------------------------------
// Documentary Collections
// ---------------------------------------------------------------------------
export const CollectionsPage: React.FC = () => (
  <ModuleListPage
    title="Documentary Collections"
    subtitle="Track documentary collections end to end"
    icon={<Clock size={24} />}
    tabs={[{ key: 'all', label: 'All Collections', fetcher: () => collectionsService.list() }]}
    preferredColumns={['collectionNumber', 'drawerName', 'draweeName', 'amount', 'currency', 'status']}
  />
);

// ---------------------------------------------------------------------------
// Supply Chain Finance
// ---------------------------------------------------------------------------
export const ScfPage: React.FC = () => (
  <ModuleListPage
    title="Supply Chain Finance"
    subtitle="Financing facilities, disbursements and repayments"
    icon={<TrendingUp size={24} />}
    tabs={[
      { key: 'all', label: 'All Facilities', fetcher: () => scfService.list() },
      { key: 'due', label: 'Due Soon', fetcher: () => scfService.dueSoon() },
      { key: 'overdue', label: 'Overdue', fetcher: () => scfService.overdue() },
    ]}
    preferredColumns={['reference', 'type', 'supplierName', 'buyerName', 'amount', 'status', 'dueDate']}
  />
);

// ---------------------------------------------------------------------------
// Customers
// ---------------------------------------------------------------------------
export const CustomersPage: React.FC = () => (
  <ModuleListPage
    title="Customers"
    subtitle="Customer onboarding, KYC and risk ratings"
    icon={<Users size={24} />}
    tabs={[{ key: 'all', label: 'All Customers', fetcher: () => customersService.list() }]}
    preferredColumns={['customerNumber', 'name', 'email', 'country', 'kycStatus', 'riskRating', 'status']}
  />
);

// ---------------------------------------------------------------------------
// Compliance
// ---------------------------------------------------------------------------
export const CompliancePage: React.FC = () => (
  <ModuleListPage
    title="Compliance"
    subtitle="Screening checks that require review"
    icon={<ShieldCheck size={24} />}
    tabs={[{ key: 'review', label: 'Review Required', fetcher: () => complianceService.reviewRequired() }]}
    preferredColumns={['checkId', 'entityType', 'entityId', 'checkType', 'score', 'status', 'createdAt']}
  />
);

// ---------------------------------------------------------------------------
// Risk
// ---------------------------------------------------------------------------
export const RiskPage: React.FC = () => (
  <ModuleListPage
    title="Risk Management"
    subtitle="High-risk assessments across the portfolio"
    icon={<AlertTriangle size={24} />}
    tabs={[{ key: 'high', label: 'High Risk', fetcher: () => riskService.highRisk() }]}
    preferredColumns={['assessmentId', 'entityType', 'entityId', 'riskLevel', 'score', 'status', 'assessedAt']}
  />
);

// ---------------------------------------------------------------------------
// Documents
// ---------------------------------------------------------------------------
export const DocumentsPage: React.FC = () => (
  <ModuleListPage
    title="Documents"
    subtitle="Uploaded trade documents and AI extraction"
    icon={<FolderOpen size={24} />}
    tabs={[{ key: 'all', label: 'All Documents', fetcher: () => documentsService.list() }]}
    preferredColumns={['fileName', 'documentType', 'entityType', 'entityId', 'status', 'uploadedAt']}
  />
);

// ---------------------------------------------------------------------------
// Shipments
// ---------------------------------------------------------------------------
export const ShipmentsPage: React.FC = () => (
  <ModuleListPage
    title="Shipments"
    subtitle="Real-time shipment tracking"
    icon={<Ship size={24} />}
    tabs={[
      { key: 'active', label: 'Active', fetcher: () => shipmentsService.active() },
      { key: 'delayed', label: 'Delayed', fetcher: () => shipmentsService.delayed() },
    ]}
    preferredColumns={['referenceNumber', 'origin', 'destination', 'carrier', 'status', 'eta']}
  />
);

// ---------------------------------------------------------------------------
// Analytics (metric cards fed by /api/v1/analytics)
// ---------------------------------------------------------------------------
export const AnalyticsPage: React.FC = () => {
  const [metrics, setMetrics] = useState<any | null>(null);
  const [compliance, setCompliance] = useState<any | null>(null);
  const [risk, setRisk] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [dash, comp, rsk] = await Promise.allSettled([
        analyticsService.dashboard(),
        analyticsService.compliance(),
        analyticsService.risk(),
      ]);
      if (dash.status === 'fulfilled') setMetrics(dash.value);
      if (comp.status === 'fulfilled') setCompliance(comp.value);
      if (rsk.status === 'fulfilled') setRisk(rsk.value);
      if (dash.status === 'rejected' && comp.status === 'rejected' && rsk.status === 'rejected') {
        setError((dash.reason as any)?.message || 'Failed to load analytics.');
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const fmt = (v: any, suffix = '') =>
    v === null || v === undefined ? '—' : `${typeof v === 'number' && !Number.isInteger(v) ? v.toFixed(1) : v}${suffix}`;

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><BarChart3 size={24} /></span>
          <div>
            <h1>Analytics</h1>
            <p>Live operational, compliance and risk metrics</p>
          </div>
        </div>
        <button className="module-refresh-btn" onClick={load} disabled={loading}>
          <RefreshCw size={16} className={loading ? 'spinning' : ''} /> Refresh
        </button>
      </div>

      {loading && (
        <div className="module-content">
          <div className="module-state"><div className="module-spinner" /><p>Loading analytics…</p></div>
        </div>
      )}

      {!loading && error && (
        <div className="module-content">
          <div className="module-state error"><AlertTriangle size={32} /><p>{error}</p></div>
        </div>
      )}

      {!loading && !error && (
        <>
          <div className="module-metrics-grid">
            <div className="module-metric-card">
              <div className="module-metric-label">Total Transactions</div>
              <div className="module-metric-value">{fmt(metrics?.totalTransactions)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Active Transactions</div>
              <div className="module-metric-value">{fmt(metrics?.activeTransactions)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Total Volume</div>
              <div className="module-metric-value">{fmt(metrics?.totalVolume)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Success Rate</div>
              <div className="module-metric-value">{fmt(metrics?.successRate, '%')}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Compliance Score</div>
              <div className="module-metric-value">{fmt(metrics?.complianceScore)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Risk Score</div>
              <div className="module-metric-value">{fmt(metrics?.riskScore)}</div>
            </div>
          </div>

          <div className="module-metrics-grid">
            <div className="module-metric-card">
              <div className="module-metric-label">Total Screenings</div>
              <div className="module-metric-value">{fmt(compliance?.totalScreenings)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Flagged Transactions</div>
              <div className="module-metric-value">{fmt(compliance?.flaggedTransactions)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">Risk Assessments</div>
              <div className="module-metric-value">{fmt(risk?.totalAssessments)}</div>
            </div>
            <div className="module-metric-card">
              <div className="module-metric-label">High Risk Items</div>
              <div className="module-metric-value">{fmt(risk?.highRiskCount)}</div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

// ---------------------------------------------------------------------------
// Workflows (workload statistics from AI routing)
// ---------------------------------------------------------------------------
export const WorkflowsPage: React.FC = () => {
  const [stats, setStats] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setStats(await workflowService.workloadStatistics());
    } catch (e: any) {
      setError(e?.message || 'Failed to load workflow statistics.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const entries: [string, any][] = stats && typeof stats === 'object' ? Object.entries(stats) : [];

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><GitBranch size={24} /></span>
          <div>
            <h1>Workflows</h1>
            <p>Approval workflows and AI workload distribution</p>
          </div>
        </div>
        <button className="module-refresh-btn" onClick={load} disabled={loading}>
          <RefreshCw size={16} className={loading ? 'spinning' : ''} /> Refresh
        </button>
      </div>

      <div className="module-content">
        {loading && <div className="module-state"><div className="module-spinner" /><p>Loading workflow statistics…</p></div>}
        {!loading && error && <div className="module-state error"><AlertTriangle size={32} /><p>{error}</p></div>}
        {!loading && !error && entries.length === 0 && (
          <div className="module-state"><p>No workflow statistics available yet.</p></div>
        )}
        {!loading && !error && entries.length > 0 && (
          <div className="settings-section">
            {entries.map(([key, value]) => (
              <div className="settings-row" key={key}>
                <span className="label">{key.replace(/([a-z0-9])([A-Z])/g, '$1 $2').replace(/^\w/, (c) => c.toUpperCase())}</span>
                <span className="value">{typeof value === 'object' ? JSON.stringify(value) : String(value)}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

// ---------------------------------------------------------------------------
// Knowledge Base (AI Q&A)
// ---------------------------------------------------------------------------
export const KnowledgePage: React.FC = () => {
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const ask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!question.trim() || loading) return;
    setLoading(true);
    setError(null);
    setAnswer(null);
    try {
      const res = await knowledgeService.ask(question.trim());
      setAnswer(
        typeof res === 'string'
          ? res
          : res?.answer ?? res?.response ?? JSON.stringify(res, null, 2)
      );
    } catch (err: any) {
      setError(err?.message || 'Failed to get an answer.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><BookOpen size={24} /></span>
          <div>
            <h1>Knowledge Base</h1>
            <p>Ask AI about trade-finance regulations and best practices</p>
          </div>
        </div>
      </div>

      <div className="module-content">
        <form className="knowledge-form" onSubmit={ask}>
          <input
            type="text"
            placeholder="e.g. What are the UCP 600 rules for document examination?"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          />
          <button className="module-refresh-btn" type="submit" disabled={loading || !question.trim()}>
            <Search size={16} /> {loading ? 'Asking…' : 'Ask AI'}
          </button>
        </form>

        {loading && <div className="module-state"><div className="module-spinner" /><p>Consulting the knowledge base…</p></div>}
        {error && <div className="module-state error"><AlertTriangle size={32} /><p>{error}</p></div>}
        {answer && <div className="knowledge-answer">{answer}</div>}
      </div>
    </div>
  );
};

// ---------------------------------------------------------------------------
// Settings (account overview + security shortcuts)
// ---------------------------------------------------------------------------
export const SettingsPage: React.FC = () => {
  const { user } = useAuthStore();

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          <span className="module-page-icon"><SettingsIcon size={24} /></span>
          <div>
            <h1>Settings</h1>
            <p>Account and security preferences</p>
          </div>
        </div>
      </div>

      <div className="module-content">
        <div className="settings-section">
          <h3>Profile</h3>
          <div className="settings-row">
            <span className="label">Username</span>
            <span className="value">{(user as any)?.username ?? '—'}</span>
          </div>
          <div className="settings-row">
            <span className="label">Email</span>
            <span className="value">{(user as any)?.email ?? '—'}</span>
          </div>
          <div className="settings-row">
            <span className="label">Full Name</span>
            <span className="value">
              {[(user as any)?.firstName, (user as any)?.lastName].filter(Boolean).join(' ') || '—'}
            </span>
          </div>
        </div>

        <div className="settings-section">
          <h3>Security</h3>
          <div className="settings-row">
            <span className="label">Multi-Factor Authentication</span>
            <a className="module-refresh-btn" href="/settings/mfa">Configure MFA</a>
          </div>
        </div>
      </div>
    </div>
  );
};
