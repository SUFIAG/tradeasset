/**
 * Application Router
 * Defines all application routes
 */

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage, RegisterPage, ForgotPasswordPage, ResetPasswordPage, MfaSetupPage } from '../pages/auth';
import { TermsOfServicePage, PrivacyPolicyPage } from '../pages/legal';
import { DashboardLayout } from '../components/layout';
import { DashboardPage } from '../pages/DashboardPage';
import {
  LCListPage,
  BankGuaranteesPage,
  CollectionsPage,
  ScfPage,
  CustomersPage,
  CompliancePage,
  RiskPage,
  DocumentsPage,
  ShipmentsPage,
  AnalyticsPage,
  WorkflowsPage,
  KnowledgePage,
  SettingsPage,
  LCCreatePage,
  LCDraftsPage,
  BGCreatePage,
  BGExpiringPage,
  ScfCreatePage,
  ScfPendingPage,
  CollectionCreatePage,
  CustomerCreatePage,
  CustomerPendingKycPage,
  ComplianceChecksPage,
  ComplianceAlertsPage,
  RiskHighPage,
  AnalyticsReportsPage,
  AnalyticsExportPage,
  CopilotPage,
  ProfilePage,
  SecurityPage,
  NotificationsPage,
  HelpPage,
} from '../pages/modules';
import { ProtectedRoute } from '../components/ProtectedRoute';
import { ROUTES } from '../config/constants';

export const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path={ROUTES.HOME} element={<Navigate to={ROUTES.LOGIN} replace />} />
        <Route path={ROUTES.LOGIN} element={<LoginPage />} />
        <Route path={ROUTES.REGISTER} element={<RegisterPage />} />
        <Route path={ROUTES.FORGOT_PASSWORD} element={<ForgotPasswordPage />} />
        <Route path={ROUTES.RESET_PASSWORD} element={<ResetPasswordPage />} />

        {/* Legal pages */}
        <Route path="/terms" element={<TermsOfServicePage />} />
        <Route path="/privacy" element={<PrivacyPolicyPage />} />

        {/* Protected routes */}
        <Route element={<ProtectedRoute />}>
          <Route element={<DashboardLayout />}>
            <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
            <Route path={ROUTES.MFA_SETUP} element={<MfaSetupPage />} />

            {/* Module routes - backend integrated */}
            {/* Letters of Credit */}
            <Route path={ROUTES.LC_LIST} element={<LCListPage />} />
            <Route path={ROUTES.LC_CREATE} element={<LCCreatePage />} />
            <Route path={ROUTES.LC_DRAFTS} element={<LCDraftsPage />} />

            {/* Bank Guarantees */}
            <Route path={ROUTES.BG_LIST} element={<BankGuaranteesPage />} />
            <Route path={ROUTES.BG_CREATE} element={<BGCreatePage />} />
            <Route path={ROUTES.BG_EXPIRING} element={<BGExpiringPage />} />

            {/* Supply Chain Finance */}
            <Route path={ROUTES.SCF_LIST} element={<ScfPage />} />
            <Route path={ROUTES.SCF_CREATE} element={<ScfCreatePage />} />
            <Route path={ROUTES.SCF_PENDING} element={<ScfPendingPage />} />

            {/* Collections */}
            <Route path={ROUTES.COLLECTIONS_LIST} element={<CollectionsPage />} />
            <Route path={ROUTES.COLLECTION_CREATE} element={<CollectionCreatePage />} />

            {/* Customers */}
            <Route path={ROUTES.CUSTOMERS} element={<CustomersPage />} />
            <Route path={ROUTES.CUSTOMER_CREATE} element={<CustomerCreatePage />} />
            <Route path={ROUTES.CUSTOMERS_PENDING_KYC} element={<CustomerPendingKycPage />} />

            {/* Compliance */}
            <Route path={ROUTES.COMPLIANCE_DASHBOARD} element={<CompliancePage />} />
            <Route path={ROUTES.COMPLIANCE_CHECKS} element={<ComplianceChecksPage />} />
            <Route path={ROUTES.COMPLIANCE_ALERTS} element={<ComplianceAlertsPage />} />

            {/* Risk */}
            <Route path={ROUTES.RISK_DASHBOARD} element={<RiskPage />} />
            <Route path={ROUTES.RISK_ASSESSMENT} element={<RiskHighPage />} />
            <Route path={ROUTES.RISK_HIGH} element={<RiskHighPage />} />

            {/* Documents */}
            <Route path={ROUTES.DOCUMENTS} element={<DocumentsPage />} />

            {/* Analytics */}
            <Route path={ROUTES.ANALYTICS_DASHBOARD} element={<AnalyticsPage />} />
            <Route path={ROUTES.ANALYTICS_REPORTS} element={<AnalyticsReportsPage />} />
            <Route path={ROUTES.ANALYTICS_EXPORT} element={<AnalyticsExportPage />} />

            {/* Workflows, Shipments */}
            <Route path={ROUTES.WORKFLOW_LIST} element={<WorkflowsPage />} />
            <Route path={ROUTES.SHIPMENTS_LIST} element={<ShipmentsPage />} />

            {/* AI Copilot & Knowledge */}
            <Route path={ROUTES.COPILOT} element={<CopilotPage />} />
            <Route path={ROUTES.KNOWLEDGE_BASE} element={<KnowledgePage />} />

            {/* Notifications & Help */}
            <Route path={ROUTES.NOTIFICATIONS} element={<NotificationsPage />} />
            <Route path={ROUTES.HELP} element={<HelpPage />} />

            {/* Settings */}
            <Route path={ROUTES.SETTINGS} element={<SettingsPage />} />
            <Route path={ROUTES.PROFILE} element={<ProfilePage />} />
            <Route path={ROUTES.SECURITY} element={<SecurityPage />} />
          </Route>
        </Route>

        {/* 404 Not Found */}
        <Route path="*" element={<Navigate to={ROUTES.LOGIN} replace />} />
      </Routes>
    </BrowserRouter>
  );
};

