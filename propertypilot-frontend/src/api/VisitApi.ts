import { apiClient } from './client';
import type { CreateVisitRequest, GpsVerificationRequest, GpsVerificationResponse, RescheduleVisitRequest, VisitServiceOption, VisitSummary } from '../features/visits/types/visit';
import type { AssignmentHistoryResponse, EvidenceResponse, ServiceRequestDetailsResponse } from '../features/service-requests/types/serviceRequest';

export const VisitApi = {
  async listServices(): Promise<VisitServiceOption[]> {
    return (await apiClient.get<VisitServiceOption[]>('/api/services')).data;
  },
  async listMine(): Promise<VisitSummary[]> {
    return (await apiClient.get<VisitSummary[]>('/api/v1/visits/mine')).data;
  },
  async listAll(): Promise<VisitSummary[]> {
    return (await apiClient.get<VisitSummary[]>('/api/v1/visits/admin')).data;
  },
  async listForRequest(serviceRequestId: string): Promise<VisitSummary[]> {
    const response = await apiClient.get<VisitSummary[]>(`/api/v1/visits/service-request/${serviceRequestId}`);
    return response.data;
  },
  async getDetails(visitId: string): Promise<VisitSummary> {
    const response = await apiClient.get<VisitSummary>(`/api/v1/visits/${visitId}`);
    return response.data;
  },
  async create(request: CreateVisitRequest): Promise<VisitSummary> {
    const response = await apiClient.post<VisitSummary>('/api/v1/visits', request);
    return response.data;
  },
  async start(visitId: string): Promise<VisitSummary> {
    const response = await apiClient.post<VisitSummary>(`/api/v1/visits/${visitId}/start`);
    return response.data;
  },
  async complete(visitId: string): Promise<VisitSummary> {
    const response = await apiClient.post<VisitSummary>(`/api/v1/visits/${visitId}/complete`);
    return response.data;
  },
  async cancel(visitId: string, reason: string): Promise<VisitSummary> {
    return (await apiClient.post<VisitSummary>(`/api/v1/visits/${visitId}/cancel`, undefined, { params: { reason } })).data;
  },
  async reschedule(visitId: string, request: RescheduleVisitRequest): Promise<VisitSummary> {
    return (await apiClient.put<VisitSummary>(`/api/v1/visits/${visitId}/reschedule`, request)).data;
  },
  async verifyGps(request: GpsVerificationRequest): Promise<GpsVerificationResponse> {
    return (await apiClient.post<GpsVerificationResponse>('/api/gps-verifications', request)).data;
  },
  async getRequest(serviceRequestId: string): Promise<ServiceRequestDetailsResponse> {
    const response = await apiClient.get<ServiceRequestDetailsResponse>(`/api/service-requests/admin/${serviceRequestId}`);
    return response.data;
  },
  async getEvidence(serviceRequestId: string): Promise<EvidenceResponse[]> {
    const response = await apiClient.get<EvidenceResponse[]>(`/api/evidence/service-request/${serviceRequestId}`);
    return response.data;
  },
  async getEvidenceForVisit(visitId: string): Promise<EvidenceResponse[]> {
    return (await apiClient.get<EvidenceResponse[]>(`/api/evidence/visit/${visitId}`)).data;
  },
  async getAssignmentHistory(serviceRequestId: string): Promise<AssignmentHistoryResponse[]> {
    const response = await apiClient.get<AssignmentHistoryResponse[]>(`/api/v1/service-requests/${serviceRequestId}/assignment-history`);
    return response.data;
  },
};
