/**
 * Generic Module List Page
 * Reusable, responsive page that fetches records from a backend endpoint
 * and displays them in a data table with loading / error / empty states.
 */

import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { RefreshCw, AlertTriangle, Inbox } from 'lucide-react';
import './ModulePage.css';

export interface ModuleTab {
  key: string;
  label: string;
  fetcher: () => Promise<any[]>;
}

interface ModuleListPageProps {
  title: string;
  subtitle?: string;
  icon?: React.ReactNode;
  tabs: ModuleTab[];
  /** Preferred column order; unknown records fall back to auto-detection */
  preferredColumns?: string[];
  maxColumns?: number;
}

const humanize = (key: string): string =>
  key
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2')
    .replace(/[_-]+/g, ' ')
    .replace(/\b\w/g, (c) => c.toUpperCase());

const formatCell = (value: any): string => {
  if (value === null || value === undefined || value === '') return '—';
  if (typeof value === 'number') return Number.isInteger(value) ? String(value) : value.toFixed(2);
  if (typeof value === 'boolean') return value ? 'Yes' : 'No';
  if (typeof value === 'string') {
    // ISO date detection
    if (/^\d{4}-\d{2}-\d{2}T/.test(value)) {
      const d = new Date(value);
      if (!isNaN(d.getTime())) return d.toLocaleString();
    }
    return value;
  }
  if (typeof value === 'object') {
    if (value.amount !== undefined && value.currency !== undefined) return `${value.amount} ${value.currency}`;
    return JSON.stringify(value);
  }
  return String(value);
};

const isStatusColumn = (key: string) => /status|state|priority|risk/i.test(key);

export const ModuleListPage: React.FC<ModuleListPageProps> = ({
  title,
  subtitle,
  icon,
  tabs,
  preferredColumns,
  maxColumns = 7,
}) => {
  const [activeTab, setActiveTab] = useState(tabs[0]?.key ?? '');
  const [rows, setRows] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const currentTab = useMemo(
    () => tabs.find((t) => t.key === activeTab) ?? tabs[0],
    [tabs, activeTab]
  );

  const load = useCallback(async () => {
    if (!currentTab) return;
    setLoading(true);
    setError(null);
    try {
      console.log(`[${title}] Fetching data for tab: ${currentTab.key}`);
      const data = await currentTab.fetcher();
      console.log(`[${title}] Fetched data:`, data);
      setRows(Array.isArray(data) ? data : []);
    } catch (e: any) {
      console.error(`[${title}] Error fetching data:`, e);
      setError(e?.message || 'Failed to load data from the server.');
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [currentTab, title]);

  useEffect(() => {
    load();
  }, [load]);

  const columns = useMemo(() => {
    if (rows.length === 0) return [];
    const keys = Object.keys(rows[0]).filter((k) => {
      const v = rows[0][k];
      return typeof v !== 'object' || v === null || (v && v.amount !== undefined);
    });
    let ordered = keys;
    if (preferredColumns?.length) {
      const preferred = preferredColumns.filter((c) => keys.includes(c));
      const rest = keys.filter((k) => !preferred.includes(k));
      ordered = [...preferred, ...rest];
    }
    return ordered.slice(0, maxColumns);
  }, [rows, preferredColumns, maxColumns]);

  return (
    <div className="module-page">
      <div className="module-page-header">
        <div className="module-page-title">
          {icon && <span className="module-page-icon">{icon}</span>}
          <div>
            <h1>{title}</h1>
            {subtitle && <p>{subtitle}</p>}
          </div>
        </div>
        <button className="module-refresh-btn" onClick={load} disabled={loading}>
          <RefreshCw size={16} className={loading ? 'spinning' : ''} />
          Refresh
        </button>
      </div>

      {tabs.length > 1 && (
        <div className="module-tabs">
          {tabs.map((tab) => (
            <button
              key={tab.key}
              className={`module-tab ${tab.key === currentTab?.key ? 'active' : ''}`}
              onClick={() => setActiveTab(tab.key)}
            >
              {tab.label}
            </button>
          ))}
        </div>
      )}

      <div className="module-content">
        {loading && (
          <div className="module-state">
            <div className="module-spinner" />
            <p>Loading {title.toLowerCase()}…</p>
          </div>
        )}

        {!loading && error && (
          <div className="module-state error">
            <AlertTriangle size={32} />
            <p>{error}</p>
            <button className="module-refresh-btn" onClick={load}>
              <RefreshCw size={16} /> Try Again
            </button>
          </div>
        )}

        {!loading && !error && rows.length === 0 && (
          <div className="module-state">
            <Inbox size={32} />
            <p>No records found.</p>
          </div>
        )}

        {!loading && !error && rows.length > 0 && (
          <div className="module-table-wrapper">
            <table className="module-table">
              <thead>
                <tr>
                  {columns.map((col) => (
                    <th key={col}>{humanize(col)}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {rows.map((row, i) => (
                  <tr key={row.id ?? i}>
                    {columns.map((col) => (
                      <td key={col}>
                        {isStatusColumn(col) && row[col] ? (
                          <span className={`status-badge status-${String(row[col]).toLowerCase()}`}>
                            {formatCell(row[col])}
                          </span>
                        ) : (
                          formatCell(row[col])
                        )}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
