import { apiClient } from '../../../api/client';
import type { AddDealParticipantRequest, Deal, DealCreateRequest, DealParticipant, DealStatusRequest, CustomerOption, PropertyOption } from '../types/deal';

export const dealApi = {
  async list(): Promise<Deal[]> { return (await apiClient.get<Deal[]>('/api/v1/deals')).data; },
  async get(id: string): Promise<Deal> { return (await apiClient.get<Deal>(`/api/v1/deals/${id}`)).data; },
  async create(request: DealCreateRequest): Promise<Deal> { return (await apiClient.post<Deal>('/api/v1/deals', request)).data; },
  async updateStatus(id: string, request: DealStatusRequest): Promise<Deal> { return (await apiClient.put<Deal>(`/api/v1/deals/${id}/status`, request)).data; },
  async cancel(id: string, reason: string): Promise<Deal> { return (await apiClient.post<Deal>(`/api/v1/deals/${id}/cancel`, null, { params: { reason } })).data; },
  async finalize(id: string, finalDealAmount: number, remarks: string): Promise<Deal> { return (await apiClient.post<Deal>(`/api/v1/deals/${id}/finalize`, { finalDealAmount, remarks })).data; },
  async participants(id: string): Promise<DealParticipant[]> { return (await apiClient.get<DealParticipant[]>(`/api/v1/deals/${id}/participants`)).data; },
  async addParticipant(id: string, request: AddDealParticipantRequest): Promise<DealParticipant> { return (await apiClient.post<DealParticipant>(`/api/v1/deals/${id}/participants`, request)).data; },
  async selectBuyer(id: string, participantId: string): Promise<DealParticipant> { return (await apiClient.post<DealParticipant>(`/api/v1/deals/${id}/participants/${participantId}/select`)).data; },
  async customers(): Promise<CustomerOption[]> { return (await apiClient.get<CustomerOption[]>('/api/v1/admin/customers')).data; },
  async properties(): Promise<PropertyOption[]> { return (await apiClient.get<PropertyOption[]>('/api/v1/properties')).data; },
};
