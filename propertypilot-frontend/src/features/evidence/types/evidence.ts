import type { EvidenceResponse, EvidenceValidationHistoryResponse } from '../../service-requests/types/serviceRequest';

export interface EvidenceTypeResponse {
  evidenceTypeId: string;
  code: string;
  name: string;
  category: string;
  description?: string;
}

export interface VisitEvidenceSummaryResponse {
  visitId: string;
  requiredEvidenceTypes: string[];
  uploadedEvidence: EvidenceResponse[];
  missingEvidenceTypes: string[];
  completionPercentage: number;
}

export interface EvidenceDashboardResponse {
  totalEvidence: number;
  pendingValidation: number;
  approved: number;
  rejected: number;
  missingEvidence: number;
  todaysUploads: number;
  byType: Record<string,number>;
  byService: Record<string,number>;
}

export type UploadEvidenceRequest = {
  evidenceTypeId: string;
  file: File;
  capturedAt: string;
  deviceInfo: string;
  latitude?: number;
  longitude?: number;
  remarks?: string;
} & ({ visitId: string; serviceRequestId?: never } | { serviceRequestId: string; visitId?: never });

export interface ReviewEvidenceRequest {
  status: 'APPROVED' | 'REJECTED' | 'REUPLOAD_REQUESTED';
  comment: string;
}

export type EvidenceDetail = EvidenceResponse & {
  validationHistory: EvidenceValidationHistoryResponse[];
  visitStatus?: string;
  visitScheduledAt?: string;
  agentName?: string;
};
