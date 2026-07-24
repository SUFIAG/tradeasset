/**
 * AI-Integrated Dashboard Page
 * Advanced dashboard with AI copilot, predictive analytics, and real-time insights
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/auth.store';
import { analyticsService } from '../services/modules.service';
import type { DashboardMetrics } from '../services/modules.service';
import { ROUTES } from '../config/constants';
import { Card, CardHeader, CardBody, Button } from '../components/ui';
import { 
  TrendingUp,
  AlertTriangle,
  Activity,
  Brain,
  MessageSquare,
  Zap,
  Target,
  Shield,
  BarChart3,
  Sparkles,
  Clock,
  DollarSign
} from 'lucide-react';
import './Dashboard.css';

export const DashboardPage: React.FC = () => {
  const { user } = useAuthStore();
  const navigate = useNavigate();
  const [aiInsights, setAiInsights] = useState<any[]>([]);
  const [showCopilot, setShowCopilot] = useState(false);
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);

  // Load live metrics from the backend analytics module
  useEffect(() => {
    let cancelled = false;
    analyticsService
      .dashboard()
      .then((data) => {
        if (!cancelled) setMetrics(data);
      })
      .catch(() => {
        // Backend unavailable - keep static fallback values
      });
    return () => {
      cancelled = true;
    };
  }, []);

  // Simulate real-time updates
  useEffect(() => {
    // Simulate AI insights loading
    const insights = [
      {
        type: 'opportunity',
        title: 'New Trading Opportunity Detected',
        message: 'AI identified favorable conditions for USD/EUR trade',
        confidence: 87,
        priority: 'high'
      },
      {
        type: 'risk',
        title: 'Risk Alert: Market Volatility',
        message: 'Increased volatility in Asian markets detected',
        confidence: 92,
        priority: 'critical'
      },
      {
        type: 'recommendation',
        title: 'Portfolio Optimization',
        message: 'Consider rebalancing tech sector exposure',
        confidence: 78,
        priority: 'medium'
      }
    ];
    setAiInsights(insights);
  }, []);


  return (
    <div className="ai-dashboard-page">
      {/* AI Insights Bar */}
          <div className="ai-insights-bar">
            <div className="insight-card market-pulse">
              <div className="insight-icon">
                <Activity size={24} />
              </div>
              <div className="insight-content">
                <div className="insight-label">Market Pulse</div>
                <div className="insight-value">Active</div>
                <div className="insight-trend positive">+2.3% Today</div>
              </div>
            </div>

            <div className="insight-card portfolio-health">
              <div className="insight-icon">
                <DollarSign size={24} />
              </div>
              <div className="insight-content">
                <div className="insight-label">Portfolio Value</div>
                <div className="insight-value">
                  {metrics?.totalVolume != null ? `$${(metrics.totalVolume / 1_000_000).toFixed(2)}M` : '$2.84M'}
                </div>
                <div className="insight-trend positive">
                  <TrendingUp size={16} /> +$65K
                </div>
              </div>
            </div>

            <div className="insight-card risk-meter">
              <div className="insight-icon">
                <Shield size={24} />
              </div>
              <div className="insight-content">
                <div className="insight-label">AI Risk Score</div>
                <div className="insight-value">
                  {metrics?.riskScore != null ? `${Math.round(metrics.riskScore)}/100` : '42/100'}
                </div>
                <div className="insight-trend neutral">Moderate</div>
              </div>
            </div>

            <div className="insight-card active-alerts">
              <div className="insight-icon">
                <AlertTriangle size={24} />
              </div>
              <div className="insight-content">
                <div className="insight-label">Active Alerts</div>
                <div className="insight-value">
                  {metrics?.activeTransactions != null ? Math.round(metrics.activeTransactions) : 3}
                </div>
                <div className="insight-trend critical">2 Critical</div>
              </div>
            </div>


            <div className="insight-card ai-confidence">
              <div className="insight-icon">
                <Sparkles size={24} />
              </div>
              <div className="insight-content">
                <div className="insight-label">AI Confidence</div>
                <div className="insight-value">87%</div>
                <div className="insight-trend positive">High</div>
              </div>
            </div>
          </div>

          {/* AI Recommendations Panel */}
          <div className="ai-recommendations-panel">
            <div className="panel-header">
              <Brain size={24} />
              <h2>AI-Powered Insights & Recommendations</h2>
              <span className="live-indicator">
                <span className="pulse-dot"></span>
                Live
              </span>
            </div>
            
            <div className="recommendations-grid">
              {aiInsights.map((insight, index) => (
                <div key={index} className={`recommendation-card ${insight.priority}`}>
                  <div className="recommendation-header">
                    <div className="recommendation-type">
                      {insight.type === 'opportunity' && <Target size={18} />}
                      {insight.type === 'risk' && <AlertTriangle size={18} />}
                      {insight.type === 'recommendation' && <Zap size={18} />}
                      <span>{insight.type.toUpperCase()}</span>
                    </div>
                    <div className="confidence-badge">
                      {insight.confidence}% confidence
                    </div>
                  </div>
                  <h3>{insight.title}</h3>
                  <p>{insight.message}</p>
                  <div className="recommendation-actions">
                    <button className="btn-primary-ai">
                      <Sparkles size={16} />
                      Execute AI Action
                    </button>
                    <button className="btn-secondary-ai">View Details</button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Main Dashboard Grid */}
          <div className="ai-dashboard-grid">
            {/* Letters of Credit - AI Enhanced */}
            <Card variant="bordered" padding="md" className="ai-card">
              <CardHeader 
                title={
                  <div className="card-title-ai">
                    <span>Letters of Credit</span>
                    <div className="ai-badge">AI-Powered</div>
                  </div>
                }
              />
              <CardBody>
                <div className="ai-card-stats">
                  <div className="stat-item">
                    <span className="stat-label">Active LCs</span>
                    <span className="stat-value">24</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Predicted Actions</span>
                    <span className="stat-value highlight">3</span>
                  </div>
                </div>
                <div className="ai-insight-mini">
                  <Sparkles size={14} />
                  <span>AI suggests reviewing 2 expiring LCs</span>
                </div>
                <Button variant="primary" style={{ marginTop: 'var(--spacing-4)', width: '100%' }} onClick={() => navigate(ROUTES.LC_LIST)}>
                  <BarChart3 size={16} />
                  View AI Analytics
                </Button>
              </CardBody>
            </Card>

            {/* Bank Guarantees - AI Enhanced */}
            <Card variant="bordered" padding="md" className="ai-card">
              <CardHeader 
                title={
                  <div className="card-title-ai">
                    <span>Bank Guarantees</span>
                    <div className="ai-badge">Real-time</div>
                  </div>
                }
              />
              <CardBody>
                <div className="ai-card-stats">
                  <div className="stat-item">
                    <span className="stat-label">Active Guarantees</span>
                    <span className="stat-value">18</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Risk Alerts</span>
                    <span className="stat-value warning">1</span>
                  </div>
                </div>
                <div className="ai-insight-mini">
                  <AlertTriangle size={14} />
                  <span>1 guarantee requires attention</span>
                </div>
                <Button variant="primary" style={{ marginTop: 'var(--spacing-4)', width: '100%' }} onClick={() => navigate(ROUTES.BG_LIST)}>
                  <Shield size={16} />
                  Monitor Guarantees
                </Button>
              </CardBody>
            </Card>

            {/* Collections - AI Enhanced */}
            <Card variant="bordered" padding="md" className="ai-card">
              <CardHeader 
                title={
                  <div className="card-title-ai">
                    <span>Collections</span>
                    <div className="ai-badge predictive">Predictive</div>
                  </div>
                }
              />
              <CardBody>
                <div className="ai-card-stats">
                  <div className="stat-item">
                    <span className="stat-label">In Progress</span>
                    <span className="stat-value">32</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Completion ETA</span>
                    <span className="stat-value">2.3d</span>
                  </div>
                </div>
                <div className="ai-insight-mini success">
                  <TrendingUp size={14} />
                  <span>12% faster than predicted</span>
                </div>
                <Button variant="primary" style={{ marginTop: 'var(--spacing-4)', width: '100%' }} onClick={() => navigate(ROUTES.COLLECTIONS_LIST)}>
                  <Clock size={16} />
                  Track Collections
                </Button>
              </CardBody>
            </Card>

            {/* Supply Chain Finance - AI Enhanced */}
            <Card variant="bordered" padding="md" className="ai-card">
              <CardHeader 
                title={
                  <div className="card-title-ai">
                    <span>Supply Chain Finance</span>
                    <div className="ai-badge">Optimized</div>
                  </div>
                }
              />
              <CardBody>
                <div className="ai-card-stats">
                  <div className="stat-item">
                    <span className="stat-label">Active Facilities</span>
                    <span className="stat-value">15</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">AI Score</span>
                    <span className="stat-value success">94</span>
                  </div>
                </div>
                <div className="ai-insight-mini">
                  <Sparkles size={14} />
                  <span>Optimization saved $47K this month</span>
                </div>
                <Button variant="primary" style={{ marginTop: 'var(--spacing-4)', width: '100%' }} onClick={() => navigate(ROUTES.SCF_LIST)}>
                  <TrendingUp size={16} />
                  View SCF Dashboard
                </Button>
              </CardBody>
            </Card>
          </div>

      {/* Floating AI Assistant Button */}
      <button 
        className="ai-assistant-fab" 
        onClick={() => setShowCopilot(!showCopilot)}
        title="AI Assistant"
      >
        <MessageSquare size={24} />
      </button>

      {/* AI Copilot Panel */}
      {showCopilot && (
        <div className="ai-copilot-panel">
          <div className="copilot-header">
            <div className="copilot-title">
              <Brain size={20} />
              <h3>AI Copilot</h3>
            </div>
            <button className="close-btn" onClick={() => setShowCopilot(false)}>×</button>
          </div>
          <div className="copilot-body">
            <div className="copilot-message ai">
              <div className="message-avatar">
                <Brain size={16} />
              </div>
              <div className="message-content">
                <p>Hello {user?.username}! I'm your AI assistant. How can I help you today?</p>
                <div className="suggested-actions">
                  <button>Show portfolio summary</button>
                  <button>Analyze risk exposure</button>
                  <button>Find opportunities</button>
                </div>
              </div>
            </div>
          </div>
          <div className="copilot-input">
            <input type="text" placeholder="Ask me anything about your portfolio..." />
            <button className="send-btn">
              <Sparkles size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

