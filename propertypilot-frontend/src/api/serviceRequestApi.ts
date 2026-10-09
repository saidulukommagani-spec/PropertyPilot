import { apiClient } from './client';
import type { AssignableAgent, AssignmentHistoryResponse, EvidenceResponse, RequestCustomerOption, RequestPropertyOption, RequestQuoteOption, ServiceRequestCreateRequest, ServiceRequestDetailsResponse, ServiceRequestSummaryResponse, ServiceRequestUpdateRequest, VisitResponse } from '../features/service-requests/types/serviceRequest';

interface ServiceRequestMutationResponse { serviceRequestId: string }

export const serviceRequestApi = {
  async list() {
    return (await apiClient.get<ServiceRequestSummaryResponse[]>('/api/service-requests/admin')).data;
  },
  async getDetails(id: string) {
    return (await apiClient.get<ServiceRequestDetailsResponse>(`/api/service-requests/admin/${id}`)).data;
  },
  async getAssignedDetails(id: string) {
    return (await apiClient.get<ServiceRequestDetailsResponse>(`/api/service-requests/${id}`)).data;
  },
  async listQuotes() {
    return (await apiClient.get<RequestQuoteOption[]>('/api/v1/quotes/admin')).data;
  },
  async listCustomers() {
    return (await apiClient.get<RequestCustomerOption[]>('/api/v1/admin/customers')).data;
  },
  async listProperties() {
    return (await apiClient.get<RequestPropertyOption[]>('/api/v1/properties')).data;
  },
  async createRequest(request: ServiceRequestCreateRequest) {
    return (await apiClient.post<{ serviceRequestId: string }>('/api/service-requests/admin', request)).data;
  },
  async updateRequest(id: string, request: ServiceRequestUpdateRequest) {
    return (await apiClient.put<{ serviceRequestId: string }>(`/api/service-requests/${id}`, request)).data;
  },
  async getVisits(id: string) {
    return (await apiClient.get<VisitResponse[]>(`/api/v1/visits/service-request/${id}`)).data;
  },
  async getEvidence(id: string) {
    return (await apiClient.get<EvidenceResponse[]>(`/api/evidence/service-request/${id}`)).data;
  },
  async getAssignableAgents() {
    return (await apiClient.get<AssignableAgent[]>('/api/v1/agents')).data;
  },
  async assignAgent(id: string, agentId: string, notes: string) {
    return (await apiClient.post<ServiceRequestMutationResponse>(`/api/v1/service-requests/${id}/assign`, { agentId, notes })).data;
  },
  async getAssignmentHistory(id: string) {
    return (await apiClient.get<AssignmentHistoryResponse[]>(`/api/v1/service-requests/${id}/assignment-history`)).data;
  },
  async acceptRequest(id: string) {
    return (await apiClient.post<ServiceRequestMutationResponse>(`/api/service-requests/${id}/accept`, { changeReason: null })).data;
  },
  async startRequest(id: string) {
    return (await apiClient.post<ServiceRequestMutationResponse>(`/api/service-requests/${id}/start`, { changeReason: null })).data;
  },
  async completeRequest(id: string) {
    return (await apiClient.post<ServiceRequestMutationResponse>(`/api/service-requests/${id}/complete`, { changeReason: null })).data;
  },
  async cancelRequest(id: string, reason: string) {
    return (await apiClient.post<ServiceRequestMutationResponse>(`/api/service-requests/${id}/cancel`, { changeReason: reason })).data;
  },
};
