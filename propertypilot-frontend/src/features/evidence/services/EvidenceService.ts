import { apiClient } from '../../../api/client';
import type { EvidenceResponse } from '../../service-requests/types/serviceRequest';
import type { EvidenceDashboardResponse, EvidenceDetail, EvidenceTypeResponse, ReviewEvidenceRequest, UploadEvidenceRequest, VisitEvidenceSummaryResponse } from '../types/evidence';

export const EvidenceService = {
  async list(): Promise<EvidenceResponse[]> {
    return (await apiClient.get<EvidenceResponse[]>('/api/evidence/admin')).data;
  },
  async get(evidenceId: string): Promise<EvidenceDetail> {
    return (await apiClient.get<EvidenceDetail>(`/api/evidence/${evidenceId}`)).data;
  },
  async getTypes(): Promise<EvidenceTypeResponse[]> {
    return (await apiClient.get<EvidenceTypeResponse[]>('/api/evidence/types')).data;
  },
  async getDashboard(): Promise<EvidenceDashboardResponse> {
    return (await apiClient.get<EvidenceDashboardResponse>('/api/evidence/dashboard')).data;
  },
  async getValidationQueue(): Promise<EvidenceResponse[]> {
    return (await apiClient.get<EvidenceResponse[]>('/api/evidence/validation')).data;
  },
  async getForVisit(visitId: string): Promise<EvidenceResponse[]> {
    return (await apiClient.get<EvidenceResponse[]>(`/api/evidence/visit/${visitId}`)).data;
  },
  async getVisitSummary(visitId: string): Promise<VisitEvidenceSummaryResponse> {
    return (await apiClient.get<VisitEvidenceSummaryResponse>(`/api/evidence/visit/${visitId}/summary`)).data;
  },
  async upload(request: UploadEvidenceRequest, onProgress?: (percentage: number) => void): Promise<EvidenceResponse> {
    const body = new FormData();
    if (request.visitId) body.append('visitId', request.visitId);
    if (request.serviceRequestId) body.append('serviceRequestId', request.serviceRequestId);
    body.append('evidenceTypeId', request.evidenceTypeId);
    body.append('file', request.file);
    body.append('capturedAt', request.capturedAt);
    body.append('deviceInfo', request.deviceInfo);
    if (request.latitude !== undefined) body.append('latitude', String(request.latitude));
    if (request.longitude !== undefined) body.append('longitude', String(request.longitude));
    if (request.remarks) body.append('remarks', request.remarks);
    return (await apiClient.post<EvidenceResponse>('/api/evidence/upload', body, {
      onUploadProgress: (event) => { if (event.total) onProgress?.(Math.round(event.loaded * 100 / event.total)); },
    })).data;
  },
  async review(evidenceId: string, request: ReviewEvidenceRequest): Promise<EvidenceResponse> {
    return (await apiClient.post<EvidenceResponse>(`/api/evidence/${evidenceId}/validation`, request)).data;
  },
};
