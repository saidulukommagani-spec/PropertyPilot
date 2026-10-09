import { apiClient } from '../../api/client';
import { serviceRequestApi } from '../../api/serviceRequestApi';
import { VisitApi } from '../../api/VisitApi';
import type { AdminDirectoryRow, AdminRecord, AdminResource } from './types';

type RowSource = Record<string, unknown>;
const ID_FIELDS: Record<AdminResource, readonly string[]> = {
  customers: ['customerId'], properties: ['propertyId'], requests: ['serviceRequestId'], agents: ['agentId'],
  visits: ['visitId'], evidence: ['evidenceId'], quotes: ['quoteId'], deals: ['dealId'], invoices: ['invoiceId'],
  payments: ['paymentId'], subscriptions: ['customerSubscriptionId', 'subscriptionId'], services: ['serviceId'],
  pricing: ['priceRuleId', 'ruleId', 'pricingRuleId'], plans: ['subscriptionPlanId', 'planId'], users: ['userId'],
};
const stringValue = (value: unknown): string => typeof value === 'string' || typeof value === 'number' ? String(value) : '';
const currencyValue = (value: unknown, currencyCode: unknown): string => {
  if (typeof value !== 'number') return '';
  const currency = typeof currencyCode === 'string' && /^[A-Za-z]{3}$/.test(currencyCode) ? currencyCode.toUpperCase() : 'INR';
  try { return new Intl.NumberFormat(undefined, { style: 'currency', currency }).format(value); }
  catch { return `${currency} ${value.toLocaleString()}`; }
};
const asRows = (value: unknown): RowSource[] => Array.isArray(value) ? value.filter((item): item is RowSource => Boolean(item) && typeof item === 'object') : [];
const row = (resource: AdminResource, source: RowSource): AdminDirectoryRow => {
  const id = ID_FIELDS[resource].map((field) => stringValue(source[field])).find(Boolean) ?? '';
  const customerName = stringValue(source.fullName ?? source.customerName);
  const title = stringValue(source.fullName ?? source.customerName ?? source.title ?? source.propertyTitle ?? source.requestNumber ?? source.visitNumber ?? source.fileName ?? source.documentName ?? source.invoiceNumber ?? source.quoteNumber ?? source.dealNumber ?? source.serviceName ?? source.name ?? source.agentName ?? source.evidenceTypeName ?? source.dealType)
    || `${resource.replaceAll('_', ' ').replace(/^./, (character) => character.toUpperCase())} record`;
  const status = stringValue(source.status ?? source.dealStatus ?? source.paymentStatus ?? source.listingStatus);
  const type = stringValue(source.customerType ?? source.propertyType ?? source.requestType ?? source.assignmentStatus ?? source.evidenceTypeCode ?? source.serviceName ?? source.entityType ?? source.invoiceType ?? source.paymentMethod ?? source.planName ?? source.roles);
  const date = stringValue(source.createdAt ?? source.requestedAt ?? source.scheduledAt ?? source.capturedAt ?? source.issuedAt ?? source.startDate ?? source.expiresAt ?? source.paymentDate);
  const amount = currencyValue(source.totalAmount ?? source.invoiceAmount ?? source.amount ?? source.finalAmount ?? source.expectedAmount, source.currencyCode);
  const secondary = resource === 'payments'
    ? [stringValue(source.invoiceNumber), stringValue(source.paymentMethod)].filter(Boolean).join(' / ')
    : resource === 'customers'
    ? [stringValue(source.email), stringValue(source.mobileNumber)].filter(Boolean).join(' · ')
    : stringValue(source.email ?? source.mobileNumber ?? source.propertyTitle ?? source.customerName ?? source.agentCode ?? source.serviceName ?? source.requestNumber ?? source.visitNumber ?? source.agentName ?? source.description);
  const detailPath = resource === 'requests' ? `/staff/service-requests/${id}` : resource === 'visits' ? `/staff/visits/${id}` : `/staff/${resource}/${id}`;
  const relationId = stringValue(source.customerId ?? source.propertyId ?? source.serviceRequestId ?? source.agentId ?? source.visitId);
  return { id, title, primary: customerName || title, secondary, status, type, date, amount, detailPath, relationId,
    customerId: stringValue(source.customerId), propertyId: stringValue(source.propertyId) };
};

async function listRaw(resource: AdminResource): Promise<RowSource[]> {
  switch (resource) {
    case 'customers': return asRows((await apiClient.get<unknown>('/api/v1/admin/customers')).data);
    case 'properties': return asRows((await apiClient.get<unknown>('/api/v1/properties')).data);
    case 'requests': return asRows(await serviceRequestApi.list());
    case 'agents': return asRows((await apiClient.get<unknown>('/api/v1/admin/agents')).data);
    case 'visits': return asRows((await apiClient.get<unknown>('/api/v1/visits/admin')).data);
    case 'evidence': return asRows((await apiClient.get<unknown>('/api/evidence/admin')).data);
    case 'quotes': return asRows((await apiClient.get<unknown>('/api/v1/quotes/admin')).data);
    case 'deals': return asRows((await apiClient.get<unknown>('/api/v1/deals')).data);
    case 'invoices': return asRows((await apiClient.get<unknown>('/api/v1/billing/invoices')).data);
    case 'subscriptions': return asRows((await apiClient.get<unknown>('/api/customers/admin')).data);
    case 'payments': return asRows((await apiClient.get<unknown>('/api/payments')).data);
    case 'services': return asRows((await apiClient.get<unknown>('/api/services/admin')).data);
    case 'pricing': return asRows((await apiClient.get<unknown>('/api/pricing-rules')).data);
    case 'plans': return asRows((await apiClient.get<unknown>('/api/subscription-plans')).data);
    case 'users': return asRows((await apiClient.get<unknown>('/api/v1/users')).data);
  }
}

export const adminPortalApi = {
  async list(resource: AdminResource): Promise<AdminDirectoryRow[]> {
    const records = await listRaw(resource);
    return records.map((item) => row(resource, item)).filter((item) => item.id.length > 0);
  },
  async get(resource: AdminResource, id: string): Promise<AdminRecord> {
    switch (resource) {
      case 'customers': return (await apiClient.get<AdminRecord>(`/api/v1/admin/customers/${id}`)).data;
      case 'properties': return (await apiClient.get<AdminRecord>(`/api/v1/properties/${id}`)).data;
      case 'requests': return (await serviceRequestApi.getDetails(id)) as unknown as AdminRecord;
      case 'agents': {
        const agents = asRows((await apiClient.get<unknown>('/api/v1/admin/agents')).data);
        const result = agents.find((item) => stringValue(item.agentId) === id);
        if (!result) throw new Error('Agent not found.');
        return result;
      }
      case 'visits': return (await VisitApi.getDetails(id)) as unknown as AdminRecord;
      case 'evidence': return (await apiClient.get<AdminRecord>(`/api/evidence/${id}`)).data;
      case 'quotes': return (await apiClient.get<AdminRecord>(`/api/v1/quotes/${id}`)).data;
      case 'deals': return (await apiClient.get<AdminRecord>(`/api/v1/deals/${id}`)).data;
      case 'invoices': return (await apiClient.get<AdminRecord>(`/api/v1/billing/invoices/${id}`)).data;
      case 'payments': return (await apiClient.get<AdminRecord>(`/api/payments/${id}`)).data;
      case 'subscriptions': return (await apiClient.get<AdminRecord>(`/api/customers/subscriptions/${id}`)).data;
      case 'services': {
        const services = asRows((await apiClient.get<unknown>('/api/services/admin')).data);
        const service = services.find((item) => stringValue(item.serviceId) === id);
        if (!service) throw new Error('Service not found.');
        return service;
      }
      case 'pricing': return (await apiClient.get<AdminRecord>(`/api/pricing-rules/${id}`)).data;
      case 'plans': return (await apiClient.get<AdminRecord>(`/api/subscription-plans/${id}`)).data;
      case 'users': return (await apiClient.get<AdminRecord>(`/api/v1/users/${id}`)).data;
    }
  },
};

export const directoryLabels: Record<AdminResource, { label: string; description: string }> = {
  customers: { label: 'Customers', description: 'Customer relationships, account status, and contact information.' },
  properties: { label: 'Properties', description: 'Properties in the PropertyPilot service portfolio.' },
  requests: { label: 'Service Requests', description: 'Customer work and service delivery status.' },
  agents: { label: 'Agents', description: 'Operational agents and their active status.' },
  visits: { label: 'Visits', description: 'Scheduled field work across service requests.' },
  evidence: { label: 'Evidence', description: 'Evidence items submitted for service delivery.' },
  quotes: { label: 'Quotes and Estimates', description: 'Customer estimates, line items, and approval status.' },
  deals: { label: 'Deals', description: 'Deal workflow and transaction status.' },
  invoices: { label: 'Invoices', description: 'Customer billing and invoice status.' },
  payments: { label: 'Payments', description: 'Payments recorded against customer invoices.' },
  subscriptions: { label: 'Subscriptions', description: 'Customer subscription status and term.' },
  services: { label: 'Service Catalog', description: 'Services available for PropertyPilot delivery.' },
  pricing: { label: 'Pricing Rules', description: 'Configured service pricing rules.' },
  plans: { label: 'Subscription Plans', description: 'Plans offered and maintained by PropertyPilot.' },
  users: { label: 'Users and Roles', description: 'User accounts and assigned access roles.' },
};
