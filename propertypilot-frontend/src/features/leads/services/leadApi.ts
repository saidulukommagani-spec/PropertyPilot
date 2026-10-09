import { apiClient } from '../../../api/client';
import type { CreateLeadRequest, Lead, LeadActivity, LeadAssignmentRequest, LeadAssignmentResponse, LeadDealConversionRequest, LeadLinkedDeal, LeadServiceRequest, LeadStatus, OperationsLeadPage, OperationsLeadSearchParams } from '../types/lead';
import type { CustomerRequestCreated, CreateCustomerRequest } from '../../customer-portal/types/customerPortal';

export const leadApi = {
  async searchOperationsLeads(params: OperationsLeadSearchParams): Promise<OperationsLeadPage> {
    const { data } = await apiClient.get<OperationsLeadPage>('/api/v1/leads', {
      params: {
        ...(params.search ? { search: params.search } : {}),
        ...(params.status ? { status: params.status } : {}),
        page: params.page,
        size: params.size,
        sort: params.sort,
        direction: params.direction,
      },
    });
    return data;
  },
  async mine(): Promise<Lead[]> {
    return (await apiClient.get<Lead[]>('/api/v1/leads/my')).data;
  },
  async get(leadId: string): Promise<Lead> {
    return (await apiClient.get<Lead>(`/api/v1/leads/${leadId}`)).data;
  },
  async create(request: CreateLeadRequest): Promise<Lead> {
    return (await apiClient.post<Lead>('/api/v1/leads', request)).data;
  },
  async updateStatus(leadId: string, status: LeadStatus): Promise<Lead> {
    return (await apiClient.patch<Lead>(`/api/v1/leads/${leadId}/status`, null, { params: { status } })).data;
  },
  async assign(leadId: string, request: LeadAssignmentRequest): Promise<LeadAssignmentResponse> {
    return (await apiClient.patch<LeadAssignmentResponse>(`/api/v1/leads/${leadId}/assign`, request)).data;
  },
  async serviceRequests(leadId: string): Promise<LeadServiceRequest[]> {
    return (await apiClient.get<LeadServiceRequest[]>(`/api/v1/leads/${leadId}/service-requests`)).data;
  },
  async activities(leadId: string): Promise<LeadActivity[]> {
    return (await apiClient.get<LeadActivity[]>(`/api/v1/leads/${leadId}/activities`)).data;
  },
  async deals(leadId: string): Promise<LeadLinkedDeal[]> {
    return (await apiClient.get<LeadLinkedDeal[]>(`/api/v1/leads/${leadId}/deals`)).data;
  },
  async convertToDeal(leadId: string, request: LeadDealConversionRequest): Promise<LeadLinkedDeal> {
    return (await apiClient.post<LeadLinkedDeal>(`/api/v1/leads/${leadId}/convert-to-deal`, request)).data;
  },
  async createServiceRequest(request: CreateCustomerRequest, operations = false): Promise<CustomerRequestCreated> {
    const endpoint = operations ? '/api/service-requests/admin' : '/api/service-requests';
    return (await apiClient.post<CustomerRequestCreated>(endpoint, request)).data;
  },
};
