/**
 * Modules Service
 * API integration for all trade-finance backend modules.
 * Endpoint paths mirror the backend controllers exactly.
 */

import { apiClient } from '../lib/api-client';
import { API_CONFIG } from '../config/constants';

const E = API_CONFIG.ENDPOINTS;

/** Spring Data Page envelope */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface DashboardMetrics {
  totalTransactions?: number;
  activeTransactions?: number;
  totalVolume?: number;
  averageProcessingTime?: number;
  successRate?: number;
  complianceScore?: number;
  riskScore?: number;
  timestamp?: string;
}

const unwrapList = (data: any): any[] => {
  if (Array.isArray(data)) return data;
  if (data && Array.isArray(data.content)) return data.content;
  return [];
};

// ---------------------------------------------------------------------------
// Letters of Credit  (/api/v1/trade/lc)  -> Page<LCResponse>
// ---------------------------------------------------------------------------
export const lcService = {
  list: async (page = 0, size = 20) =>
    unwrapList((await apiClient.get(E.LC, { params: { page, size } })).data),
  getById: async (lcId: string) => (await apiClient.get(`${E.LC}/${lcId}`)).data,
  create: async (payload: any) => (await apiClient.post(E.LC, payload)).data,
  submit: async (lcId: string) => (await apiClient.post(`${E.LC}/${lcId}/submit`)).data,
  approve: async (lcId: string, payload?: any) => (await apiClient.post(`${E.LC}/${lcId}/approve`, payload)).data,
  reject: async (lcId: string, payload?: any) => (await apiClient.post(`${E.LC}/${lcId}/reject`, payload)).data,
  issue: async (lcId: string) => (await apiClient.post(`${E.LC}/${lcId}/issue`)).data,
};

// ---------------------------------------------------------------------------
// Bank Guarantees  (/api/v1/bank-guarantees)  -> List<BankGuaranteeResponse>
// ---------------------------------------------------------------------------
export const bgService = {
  list: async () => unwrapList((await apiClient.get(E.BG)).data),
  expiring: async () => unwrapList((await apiClient.get(`${E.BG}/expiring`)).data),
  getById: async (id: string) => (await apiClient.get(`${E.BG}/${id}`)).data,
  create: async (payload: any) => (await apiClient.post(E.BG, payload)).data,
  submit: async (id: string) => (await apiClient.post(`${E.BG}/${id}/submit`)).data,
  approve: async (id: string, payload?: any) => (await apiClient.post(`${E.BG}/${id}/approve`, payload)).data,
};

// ---------------------------------------------------------------------------
// Documentary Collections  (/api/v1/documentary-collections)
// ---------------------------------------------------------------------------
export const collectionsService = {
  list: async () => unwrapList((await apiClient.get(E.COLLECTIONS)).data),
  getById: async (id: string) => (await apiClient.get(`${E.COLLECTIONS}/${id}`)).data,
  create: async (payload: any) => (await apiClient.post(E.COLLECTIONS, payload)).data,
  submit: async (id: string) => (await apiClient.post(`${E.COLLECTIONS}/${id}/submit`)).data,
};

// ---------------------------------------------------------------------------
// Supply Chain Finance  (/api/v1/scf)  -> List<SCFResponse>
// ---------------------------------------------------------------------------
export const scfService = {
  list: async () => unwrapList((await apiClient.get(E.SCF)).data),
  dueSoon: async () => unwrapList((await apiClient.get(`${E.SCF}/due-soon`)).data),
  overdue: async () => unwrapList((await apiClient.get(`${E.SCF}/overdue`)).data),
  getById: async (id: string) => (await apiClient.get(`${E.SCF}/${id}`)).data,
  create: async (payload: any) => (await apiClient.post(E.SCF, payload)).data,
};

// ---------------------------------------------------------------------------
// Customers  (/api/v1/customers)  -> Page<CustomerResponse>
// ---------------------------------------------------------------------------
export const customersService = {
  list: async (page = 0, size = 20) =>
    unwrapList((await apiClient.get(E.CUSTOMERS, { params: { page, size } })).data),
  search: async (query: string, page = 0, size = 20) =>
    unwrapList((await apiClient.get(`${E.CUSTOMERS}/search`, { params: { query, page, size } })).data),
  getById: async (id: string) => (await apiClient.get(`${E.CUSTOMERS}/${id}`)).data,
  create: async (payload: any) => (await apiClient.post(E.CUSTOMERS, payload)).data,
};

// ---------------------------------------------------------------------------
// Documents  (/api/v1/documents)  -> Page<DocumentResponse>
// ---------------------------------------------------------------------------
export const documentsService = {
  list: async (page = 0, size = 20) =>
    unwrapList((await apiClient.get(E.DOCUMENTS, { params: { page, size } })).data),
  getById: async (id: string) => (await apiClient.get(`${E.DOCUMENTS}/${id}`)).data,
  upload: async (formData: FormData) =>
    (await apiClient.post(E.DOCUMENTS, formData, { headers: { 'Content-Type': 'multipart/form-data' } })).data,
};

// ---------------------------------------------------------------------------
// Compliance Checks  (/api/compliance/checks)
// ---------------------------------------------------------------------------
export const complianceService = {
  reviewRequired: async () => unwrapList((await apiClient.get(`${E.COMPLIANCE}/review-required`)).data),
  getById: async (id: string) => (await apiClient.get(`${E.COMPLIANCE}/${id}`)).data,
  approve: async (id: string, payload?: any) => (await apiClient.post(`${E.COMPLIANCE}/${id}/approve`, payload)).data,
  reject: async (id: string, payload?: any) => (await apiClient.post(`${E.COMPLIANCE}/${id}/reject`, payload)).data,
};

// ---------------------------------------------------------------------------
// Risk Assessments  (/api/risk/assessments)
// ---------------------------------------------------------------------------
export const riskService = {
  highRisk: async () => unwrapList((await apiClient.get(`${E.RISK}/high-risk`)).data),
  getById: async (id: string) => (await apiClient.get(`${E.RISK}/${id}`)).data,
  byEntity: async (entityType: string, entityId: string) =>
    unwrapList((await apiClient.get(`${E.RISK}/entity/${entityType}/${entityId}`)).data),
};

// ---------------------------------------------------------------------------
// Shipments  (/api/v1/shipments)
// ---------------------------------------------------------------------------
export const shipmentsService = {
  active: async () => unwrapList((await apiClient.get(`${E.SHIPMENTS}/active`)).data),
  delayed: async () => unwrapList((await apiClient.get(`${E.SHIPMENTS}/delayed`)).data),
  statistics: async () => (await apiClient.get(`${E.SHIPMENTS}/statistics`)).data,
  getById: async (id: string) => (await apiClient.get(`${E.SHIPMENTS}/${id}`)).data,
};

// ---------------------------------------------------------------------------
// Workflow  (/api/v1/workflow)
// ---------------------------------------------------------------------------
export const workflowService = {
  getById: async (id: string) => (await apiClient.get(`${E.WORKFLOW}/${id}`)).data,
  byEntity: async (entityType: string, entityId: string) =>
    (await apiClient.get(`${E.WORKFLOW}/entity/${entityType}/${entityId}`)).data,
  workloadStatistics: async () => (await apiClient.get(`${E.WORKFLOW}/ai/workload/statistics`)).data,
  approveTask: async (workflowId: string, payload: any) =>
    (await apiClient.post(`${E.WORKFLOW}/${workflowId}/tasks/approve`, payload)).data,
  rejectTask: async (workflowId: string, payload: any) =>
    (await apiClient.post(`${E.WORKFLOW}/${workflowId}/tasks/reject`, payload)).data,
};

// ---------------------------------------------------------------------------
// Analytics  (/api/v1/analytics)
// ---------------------------------------------------------------------------
export const analyticsService = {
  dashboard: async (): Promise<DashboardMetrics> => (await apiClient.get(`${E.ANALYTICS}/dashboard`)).data,
  transactions: async (params?: any) => (await apiClient.get(`${E.ANALYTICS}/transactions`, { params })).data,
  compliance: async () => (await apiClient.get(`${E.ANALYTICS}/compliance`)).data,
  risk: async () => (await apiClient.get(`${E.ANALYTICS}/risk`)).data,
  performance: async () => (await apiClient.get(`${E.ANALYTICS}/performance`)).data,
};

// ---------------------------------------------------------------------------
// Knowledge Base  (/api/v1/knowledge)
// ---------------------------------------------------------------------------
export const knowledgeService = {
  ask: async (question: string) => (await apiClient.post(`${E.KNOWLEDGE}/ask`, { question })).data,
  search: async (query: string) => (await apiClient.post(`${E.KNOWLEDGE}/search`, { query })).data,
};

// ---------------------------------------------------------------------------
// Audit  (/api/v1/audit)
// ---------------------------------------------------------------------------
export const auditService = {
  list: async (page = 0, size = 20) =>
    unwrapList((await apiClient.get(E.AUDIT, { params: { page, size } })).data),
};
