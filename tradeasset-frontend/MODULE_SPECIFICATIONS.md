# TradeSphere AI - Detailed Module Specifications
## Complete API Integration & Component Specifications

**Version:** 1.0.0  
**Last Updated:** July 20, 2026  
**Document Type:** Detailed Technical Specification

---

## Table of Contents
1. [Module 3: Bank Guarantee](#module-3-bank-guarantee)
2. [Module 4: Supply Chain Finance](#module-4-supply-chain-finance)
3. [Module 5: Documentary Collection](#module-5-documentary-collection)
4. [Module 6: Customer Management](#module-6-customer-management)
5. [Module 7: Compliance & Screening](#module-7-compliance--screening)
6. [Module 8: Risk Management](#module-8-risk-management)
7. [Module 9: Document Management](#module-9-document-management)
8. [Module 10: Analytics & Reporting](#module-10-analytics--reporting)
9. [Module 11: Workflow Management](#module-11-workflow-management)
10. [Module 12: Shipment Tracking](#module-12-shipment-tracking)
11. [Module 13: AI Copilot](#module-13-ai-copilot)
12. [Module 14: Knowledge Base](#module-14-knowledge-base)
13. [Module 15: Security (SSO & MFA)](#module-15-security-sso--mfa)
14. [Module 16: Settings & Configuration](#module-16-settings--configuration)
15. [Complete API Endpoint Reference](#complete-api-endpoint-reference)

---

## MODULE 3: BANK GUARANTEE

### 3.1 API Endpoints
```typescript
// src/features/bank-guarantee/services/bgService.ts
import { api } from '@/lib/api';
import type { BankGuarantee, CreateBGRequest, IssueBGRequest, AmendmentRequest, ClaimRequest } from '../types/bg.types';

export const bgService = {
  // List & Search
  getAll: (filters?: any) =>
    api.get<{ content: BankGuarantee[]; totalElements: number }>('/bank-guarantees', { params: filters }),
  
  getById: (id: string) =>
    api.get<BankGuarantee>(`/bank-guarantees/${id}`),
  
  // CRUD Operations
  create: (data: CreateBGRequest) =>
    api.post<BankGuarantee>('/bank-guarantees', data),
  
  update: (id: string, data: Partial<CreateBGRequest>) =>
    api.put<BankGuarantee>(`/bank-guarantees/${id}`, data),
  
  // Workflow Actions
  submit: (id: string) =>
    api.post<void>(`/bank-guarantees/${id}/submit`),
  
  approve: (id: string, comments?: string) =>
    api.post<void>(`/bank-guarantees/${id}/approve`, { comments }),
  
  reject: (id: string, reason: string) =>
    api.post<void>(`/bank-guarantees/${id}/reject`, { reason }),
  
  issue: (id: string, data: IssueBGRequest) =>
    api.post<void>(`/bank-guarantees/${id}/issue`, data),
  
  // Amendment Management
  requestAmendment: (id: string, data: AmendmentRequest) =>
    api.post<void>(`/bank-guarantees/${id}/amendment`, data),
  
  approveAmendment: (id: string, amendmentId: string) =>
    api.post<void>(`/bank-guarantees/${id}/amendment/${amendmentId}/approve`),
  
  rejectAmendment: (id: string, amendmentId: string, reason: string) =>
    api.post<void>(`/bank-guarantees/${id}/amendment/${amendmentId}/reject`, { reason }),
  
  getAmendments: (id: string) =>
    api.get<Amendment[]>(`/bank-guarantees/${id}/amendments`),
  
  // Claim Management
  registerClaim: (id: string, data: ClaimRequest) =>
    api.post<void>(`/bank-guarantees/${id}/claim`, data),
  
  approveClaim: (id: string, claimId: string) =>
    api.post<void>(`/bank-guarantees/${id}/claim/${claimId}/approve`),
  
  rejectClaim: (id: string, claimId: string, reason: string) =>
    api.post<void>(`/bank-guarantees/${id}/claim/${claimId}/reject`, { reason }),
  
  getClaims: (id: string) =>
    api.get<Claim[]>(`/bank-guarantees/${id}/claims`),
  
  // Extension & Reduction
  requestExtension: (id: string, data: { newExpiryDate: string; reason: string }) =>
    api.post<void>(`/bank-guarantees/${id}/extend`, data),
  
  requestReduction: (id: string, data: { newAmount: number; reason: string }) =>
    api.post<void>(`/bank-guarantees/${id}/reduce`, data),
  
  // Cancellation & Release
  cancel: (id: string, reason: string) =>
    api.post<void>(`/bank-guarantees/${id}/cancel`, { reason }),
  
  release: (id: string) =>
    api.post<void>(`/bank-guarantees/${id}/release`),
  
  // Monitoring
  getExpiring: (days: number) =>
    api.get<BankGuarantee[]>('/bank-guarantees/expiring', { params: { days } }),
};
```

### 3.2 TypeScript Types
```typescript
// src/features/bank-guarantee/types/bg.types.ts
export enum BGType {
  PERFORMANCE = 'PERFORMANCE',
  ADVANCE_PAYMENT = 'ADVANCE_PAYMENT',
  BID_BOND = 'BID_BOND',
  PAYMENT = 'PAYMENT',
  RETENTION = 'RETENTION',
  CUSTOMS = 'CUSTOMS',
}

export enum BGStatus {
  DRAFT = 'DRAFT',
  SUBMITTED = 'SUBMITTED',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  ISSUED = 'ISSUED',
  ACTIVE = 'ACTIVE',
  CLAIMED = 'CLAIMED',
  EXPIRED = 'EXPIRED',
  RELEASED = 'RELEASED',
  CANCELLED = 'CANCELLED',
}

export interface BankGuarantee {
  id: string;
  bgNumber: string;
  type: BGType;
  status: BGStatus;
  applicant: {
    id: string;
    name: string;
  };
  beneficiary: {
    id: string;
    name: string;
  };
  amount: {
    value: number;
    currency: string;
  };
  issueDate?: string;
  effectiveDate: string;
  expiryDate: string;
  underlyingContract: string;
  terms: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBGRequest {
  applicantId: string;
  beneficiaryId: string;
  type: BGType;
  amount: number;
  currency: string;
  effectiveDate: string;
  expiryDate: string;
  underlyingContract: string;
  terms: string;
}

export interface IssueBGRequest {
  bgNumber: string;
  issueDate: string;
  swiftMessage?: string;
}

export interface AmendmentRequest {
  amendmentType: 'EXTEND_EXPIRY' | 'INCREASE_AMOUNT' | 'DECREASE_AMOUNT' | 'CHANGE_BENEFICIARY' | 'OTHER';
  description: string;
  newExpiryDate?: string;
  newAmount?: number;
  newBeneficiaryId?: string;
}

export interface ClaimRequest {
  claimAmount: number;
  claimDate: string;
  claimReason: string;
  supportingDocuments: string[];
}
```

### 3.3 BG List Page Component
```typescript
// src/features/bank-guarantee/pages/BGListPage.tsx
import React, { useState } from 'react';
import { Table, Button, Space, Tag, Input, Select, DatePicker, Card, Badge } from 'antd';
import { PlusOutlined, SearchOutlined, FilterOutlined, ExportOutlined, WarningOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { bgService } from '../services/bgService';
import { BGStatus, BGType } from '../types/bg.types';
import PageHeader from '@/components/common/PageHeader';
import StatusBadge from '@/components/common/StatusBadge';
import { formatCurrency, formatDate } from '@/lib/utils';

const BGListPage: React.FC = () => {
  const navigate = useNavigate();
  const [filters, setFilters] = useState({});

  const { data, isLoading } = useQuery({
    queryKey: ['bg-list', filters],
    queryFn: () => bgService.getAll(filters),
  });

  const columns = [
    {
      title: 'BG Number',
      dataIndex: 'bgNumber',
      key: 'bgNumber',
      render: (text: string, record: any) => (
        <Button type="link" onClick={() => navigate(`/bank-guarantee/${record.id}`)}>
          {text}
        </Button>
      ),
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      render: (type: BGType) => <Tag color="blue">{type.replace('_', ' ')}</Tag>,
    },
    {
      title: 'Applicant',
      dataIndex: ['applicant', 'name'],
      key: 'applicant',
    },
    {
      title: 'Beneficiary',
      dataIndex: ['beneficiary', 'name'],
      key: 'beneficiary',
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount: any) => formatCurrency(amount.value, amount.currency),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: BGStatus) => <StatusBadge status={status} />,
    },
    {
      title: 'Expiry Date',
      dataIndex: 'expiryDate',
      key: 'expiryDate',
      render: (date: string) => {
        const daysToExpiry = Math.ceil((new Date(date).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
        return (
          <Space>
            {formatDate(date)}
            {daysToExpiry <= 30 && daysToExpiry > 0 && (
              <Badge count={<WarningOutlined style={{ color: '#faad14' }} />} />
            )}
          </Space>
        );
      },
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: any) => (
        <Space>
          <Button size="small" onClick={() => navigate(`/bank-guarantee/${record.id}`)}>
            View
          </Button>
          {record.status === 'ACTIVE' && (
            <>
              <Button size="small" onClick={() => navigate(`/bank-guarantee/${record.id}/claim`)}>
                Claim
              </Button>
              <Button size="small" onClick={() => navigate(`/bank-guarantee/${record.id}/amend`)}>
                Amend
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Bank Guarantees"
        extra={[
          <Button key="export" icon={<ExportOutlined />}>Export</Button>,
          <Button key="expiring" icon={<WarningOutlined />} onClick={() => navigate('/bank-guarantee/expiring')}>
            Expiring Soon
          </Button>,
          <Button key="create" type="primary" icon={<PlusOutlined />} onClick={() => navigate('/bank-guarantee/create')}>
            New Bank Guarantee
          </Button>,
        ]}
      />

      <Card>
        <Space style={{ marginBottom: 16 }} wrap>
          <Input
            placeholder="Search BG Number, Applicant..."
            prefix={<SearchOutlined />}
            style={{ width: 300 }}
            onChange={(e) => setFilters({ ...filters, search: e.target.value })}
          />
          <Select
            placeholder="Type"
            style={{ width: 180 }}
            allowClear
            onChange={(value) => setFilters({ ...filters, type: value })}
          >
            <Select.Option value="PERFORMANCE">Performance</Select.Option>
            <Select.Option value="ADVANCE_PAYMENT">Advance Payment</Select.Option>
            <Select.Option value="BID_BOND">Bid Bond</Select.Option>
            <Select.Option value="PAYMENT">Payment</Select.Option>
            <Select.Option value="RETENTION">Retention</Select.Option>
          </Select>
          <Select
            placeholder="Status"
            style={{ width: 150 }}
            allowClear
            onChange={(value) => setFilters({ ...filters, status: value })}
          >
            <Select.Option value="DRAFT">Draft</Select.Option>
            <Select.Option value="ACTIVE">Active</Select.Option>
            <Select.Option value="CLAIMED">Claimed</Select.Option>
            <Select.Option value="EXPIRED">Expired</Select.Option>
          </Select>
          <Button icon={<FilterOutlined />}>More Filters</Button>
        </Space>

        <Table
          columns={columns}
          dataSource={data?.content}
          loading={isLoading}
          rowKey="id"
          pagination={{
            total: data?.totalElements,
            pageSize: 20,
            showSizeChanger: true,
          }}
        />
      </Card>
    </>
  );
};

export default BGListPage;
```

### 3.4 BG Create Form
```typescript
// src/features/bank-guarantee/pages/CreateBGPage.tsx
import React, { useState } from 'react';
import { Form, Input, InputNumber, Select, DatePicker, Button, Card, Steps, Row, Col, message } from 'antd';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { bgService } from '../services/bgService';
import { CreateBGSchema } from '../validators/bg.validators';
import PageHeader from '@/components/common/PageHeader';
import CustomerSearch from '@/features/customer/components/CustomerSearch';
import { BGType } from '../types/bg.types';

const CreateBGPage: React.FC = () => {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(0);
  
  const { control, handleSubmit, watch, formState: { errors } } = useForm({
    resolver: zodResolver(CreateBGSchema),
    defaultValues: {
      currency: 'USD',
    },
  });

  const createMutation = useMutation({
    mutationFn: bgService.create,
    onSuccess: () => {
      message.success('Bank Guarantee created successfully');
      navigate('/bank-guarantee/list');
    },
    onError: () => {
      message.error('Failed to create Bank Guarantee');
    },
  });

  const onSubmit = (data: any) => {
    createMutation.mutate(data);
  };

  const steps = [
    { title: 'Basic Info', description: 'Type & Parties' },
    { title: 'Amount & Dates', description: 'Financial Details' },
    { title: 'Terms', description: 'Conditions & Terms' },
    { title: 'Review', description: 'Review & Submit' },
  ];

  return (
    <>
      <PageHeader title="New Bank Guarantee" onBack={() => navigate('/bank-guarantee/list')} />

      <Card>
        <Steps current={currentStep} items={steps} style={{ marginBottom: 32 }} />

        <Form layout="vertical" onFinish={handleSubmit(onSubmit)}>
          {currentStep === 0 && (
            <Row gutter={16}>
              <Col span={24}>
                <Form.Item label="Guarantee Type" required>
                  <Controller
                    name="type"
                    control={control}
                    render={({ field }) => (
                      <Select {...field} size="large" placeholder="Select guarantee type">
                        <Select.Option value={BGType.PERFORMANCE}>
                          <div>
                            <div style={{ fontWeight: 500 }}>Performance Guarantee</div>
                            <div style={{ fontSize: 12, color: '#666' }}>Guarantees performance of contractual obligations</div>
                          </div>
                        </Select.Option>
                        <Select.Option value={BGType.ADVANCE_PAYMENT}>
                          <div>
                            <div style={{ fontWeight: 500 }}>Advance Payment Guarantee</div>
                            <div style={{ fontSize: 12, color: '#666' }}>Secures advance payment to supplier</div>
                          </div>
                        </Select.Option>
                        <Select.Option value={BGType.BID_BOND}>
                          <div>
                            <div style={{ fontWeight: 500 }}>Bid Bond</div>
                            <div style={{ fontSize: 12, color: '#666' }}>Guarantees bid validity and acceptance</div>
                          </div>
                        </Select.Option>
                        <Select.Option value={BGType.PAYMENT}>
                          <div>
                            <div style={{ fontWeight: 500 }}>Payment Guarantee</div>
                            <div style={{ fontSize: 12, color: '#666' }}>Guarantees payment obligations</div>
                          </div>
                        </Select.Option>
                        <Select.Option value={BGType.RETENTION}>
                          <div>
                            <div style={{ fontWeight: 500 }}>Retention Guarantee</div>
                            <div style={{ fontSize: 12, color: '#666' }}>Replaces retention money deduction</div>
                          </div>
                        </Select.Option>
                      </Select>
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Applicant (Principal)" required>
                  <Controller
                    name="applicantId"
                    control={control}
                    render={({ field }) => (
                      <CustomerSearch
                        {...field}
                        placeholder="Search applicant..."
                        error={errors.applicantId?.message}
                      />
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Beneficiary" required>
                  <Controller
                    name="beneficiaryId"
                    control={control}
                    render={({ field }) => (
                      <CustomerSearch
                        {...field}
                        placeholder="Search beneficiary..."
                        error={errors.beneficiaryId?.message}
                      />
                    )}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}

          {currentStep === 1 && (
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="Currency" required>
                  <Controller
                    name="currency"
                    control={control}
                    render={({ field }) => (
                      <Select {...field} size="large">
                        <Select.Option value="USD">USD - US Dollar</Select.Option>
                        <Select.Option value="EUR">EUR - Euro</Select.Option>
                        <Select.Option value="GBP">GBP - British Pound</Select.Option>
                        <Select.Option value="AED">AED - UAE Dirham</Select.Option>
                        <Select.Option value="SAR">SAR - Saudi Riyal</Select.Option>
                      </Select>
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Guarantee Amount" required>
                  <Controller
                    name="amount"
                    control={control}
                    render={({ field }) => (
                      <InputNumber
                        {...field}
                        size="large"
                        style={{ width: '100%' }}
                        min={0}
                        precision={2}
                        formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                        parser={(value) => value!.replace(/\$\s?|(,*)/g, '')}
                      />
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Effective Date" required>
                  <Controller
                    name="effectiveDate"
                    control={control}
                    render={({ field }) => (
                      <DatePicker {...field} size="large" style={{ width: '100%' }} />
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="Expiry Date" required>
                  <Controller
                    name="expiryDate"
                    control={control}
                    render={({ field }) => (
                      <DatePicker {...field} size="large" style={{ width: '100%' }} />
                    )}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}

          {currentStep === 2 && (
            <Row gutter={16}>
              <Col span={24}>
                <Form.Item label="Underlying Contract Reference" required>
                  <Controller
                    name="underlyingContract"
                    control={control}
                    render={({ field }) => (
                      <Input {...field} size="large" placeholder="Enter contract reference number" />
                    )}
                  />
                </Form.Item>
              </Col>
              <Col span={24}>
                <Form.Item label="Guarantee Terms & Conditions" required>
                  <Controller
                    name="terms"
                    control={control}
                    render={({ field }) => (
                      <Input.TextArea
                        {...field}
                        rows={8}
                        placeholder="Enter detailed terms and conditions..."
                      />
                    )}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}

          {currentStep === 3 && (
            <div>
              <h3>Review Bank Guarantee Details</h3>
              {/* Display summary of all entered data */}
            </div>
          )}

          <div style={{ marginTop: 24, textAlign: 'right' }}>
            {currentStep > 0 && (
              <Button style={{ marginRight: 8 }} onClick={() => setCurrentStep(currentStep - 1)}>
                Previous
              </Button>
            )}
            {currentStep < steps.length - 1 && (
              <Button type="primary" onClick={() => setCurrentStep(currentStep + 1)}>
                Next
              </Button>
            )}
            {currentStep === steps.length - 1 && (
              <Button type="primary" htmlType="submit" loading={createMutation.isPending}>
                Submit Guarantee Request
              </Button>
            )}
          </div>
        </Form>
      </Card>
    </>
  );
};

export default CreateBGPage;
```

---

## MODULE 4: SUPPLY CHAIN FINANCE (SCF)

### 4.1 API Endpoints
```typescript
// src/features/scf/services/scfService.ts
import { api } from '@/lib/api';

export const scfService = {
  // List & Search
  getAll: (filters?: any) =>
    api.get('/scf', { params: filters }),
  
  getById: (id: string) =>
    api.get(`/scf/${id}`),
  
  // Create & Approve
  create: (data: any) =>
    api.post('/scf', data),
  
  approve: (id: string, comments?: string) =>
    api.post(`/scf/${id}/approve`, { comments }),
  
  reject: (id: string, reason: string) =>
    api.post(`/scf/${id}/reject`, { reason }),
  
  // Disbursement & Repayment
  disburse: (id: string, data: any) =>
    api.post(`/scf/${id}/disburse`, data),
  
  repay: (id: string, data: any) =>
    api.post(`/scf/${id}/repay`, data),
  
  // Early Payment
  acceptEarlyPayment: (id: string, data: any) =>
    api.post(`/scf/${id}/accept-early-payment`, data),
  
  calculateDiscount: (invoiceAmount: number, terms: any) =>
    api.post('/scf/calculate-discount', { invoiceAmount, terms }),
  
  // Batch Processing
  createBatch: (data: any[]) =>
    api.post('/scf/batch', data),
  
  // Analytics
  getDashboard: () =>
    api.get('/scf/dashboard'),
  
  getFinancingRate: (customerId: string) =>
    api.get(`/scf/financing-rate/${customerId}`),
};
```

### 4.2 SCF Dashboard
```typescript
// src/features/scf/pages/SCFDashboard.tsx
import React from 'react';
import { Row, Col, Card, Statistic, Progress, Table } from 'antd';
import { DollarOutlined, RiseOutlined, CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { useQuery } from '@tanstack/react-query';
import { scfService } from '../services/scfService';
import PageHeader from '@/components/common/PageHeader';
import ReactECharts from 'echarts-for-react';

const SCFDashboard: React.FC = () => {
  const { data: dashboardData } = useQuery({
    queryKey: ['scf-dashboard'],
    queryFn: scfService.getDashboard,
  });

  return (
    <>
      <PageHeader title="Supply Chain Finance Dashboard" />

      {/* Key Metrics */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Financed"
              value={dashboardData?.totalFinanced || 0}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              prefix={<DollarOutlined />}
              suffix="M"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Active Facilities"
              value={dashboardData?.activeFacilities || 0}
              valueStyle={{ color: '#1890ff' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Pending Approvals"
              value={dashboardData?.pendingApprovals || 0}
              valueStyle={{ color: '#faad14' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Average Discount Rate"
              value={dashboardData?.avgDiscountRate || 0}
              precision={2}
              valueStyle={{ color: '#722ed1' }}
              prefix={<RiseOutlined />}
              suffix="%"
            />
          </Card>
        </Col>
      </Row>

      {/* Charts */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} lg={16}>
          <Card title="Financing Volume Trend">
            {/* ECharts line chart */}
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="Financing by Type">
            {/* Pie chart for Invoice Discounting, Factoring, etc. */}
          </Card>
        </Col>
      </Row>

      {/* Recent Transactions */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24}>
          <Card title="Recent Financing Requests">
            {/* Table of recent SCF requests */}
          </Card>
        </Col>
      </Row>
    </>
  );
};

export default SCFDashboard;
```

---

## MODULE 5: DOCUMENTARY COLLECTION

### 5.1 Collection Service
```typescript
// src/features/collection/services/collectionService.ts
export const collectionService = {
  getAll: (filters?: any) =>
    api.get('/collections', { params: filters }),
  
  getById: (id: string) =>
    api.get(`/collections/${id}`),
  
  create: (data: any) =>
    api.post('/collections', data),
  
  update: (id: string, data: any) =>
    api.put(`/collections/${id}`, data),
  
  submit: (id: string) =>
    api.post(`/collections/${id}/submit`),
  
  acceptDocuments: (id: string) =>
    api.post(`/collections/${id}/accept-documents`),
  
  releaseDocuments: (id: string) =>
    api.post(`/collections/${id}/release-documents`),
  
  processPayment: (id: string, data: any) =>
    api.post(`/collections/${id}/process-payment`, data),
};
```

---

## MODULE 6: CUSTOMER MANAGEMENT

### 6.1 API Endpoints
```typescript
// src/features/customer/services/customerService.ts
export const customerService = {
  // CRUD Operations
  getAll: (filters?: any) =>
    api.get('/customers', { params: filters }),
  
  getById: (id: string) =>
    api.get(`/customers/${id}`),
  
  getByNumber: (customerNumber: string) =>
    api.get(`/customers/number/${customerNumber}`),
  
  create: (data: any) =>
    api.post('/customers', data),
  
  update: (id: string, data: any) =>
    api.put(`/customers/${id}`, data),
  
  // Workflow
  submit: (id: string) =>
    api.post(`/customers/${id}/submit`),
  
  approve: (id: string, comments?: string) =>
    api.post(`/customers/${id}/approve`, { comments }),
  
  reject: (id: string, reason: string) =>
    api.post(`/customers/${id}/reject`, { reason }),
  
  activate: (id: string) =>
    api.post(`/customers/${id}/activate`),
  
  suspend: (id: string, reason: string) =>
    api.post(`/customers/${id}/suspend`, { reason }),
  
  reactivate: (id: string) =>
    api.post(`/customers/${id}/reactivate`),
  
  // KYC Management
  completeKYC: (id: string, data: any) =>
    api.post(`/customers/${id}/kyc/complete`, data),
  
  updateKYCStatus: (id: string, status: string) =>
    api.post(`/customers/${id}/kyc/status`, { status }),
  
  // Credit Management
  setCreditLimit: (id: string, data: { amount: number; currency: string; expiryDate: string }) =>
    api.post(`/customers/${id}/credit-limit`, data),
  
  getCreditUtilization: (id: string) =>
    api.get(`/customers/${id}/credit-utilization`),
  
  // Risk Assessment
  updateRiskRating: (id: string, data: { riskRating: string; riskScore: number }) =>
    api.post(`/customers/${id}/risk-rating`, data),
  
  // Search & Filter
  search: (query: string) =>
    api.get('/customers/search', { params: { query } }),
  
  getByStatus: (status: string) =>
    api.get(`/customers/by-status/${status}`),
  
  // Documents
  getDocuments: (id: string) =>
    api.get(`/customers/${id}/documents`),
  
  uploadDocument: (id: string, file: File, documentType: string) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentType', documentType);
    return api.post(`/customers/${id}/documents`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
```

### 6.2 Customer Profile Page
```typescript
// src/features/customer/pages/CustomerProfilePage.tsx
import React from 'react';
import { useParams } from 'react-router-dom';
import { Card, Tabs, Descriptions, Tag, Space, Button, Timeline } from 'antd';
import { useQuery } from '@tanstack/react-query';
import { customerService } from '../services/customerService';
import PageHeader from '@/components/common/PageHeader';
import KYCSection from '../components/KYCSection';
import CreditLimitSection from '../components/CreditLimitSection';
import TransactionHistory from '../components/TransactionHistory';
import DocumentLibrary from '../components/DocumentLibrary';

const CustomerProfilePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  
  const { data: customer, isLoading } = useQuery({
    queryKey: ['customer', id],
    queryFn: () => customerService.getById(id!),
  });

  if (isLoading) return <div>Loading...</div>;

  return (
    <>
      <PageHeader
        title={customer?.name || 'Customer Profile'}
        subTitle={`Customer #${customer?.customerNumber}`}
        tags={<Tag color={customer?.status === 'ACTIVE' ? 'green' : 'orange'}>{customer?.status}</Tag>}
        extra={[
          <Button key="edit">Edit</Button>,
          <Button key="suspend" danger>Suspend</Button>,
        ]}
      />

      <Card>
        <Tabs
          items={[
            {
              key: 'overview',
              label: 'Overview',
              children: (
                <Descriptions bordered column={2}>
                  <Descriptions.Item label="Customer Number">{customer?.customerNumber}</Descriptions.Item>
                  <Descriptions.Item label="Type">{customer?.type}</Descriptions.Item>
                  <Descriptions.Item label="Email">{customer?.email}</Descriptions.Item>
                  <Descriptions.Item label="Phone">{customer?.phone}</Descriptions.Item>
                  <Descriptions.Item label="Address" span={2}>{customer?.address}</Descriptions.Item>
                  <Descriptions.Item label="Country">{customer?.country}</Descriptions.Item>
                  <Descriptions.Item label="Tax ID">{customer?.taxId}</Descriptions.Item>
                  <Descriptions.Item label="KYC Status">
                    <Tag color={customer?.kycStatus === 'COMPLETED' ? 'green' : 'orange'}>
                      {customer?.kycStatus}
                    </Tag>
                  </Descriptions.Item>
                  <Descriptions.Item label="Risk Rating">
                    <Tag color={customer?.riskRating === 'LOW' ? 'green' : 'red'}>
                      {customer?.riskRating}
                    </Tag>
                  </Descriptions.Item>
                </Descriptions>
              ),
            },
            {
              key: 'kyc',
              label: 'KYC & Compliance',
              children: <KYCSection customerId={id!} />,
            },
            {
              key: 'credit',
              label: 'Credit Limit',
              children: <CreditLimitSection customerId={id!} />,
            },
            {
              key: 'transactions',
              label: 'Transaction History',
              children: <TransactionHistory customerId={id!} />,
            },
            {
              key: 'documents',
              label: 'Documents',
              children: <DocumentLibrary customerId={id!} />,
            },
            {
              key: 'activity',
              label: 'Activity Log',
              children: (
                <Timeline
                  items={customer?.activities?.map((activity: any) => ({
                    children: (
                      <div>
                        <div>{activity.description}</div>
                        <div style={{ fontSize: 12, color: '#999' }}>
                          {new Date(activity.timestamp).toLocaleString()}
                        </div>
                      </div>
                    ),
                  }))}
                />
              ),
            },
          ]}
        />
      </Card>
    </>
  );
};

export default CustomerProfilePage;
```

---

## MODULE 7: COMPLIANCE & SCREENING

### 7.1 API Endpoints
```typescript
// src/features/compliance/services/complianceService.ts
export const complianceService = {
  // Screening Checks
  initiateCheck: (data: { entityType: string; entityId: string; checkType: string }) =>
    api.post('/compliance/checks', data),
  
  getCheckStatus: (checkId: string) =>
    api.get(`/compliance/checks/${checkId}`),
  
  approveCheck: (checkId: string, comments?: string) =>
    api.post(`/compliance/checks/${checkId}/approve`, { comments }),
  
  rejectCheck: (checkId: string, reason: string) =>
    api.post(`/compliance/checks/${checkId}/reject`, { reason }),
  
  clearAsFalsePositive: (checkId: string, reason: string) =>
    api.post(`/compliance/checks/${checkId}/clear-false-positive`, { reason }),
  
  // Screening Hits
  getHits: (checkId: string) =>
    api.get(`/compliance/checks/${checkId}/hits`),
  
  investigateHit: (hitId: string, data: any) =>
    api.post(`/compliance/hits/${hitId}/investigate`, data),
  
  // Reports
  getComplianceReports: (filters?: any) =>
    api.get('/compliance/reports', { params: filters }),
  
  generateReport: (reportType: string, params: any) =>
    api.post('/compliance/reports/generate', { reportType, params }),
  
  exportReport: (reportId: string, format: 'pdf' | 'excel') =>
    api.get(`/compliance/reports/${reportId}/export`, {
      params: { format },
      responseType: 'blob',
    }),
  
  // Watchlist Management
  getWatchlists: () =>
    api.get('/compliance/watchlists'),
  
  searchWatchlist: (query: string) =>
    api.get('/compliance/watchlists/search', { params: { query } }),
};
```

### 7.2 Compliance Dashboard
```typescript
// src/features/compliance/pages/ComplianceDashboard.tsx
import React from 'react';
import { Row, Col, Card, Statistic, Table, Tag } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { useQuery } from '@tanstack/react-query';
import { complianceService } from '../services/complianceService';
import PageHeader from '@/components/common/PageHeader';

const ComplianceDashboard: React.FC = () => {
  const { data: stats } = useQuery({
    queryKey: ['compliance-stats'],
    queryFn: () => api.get('/compliance/stats'),
  });

  return (
    <>
      <PageHeader title="Compliance & Screening Dashboard" />

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Screening Checks (Today)"
              value={stats?.totalChecksToday || 0}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Cleared"
              value={stats?.clearedChecks || 0}
              valueStyle={{ color: '#3f8600' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Pending Review"
              value={stats?.pendingChecks || 0}
              valueStyle={{ color: '#faad14' }}
              prefix={<ExclamationCircleOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Hits Detected"
              value={stats?.hitsDetected || 0}
              valueStyle={{ color: '#cf1322' }}
              prefix={<CloseCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} lg={12}>
          <Card title="Recent Screening Checks">
            {/* Table of recent checks */}
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="Pending Hits (Require Action)">
            {/* Table of hits requiring investigation */}
          </Card>
        </Col>
      </Row>
    </>
  );
};

export default ComplianceDashboard;
```

---

## MODULE 8: RISK MANAGEMENT

### 8.1 API Endpoints
```typescript
// src/features/risk/services/riskService.ts
export const riskService = {
  // Risk Assessments
  initiateAssessment: (data: { entityType: string; entityId: string }) =>
    api.post('/risk/assessments', data),
  
  getAssessment: (assessmentId: string) =>
    api.get(`/risk/assessments/${assessmentId}`),
  
  getAssessmentsByEntity: (entityType: string, entityId: string) =>
    api.get(`/risk/assessments/${entityType}/${entityId}`),
  
  // Risk Scoring
  calculateRiskScore: (data: any) =>
    api.post('/risk/calculate-score', data),
  
  updateRiskScore: (assessmentId: string, score: number) =>
    api.put(`/risk/assessments/${assessmentId}/score`, { score }),
  
  // Risk Monitoring
  getHighRiskEntities: () =>
    api.get('/risk/high-risk-entities'),
  
  getRiskDashboard: () =>
    api.get('/risk/dashboard'),
  
  // Risk Reports
  generateRiskReport: (params: any) =>
    api.post('/risk/reports/generate', params),
  
  exportRiskReport: (reportId: string, format: 'pdf' | 'excel') =>
    api.get(`/risk/reports/${reportId}/export`, {
      params: { format },
      responseType: 'blob',
    }),
};
```

---

## MODULE 9: DOCUMENT MANAGEMENT

### 9.1 API Endpoints
```typescript
// src/features/document/services/documentService.ts
export const documentService = {
  // Upload & Storage
  upload: (file: File, metadata: any) => {
    const formData = new FormData();
    formData.append('file', file);
    Object.keys(metadata).forEach(key => {
      formData.append(key, metadata[key]);
    });
    return api.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  uploadMultiple: (files: File[], metadata: any) => {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    Object.keys(metadata).forEach(key => {
      formData.append(key, metadata[key]);
    });
    return api.post('/documents/upload-multiple', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  // Document Retrieval
  getById: (documentId: string) =>
    api.get(`/documents/${documentId}`),
  
  getAll: (filters?: any) =>
    api.get('/documents', { params: filters }),
  
  download: (documentId: string) =>
    api.get(`/documents/${documentId}/download`, { responseType: 'blob' }),
  
  // AI Extraction
  extractData: (documentId: string) =>
    api.post(`/documents/${documentId}/extract`),
  
  getExtractionStatus: (documentId: string) =>
    api.get(`/documents/${documentId}/extraction-status`),
  
  getExtractedData: (documentId: string) =>
    api.get(`/documents/${documentId}/extracted-data`),
  
  // OCR
  performOCR: (documentId: string) =>
    api.post(`/documents/${documentId}/ocr`),
  
  getOCRResult: (documentId: string) =>
    api.get(`/documents/${documentId}/ocr-result`),
  
  // Verification
  verify: (documentId: string, verificationData: any) =>
    api.post(`/documents/${documentId}/verify`, verificationData),
  
  approve: (documentId: string, comments?: string) =>
    api.post(`/documents/${documentId}/approve`, { comments }),
  
  reject: (documentId: string, reason: string) =>
    api.post(`/documents/${documentId}/reject`, { reason }),
  
  // Search
  search: (query: string, filters?: any) =>
    api.get('/documents/search', { params: { query, ...filters } }),
  
  // Virus Scanning
  scanForVirus: (documentId: string) =>
    api.post(`/documents/${documentId}/scan`),
  
  getScanResult: (documentId: string) =>
    api.get(`/documents/${documentId}/scan-result`),
};
```

### 9.2 Document Upload Component
```typescript
// src/features/document/components/DocumentUpload.tsx
import React, { useState } from 'react';
import { Upload, Button, message, Progress, Card, List, Tag } from 'antd';
import { InboxOutlined, FileOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { useMutation } from '@tantml:query';
import { documentService } from '../services/documentService';
import type { UploadFile } from 'antd/es/upload/interface';

const { Dragger } = Upload;

const DocumentUpload: React.FC = () => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [uploadProgress, setUploadProgress] = useState<Record<string, number>>({});

  const uploadMutation = useMutation({
    mutationFn: documentService.upload,
    onSuccess: (data, variables) => {
      message.success(`${variables.name} uploaded successfully`);
    },
    onError: () => {
      message.error('Upload failed');
    },
  });

  const handleUpload = async (file: File) => {
    try {
      await uploadMutation.mutateAsync(file, {
        documentType: 'TRADE_DOCUMENT',
        category: 'GENERAL',
      });
    } catch (error) {
      console.error('Upload error:', error);
    }
  };

  const uploadProps = {
    name: 'file',
    multiple: true,
    fileList,
    beforeUpload: (file: File) => {
      // Validate file size (max 50MB)
      const isLt50M = file.size / 1024 / 1024 < 50;
      if (!isLt50M) {
        message.error('File must be smaller than 50MB!');
        return Upload.LIST_IGNORE;
      }
      return false; // Prevent auto upload
    },
    onChange: (info: any) => {
      setFileList(info.fileList);
    },
    onRemove: (file: UploadFile) => {
      setFileList(fileList.filter(f => f.uid !== file.uid));
    },
  };

  const handleBatchUpload = async () => {
    for (const file of fileList) {
      if (file.originFileObj) {
        await handleUpload(file.originFileObj);
      }
    }
  };

  return (
    <Card title="Upload Documents">
      <Dragger {...uploadProps}>
        <p className="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p className="ant-upload-text">Click or drag files to this area to upload</p>
        <p className="ant-upload-hint">
          Support for single or bulk upload. Supported formats: PDF, DOC, DOCX, XLS, XLSX, PNG, JPG
        </p>
      </Dragger>

      {fileList.length > 0 && (
        <div style={{ marginTop: 16 }}>
          <Button type="primary" onClick={handleBatchUpload} loading={uploadMutation.isPending}>
            Upload {fileList.length} file(s)
          </Button>
        </div>
      )}

      <List
        style={{ marginTop: 16 }}
        dataSource={fileList}
        renderItem={(file) => (
          <List.Item>
            <List.Item.Meta
              avatar={<FileOutlined />}
              title={file.name}
              description={`${(file.size! / 1024).toFixed(2)} KB`}
            />
            {uploadProgress[file.uid] !== undefined && (
              <Progress percent={uploadProgress[file.uid]} style={{ width: 200 }} />
            )}
            {file.status === 'done' && <CheckCircleOutlined style={{ color: '#52c41a' }} />}
          </List.Item>
        )}
      />
    </Card>
  );
};

export default DocumentUpload;
```

---

## MODULE 10: ANALYTICS & REPORTING

### 10.1 API Endpoints
```typescript
// src/features/analytics/services/analyticsService.ts
export const analyticsService = {
  // Dashboard Metrics
  getDashboardMetrics: () =>
    api.get('/analytics/dashboard'),
  
  getTransactionAnalytics: (filters?: any) =>
    api.get('/analytics/transactions', { params: filters }),
  
  getComplianceAnalytics: () =>
    api.get('/analytics/compliance'),
  
  getRiskAnalytics: () =>
    api.get('/analytics/risk'),
  
  getPerformanceMetrics: () =>
    api.get('/analytics/performance'),
  
  // Custom Reports
  generateReport: (data: { reportType: string; parameters: any }) =>
    api.post('/analytics/reports', data),
  
  getReport: (reportId: string) =>
    api.get(`/analytics/reports/${reportId}`),
  
  getAllReports: () =>
    api.get('/analytics/reports'),
  
  // Export
  exportReport: (reportId: string, format: 'pdf' | 'excel' | 'csv') =>
    api.get(`/analytics/reports/${reportType}/export`, {
      params: { format },
      responseType: 'blob',
    }),
  
  // Metrics
  recordMetric: (data: { metricName: string; value: number; tags: Record<string, string> }) =>
    api.post('/analytics/metrics', data),
  
  // Cache
  getCacheStats: () =>
    api.get('/analytics/cache/stats'),
  
  clearCache: () =>
    api.post('/analytics/cache/clear'),
};
```

---

## MODULE 11: WORKFLOW MANAGEMENT

### 11.1 API Endpoints
```typescript
// src/features/workflow/services/workflowService.ts
export const workflowService = {
  // Workflow Operations
  create: (data: { entityType: string; entityId: string; workflowType: string }) =>
    api.post('/workflow', data),
  
  getById: (workflowId: string) =>
    api.get(`/workflow/${workflowId}`),
  
  getByEntity: (entityType: string, entityId: string) =>
    api.get(`/workflow/entity/${entityType}/${entityId}`),
  
  // Task Management
  getMyTasks: (filters?: any) =>
    api.get('/workflow/tasks/my-tasks', { params: filters }),
  
  getPendingApprovals: () =>
    api.get('/workflow/tasks/pending-approvals'),
  
  approveTask: (workflowId: string, taskId: string, comments?: string) =>
    api.post(`/workflow/${workflowId}/tasks/approve`, { taskId, comments }),
  
  rejectTask: (workflowId: string, taskId: string, reason: string) =>
    api.post(`/workflow/${workflowId}/tasks/reject`, { taskId, reason }),
  
  reassignTask: (workflowId: string, taskId: string, userId: string) =>
    api.post(`/workflow/${workflowId}/tasks/${taskId}/reassign`, { userId }),
  
  // Monitoring
  getWorkflowMonitor: () =>
    api.get('/workflow/monitor'),
  
  getSLAViolations: () =>
    api.get('/workflow/sla-violations'),
  
  // AI Routing
  getRecommendedAssignee: (taskId: string) =>
    api.get(`/workflow/ai-routing/recommend/${taskId}`),
};
```

### 11.2 Task List Component
```typescript
// src/features/workflow/pages/TaskListPage.tsx
import React from 'react';
import { Table, Button, Space, Tag, Badge } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { workflowService } from '../services/workflowService';
import PageHeader from '@/components/common/PageHeader';
import { formatDate, calculateDaysUntilDue } from '@/lib/utils';

const TaskListPage: React.FC = () => {
  const navigate = useNavigate();
  
  const { data: tasks, isLoading } = useQuery({
    queryKey: ['my-tasks'],
    queryFn: workflowService.getMyTasks,
  });

  const columns = [
    {
      title: 'Task',
      dataIndex: 'taskName',
      key: 'taskName',
    },
    {
      title: 'Entity',
      key: 'entity',
      render: (record: any) => `${record.entityType} - ${record.entityId}`,
    },
    {
      title: 'Priority',
      dataIndex: 'priority',
      key: 'priority',
      render: (priority: string) => {
        const color = priority === 'HIGH' ? 'red' : priority === 'MEDIUM' ? 'orange' : 'blue';
        return <Tag color={color}>{priority}</Tag>;
      },
    },
    {
      title: 'Due Date',
      dataIndex: 'dueDate',
      key: 'dueDate',
      render: (date: string) => {
        const days = calculateDaysUntilDue(date);
        return (
          <Space>
            {formatDate(date)}
            {days <= 1 && <Badge status="error" text="Overdue" />}
            {days > 1 && days <= 3 && <Badge status="warning" text="Due Soon" />}
          </Space>
        );
      },
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (record: any) => (
        <Space>
          <Button size="small" type="primary" onClick={() => navigate(`/workflow/task/${record.id}`)}>
            Review
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <>
      <PageHeader title="My Tasks" />
      <Table columns={columns} dataSource={tasks} loading={isLoading} rowKey="id" />
    </>
  );
};

export default TaskListPage;
```

---

## MODULE 12: SHIPMENT TRACKING

### 12.1 API Endpoints
```typescript
// src/features/shipment/services/shipmentService.ts
export const shipmentService = {
  getAll: (filters?: any) =>
    api.get('/shipments', { params: filters }),
  
  getById: (shipmentId: string) =>
    api.get(`/shipments/${shipmentId}`),
  
  track: (trackingNumber: string, carrier: string) =>
    api.get(`/shipments/track`, { params: { trackingNumber, carrier } }),
  
  getTrackingHistory: (shipmentId: string) =>
    api.get(`/shipments/${shipmentId}/tracking-history`),
  
  updateStatus: (shipmentId: string, status: string) =>
    api.post(`/shipments/${shipmentId}/status`, { status }),
  
  linkToTransaction: (shipmentId: string, transactionId: string, transactionType: string) =>
    api.post(`/shipments/${shipmentId}/link`, { transactionId, transactionType }),
};
```

---

## MODULE 13: AI COPILOT

### 13.1 API Endpoints
```typescript
// src/features/copilot/services/copilotService.ts
export const copilotService = {
  // Conversation Management
  createConversation: () =>
    api.post('/copilot/conversations'),
  
  getConversation: (conversationId: string) =>
    api.get(`/copilot/conversations/${conversationId}`),
  
  getMyConversations: () =>
    api.get('/copilot/conversations/my-conversations'),
  
  // Messaging
  sendMessage: (conversationId: string, message: string) =>
    api.post(`/copilot/conversations/${conversationId}/messages`, { message }),
  
  getMessages: (conversationId: string) =>
    api.get(`/copilot/conversations/${conversationId}/messages`),
  
  // Intent Recognition
  recognizeIntent: (message: string) =>
    api.post('/copilot/intents/recognize', { message }),
  
  // Actions
  executeAction: (conversationId: string, action: string, parameters: any) =>
    api.post(`/copilot/conversations/${conversationId}/execute`, { action, parameters }),
};
```

### 13.2 Copilot Widget
```typescript
// src/features/copilot/components/CopilotWidget.tsx
import React, { useState, useRef, useEffect } from 'react';
import { FloatButton, Drawer, Input, List, Avatar, Space, Typography, Button } from 'antd';
import { RobotOutlined, SendOutlined } from '@ant-design/icons';
import { useMutation, useQuery } from '@tanstack/react-query';
import { copilotService } from '../services/copilotService';
import styles from './CopilotWidget.module.css';

const { TextArea } = Input;
const { Text } = Typography;

const CopilotWidget: React.FC = () => {
  const [open, setOpen] = useState(false);
  const [input, setInput] = useState('');
  const [conversationId, setConversationId] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Create conversation on mount
  useEffect(() => {
    copilotService.createConversation().then((conv) => {
      setConversationId(conv.id);
    });
  }, []);

  // Fetch messages
  const { data: messages = [], refetch } = useQuery({
    queryKey: ['copilot-messages', conversationId],
    queryFn: () => copilotService.getMessages(conversationId!),
    enabled: !!conversationId,
    refetchInterval: 2000, // Poll every 2 seconds
  });

  // Send message mutation
  const sendMessageMutation = useMutation({
    mutationFn: (message: string) => copilotService.sendMessage(conversationId!, message),
    onSuccess: () => {
      setInput('');
      refetch();
    },
  });

  const handleSend = () => {
    if (input.trim() && conversationId) {
      sendMessageMutation.mutate(input);
    }
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <>
      <FloatButton
        icon={<RobotOutlined />}
        type="primary"
        style={{ right: 24, bottom: 24 }}
        onClick={() => setOpen(true)}
        badge={{ count: 0 }}
      />

      <Drawer
        title="AI Copilot Assistant"
        placement="right"
        open={open}
        onClose={() => setOpen(false)}
        width={400}
      >
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
          {/* Messages */}
          <div style={{ flex: 1, overflowY: 'auto', marginBottom: 16 }}>
            <List
              dataSource={messages}
              renderItem={(msg: any) => (
                <div
                  style={{
                    display: 'flex',
                    justifyContent: msg.role === 'USER' ? 'flex-end' : 'flex-start',
                    marginBottom: 12,
                  }}
                >
                  {msg.role === 'ASSISTANT' && (
                    <Avatar icon={<RobotOutlined />} style={{ marginRight: 8 }} />
                  )}
                  <div
                    style={{
                      maxWidth: '70%',
                      padding: '8px 12px',
                      borderRadius: 8,
                      backgroundColor: msg.role === 'USER' ? '#1890ff' : '#f0f0f0',
                      color: msg.role === 'USER' ? '#fff' : '#000',
                    }}
                  >
                    <Text style={{ color: 'inherit' }}>{msg.content}</Text>
                  </div>
                </div>
              )}
            />
            <div ref={messagesEndRef} />
          </div>

          {/* Input */}
          <Space.Compact style={{ width: '100%' }}>
            <TextArea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onPressEnter={(e) => {
                if (e.shiftKey) return;
                e.preventDefault();
                handleSend();
              }}
              placeholder="Ask me anything..."
              autoSize={{ minRows: 1, maxRows: 4 }}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              onClick={handleSend}
              loading={sendMessageMutation.isPending}
            />
          </Space.Compact>
        </div>
      </Drawer>
    </>
  );
};

export default CopilotWidget;
```

---

## MODULE 14: KNOWLEDGE BASE

### 14.1 API Endpoints
```typescript
// src/features/knowledge/services/knowledgeService.ts
export const knowledgeService = {
  // Search
  search: (query: string, filters?: any) =>
    api.get('/knowledge/search', { params: { query, ...filters } }),
  
  semanticSearch: (query: string, topK: number = 10) =>
    api.get('/knowledge/semantic-search', { params: { query, topK } }),
  
  // Documents
  getDocuments: () =>
    api.get('/knowledge/documents'),
  
  getDocument: (documentId: string) =>
    api.get(`/knowledge/documents/${documentId}`),
  
  uploadDocument: (file: File, metadata: any) => {
    const formData = new FormData();
    formData.append('file', file);
    Object.keys(metadata).forEach(key => formData.append(key, metadata[key]));
    return api.post('/knowledge/documents', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  // AI Answers
  askQuestion: (question: string, context?: string) =>
    api.post('/knowledge/ask', { question, context }),
};
```

---

## MODULE 15: SECURITY (SSO & MFA)

### 15.1 SSO Configuration
```typescript
// src/features/security/services/ssoService.ts
export const ssoService = {
  // SSO Session Management
  initiateSso: (provider: string, returnUrl: string) =>
    api.post('/sso/initiate', { provider, returnUrl }),
  
  processSsoCallback: (code: string, state: string) =>
    api.post('/sso/callback', { code, state }),
  
  logout: (sessionId: string) =>
    api.post(`/sso/logout/${sessionId}`),
  
  validateSession: (sessionId: string) =>
    api.get(`/sso/validate/${sessionId}`),
  
  // SSO Configuration
  getProviders: () =>
    api.get('/sso/providers'),
  
  configureProvider: (provider: string, config: any) =>
    api.post(`/sso/providers/${provider}/configure`, config),
};
```

### 15.2 MFA Management
```typescript
// src/features/security/services/mfaService.ts
export const mfaService = {
  // MFA Setup
  setup: (method: 'TOTP' | 'SMS' | 'EMAIL') =>
    api.post('/mfa/setup', { method }),
  
  enable: (code: string) =>
    api.post('/mfa/enable', { code }),
  
  disable: (password: string) =>
    api.post('/mfa/disable', { password }),
  
  // OTP
  generateOtp: () =>
    api.post('/mfa/otp/generate'),
  
  verifyCode: (code: string) =>
    api.post('/mfa/otp/verify', { code }),
  
  // Backup Codes
  regenerateBackupCodes: () =>
    api.post('/mfa/backup-codes/regenerate'),
  
  getBackupCodes: () =>
    api.get('/mfa/backup-codes'),
};
```

---

## MODULE 16: SETTINGS & CONFIGURATION

### 16.1 User Settings
```typescript
// src/features/settings/services/settingsService.ts
export const settingsService = {
  // User Profile
  getProfile: () =>
    api.get('/settings/profile'),
  
  updateProfile: (data: any) =>
    api.put('/settings/profile', data),
  
  changePassword: (oldPassword: string, newPassword: string) =>
    api.post('/settings/change-password', { oldPassword, newPassword }),
  
  // Preferences
  getPreferences: () =>
    api.get('/settings/preferences'),
  
  updatePreferences: (preferences: any) =>
    api.put('/settings/preferences', preferences),
  
  // Notifications
  getNotificationSettings: () =>
    api.get('/settings/notifications'),
  
  updateNotificationSettings: (settings: any) =>
    api.put('/settings/notifications', settings'),
  
  // Avatar
  uploadAvatar: (file: File) => {
    const formData = new FormData();
    formData.append('avatar', file);
    return api.post('/settings/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
```

---

## COMPLETE API ENDPOINT REFERENCE

### Base URL
```
Development: http://localhost:8080/api/v1
Production: https://api.tradesphere.ai/api/v1
```

### Authentication Endpoints
```
POST   /auth/login
POST   /auth/register
POST   /auth/logout
POST   /auth/refresh-token
POST   /auth/forgot-password
POST   /auth/reset-password
POST   /auth/verify-email
GET    /auth/me
```

### Letter of Credit (LC) Endpoints
```
GET    /lc
POST   /lc
GET    /lc/{id}
PUT    /lc/{id}
POST   /lc/{id}/submit
POST   /lc/{id}/approve
POST   /lc/{id}/reject
POST   /lc/{id}/issue
POST   /lc/{id}/amendment
POST   /lc/{id}/amendment/{amendmentId}/approve
POST   /lc/{id}/amendment/{amendmentId}/reject
POST   /lc/{id}/documents/receive
POST   /lc/{id}/documents/accept
POST   /lc/{id}/documents/reject
POST   /lc/{id}/discrepancies/waive
POST   /lc/{id}/payment/authorize
POST   /lc/{id}/payment/complete
GET    /lc/expiring
GET    /lc/{id}/amendments
```

### Bank Guarantee Endpoints
```
GET    /bank-guarantees
POST   /bank-guarantees
GET    /bank-guarantees/{id}
PUT    /bank-guarantees/{id}
POST   /bank-guarantees/{id}/submit
POST   /bank-guarantees/{id}/approve
POST   /bank-guarantees/{id}/reject
POST   /bank-guarantees/{id}/issue
POST   /bank-guarantees/{id}/amendment
POST   /bank-guarantees/{id}/amendment/{amendmentId}/approve
POST   /bank-guarantees/{id}/claim
POST   /bank-guarantees/{id}/claim/{claimId}/approve
POST   /bank-guarantees/{id}/extend
POST   /bank-guarantees/{id}/reduce
POST   /bank-guarantees/{id}/cancel
POST   /bank-guarantees/{id}/release
GET    /bank-guarantees/expiring
GET    /bank-guarantees/{id}/claims
GET    /bank-guarantees/{id}/amendments
```

### Supply Chain Finance Endpoints
```
GET    /scf
POST   /scf
GET    /scf/{id}
POST   /scf/{id}/approve
POST   /scf/{id}/reject
POST   /scf/{id}/disburse
POST   /scf/{id}/repay
POST   /scf/{id}/accept-early-payment
POST   /scf/batch
POST   /scf/calculate-discount
GET    /scf/dashboard
GET    /scf/financing-rate/{customerId}
```

### Customer Management Endpoints
```
GET    /customers
POST   /customers
GET    /customers/{id}
GET    /customers/number/{customerNumber}
PUT    /customers/{id}
POST   /customers/{id}/submit
POST   /customers/{id}/approve
POST   /customers/{id}/reject
POST   /customers/{id}/activate
POST   /customers/{id}/suspend
POST   /customers/{id}/reactivate
POST   /customers/{id}/kyc/complete
POST   /customers/{id}/credit-limit
POST   /customers/{id}/risk-rating
GET    /customers/search
GET    /customers/by-status/{status}
GET    /customers/{id}/credit-utilization
GET    /customers/{id}/documents
POST   /customers/{id}/documents
```

### Compliance Endpoints
```
POST   /compliance/checks
GET    /compliance/checks/{id}
POST   /compliance/checks/{id}/approve
POST   /compliance/checks/{id}/reject
POST   /compliance/checks/{id}/clear-false-positive
GET    /compliance/checks/{id}/hits
POST   /compliance/hits/{id}/investigate
GET    /compliance/reports
POST   /compliance/reports/generate
GET    /compliance/reports/{id}/export
GET    /compliance/watchlists
GET    /compliance/watchlists/search
```

### Risk Management Endpoints
```
POST   /risk/assessments
GET    /risk/assessments/{id}
GET    /risk/assessments/{entityType}/{entityId}
POST   /risk/calculate-score
PUT    /risk/assessments/{id}/score
GET    /risk/high-risk-entities
GET    /risk/dashboard
POST   /risk/reports/generate
GET    /risk/reports/{id}/export
```

### Document Management Endpoints
```
POST   /documents/upload
POST   /documents/upload-multiple
GET    /documents
GET    /documents/{id}
GET    /documents/{id}/download
POST   /documents/{id}/extract
GET    /documents/{id}/extraction-status
GET    /documents/{id}/extracted-data
POST   /documents/{id}/ocr
GET    /documents/{id}/ocr-result
POST   /documents/{id}/verify
POST   /documents/{id}/approve
POST   /documents/{id}/reject
GET    /documents/search
POST   /documents/{id}/scan
GET    /documents/{id}/scan-result
```

### Analytics Endpoints
```
GET    /analytics/dashboard
GET    /analytics/transactions
GET    /analytics/compliance
GET    /analytics/risk
GET    /analytics/performance
POST   /analytics/reports
GET    /analytics/reports/{id}
GET    /analytics/reports/{type}/export
POST   /analytics/metrics
GET    /analytics/cache/stats
POST   /analytics/cache/clear
```

### Workflow Endpoints
```
POST   /workflow
GET    /workflow/{id}
GET    /workflow/entity/{entityType}/{entityId}
GET    /workflow/tasks/my-tasks
GET    /workflow/tasks/pending-approvals
POST   /workflow/{id}/tasks/approve
POST   /workflow/{id}/tasks/reject
POST   /workflow/{id}/tasks/{taskId}/reassign
GET    /workflow/monitor
GET    /workflow/sla-violations
GET    /workflow/ai-routing/recommend/{taskId}
```

### Shipment Tracking Endpoints
```
GET    /shipments
GET    /shipments/{id}
GET    /shipments/track
GET    /shipments/{id}/tracking-history
POST   /shipments/{id}/status
POST   /shipments/{id}/link
```

### AI Copilot Endpoints
```
POST   /copilot/conversations
GET    /copilot/conversations/{id}
GET    /copilot/conversations/my-conversations
POST   /copilot/conversations/{id}/messages
GET    /copilot/conversations/{id}/messages
POST   /copilot/intents/recognize
POST   /copilot/conversations/{id}/execute
```

### Knowledge Base Endpoints
```
GET    /knowledge/search
GET    /knowledge/semantic-search
GET    /knowledge/documents
GET    /knowledge/documents/{id}
POST   /knowledge/documents
POST   /knowledge/ask
```

### Security Endpoints
```
POST   /sso/initiate
POST   /sso/callback
POST   /sso/logout/{sessionId}
GET    /sso/validate/{sessionId}
GET    /sso/providers
POST   /sso/providers/{provider}/configure
POST   /mfa/setup
POST   /mfa/enable
POST   /mfa/disable
POST   /mfa/otp/generate
POST   /mfa/otp/verify
POST   /mfa/backup-codes/regenerate
GET    /mfa/backup-codes
```

### Settings Endpoints
```
GET    /settings/profile
PUT    /settings/profile
POST   /settings/change-password
GET    /settings/preferences
PUT    /settings/preferences
GET    /settings/notifications
PUT    /settings/notifications
POST   /settings/avatar
```

---

## Summary

This document provides:

✅ **Complete API Integration** - All 17 modules fully documented  
✅ **TypeScript Types** - Type-safe interfaces for all entities  
✅ **Service Layer** - Reusable API service functions  
✅ **Component Examples** - Real-world component implementations  
✅ **Best Practices** - Industry-standard patterns  
✅ **Full Endpoint Reference** - Complete API documentation  

**Implementation Priority:**
1. Authentication & Security (Module 1, 15)
2. Dashboard (Module 0)
3. Customer Management (Module 6)
4. Letter of Credit (Module 2)
5. Bank Guarantee (Module 3)
6. Remaining Modules (4-14, 16)

**End of Module Specifications Document**

