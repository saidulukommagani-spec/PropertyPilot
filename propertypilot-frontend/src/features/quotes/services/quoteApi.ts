import { apiClient } from '../../../api/client';
import type { PricingEstimateRequest, PricingEstimateResponse, QuoteCustomer, QuoteProperty, QuoteResponse, QuoteServiceOption } from '../types/quote';

export const quoteApi = {
  async list(operations: boolean): Promise<QuoteResponse[]> { return (await apiClient.get<QuoteResponse[]>(operations ? '/api/v1/quotes/admin' : '/api/v1/quotes')).data; },
  async get(id: string): Promise<QuoteResponse> { return (await apiClient.get<QuoteResponse>(`/api/v1/quotes/${id}`)).data; },
  async accept(id: string): Promise<QuoteResponse> { return (await apiClient.post<QuoteResponse>(`/api/v1/quotes/${id}/accept`)).data; },
  async reject(id: string, reason: string): Promise<QuoteResponse> { return (await apiClient.post<QuoteResponse>(`/api/v1/quotes/${id}/reject`, { reason })).data; },
  async calculate(request: PricingEstimateRequest): Promise<PricingEstimateResponse> { return (await apiClient.post<PricingEstimateResponse>('/api/v1/pricing/calculate', request)).data; },
  async customers(): Promise<QuoteCustomer[]> { return (await apiClient.get<QuoteCustomer[]>('/api/v1/admin/customers')).data; },
  async properties(): Promise<QuoteProperty[]> { return (await apiClient.get<QuoteProperty[]>('/api/v1/properties')).data; },
  async services(): Promise<QuoteServiceOption[]> { return (await apiClient.get<QuoteServiceOption[]>('/api/services')).data; },
};
