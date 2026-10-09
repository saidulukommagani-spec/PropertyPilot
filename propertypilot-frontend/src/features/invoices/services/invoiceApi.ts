import { apiClient } from '../../../api/client';
import type { DealOption, InvoiceResponse, PaymentResponse, RecordPaymentRequest, RevenueDashboard } from '../types/invoice';

export const invoiceApi = {
  async list(): Promise<InvoiceResponse[]> { return (await apiClient.get<InvoiceResponse[]>('/api/v1/billing/invoices')).data; },
  async get(id: string): Promise<InvoiceResponse> { return (await apiClient.get<InvoiceResponse>(`/api/v1/billing/invoices/${id}`)).data; },
  async send(id: string): Promise<InvoiceResponse> { return (await apiClient.post<InvoiceResponse>(`/api/v1/billing/invoices/${id}/send`)).data; },
  async cancel(id: string): Promise<InvoiceResponse> { return (await apiClient.post<InvoiceResponse>(`/api/v1/billing/invoices/${id}/cancel`)).data; },
  async listForEntity(type: string, id: string): Promise<InvoiceResponse[]> { return (await apiClient.get<InvoiceResponse[]>(`/api/v1/billing/invoices/entity/${type}/${id}`)).data; },
  async payments(id: string): Promise<PaymentResponse[]> { return (await apiClient.get<PaymentResponse[]>(`/api/payments/invoice/${id}`)).data; },
  async recordPayment(id: string, request: RecordPaymentRequest): Promise<PaymentResponse> { return (await apiClient.post<PaymentResponse>(`/api/payments/invoice/${id}`, request)).data; },
  async revenue(): Promise<RevenueDashboard> { return (await apiClient.get<RevenueDashboard>('/api/admin/revenue')).data; },
  async deals(): Promise<DealOption[]> { return (await apiClient.get<DealOption[]>('/api/v1/deals')).data; },
  async completeDeal(id: string, finalDealAmount: number): Promise<DealOption> { return (await apiClient.put<DealOption>(`/api/v1/deals/${id}/complete`, { finalDealAmount })).data; },
};
