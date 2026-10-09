import type { EvidenceResponse, ServiceRequestDetailsResponse } from '../../service-requests/types/serviceRequest';

export interface VisitSummary {
  visitId: string;
  serviceRequestId: string;
  assignmentId?: string;
  agentId?: string;
  agentName?: string;
  status: string;
  scheduledAt: string;
  startedAt?: string;
  endedAt?: string;
  notes?: string;
  cancellationReason?: string;
  createdAt?: string;
  createdByName?: string;
  startedByName?: string;
  completedByName?: string;
  rescheduleReason?: string;
  updatedAt?: string;
  updatedByName?: string;
  propertyName?: string;
  customerName?: string;
  serviceName?: string;
  assignedAt?: string;
  assignedByName?: string;
  acceptedAt?: string;
  propertyId?: string;
  customerId?: string;
}

export type VisitDetails = VisitSummary & {
  request?: ServiceRequestDetailsResponse;
  evidence: EvidenceResponse[];
  visitNumber: number;
};

export interface CreateVisitRequest {
  assignmentId: string;
  scheduledAt: string;
  notes?: string;
}

export interface RescheduleVisitRequest {
  scheduledAt: string;
  notes?: string;
  reason: string;
}

export interface VisitAssignmentOption {
  assignmentId: string;
  agentId: string;
  agentName: string;
  agentCode?: string;
  assignmentStatus: string;
}

export interface VisitServiceOption {
  serviceId: string;
  serviceName: string;
  requiresVisit: boolean | null;
  visitMandatory: boolean | null;
  remote: boolean | null;
}

export interface GpsVerificationRequest {
  visitId: string;
  latitude: number;
  longitude: number;
}

export interface GpsVerificationResponse {
  gpsVerificationId: string;
  visitId: string;
  validationStatus: 'VERIFIED' | 'REJECTED' | string;
  latitude: number;
  longitude: number;
  distanceFromExpectedKm: number;
  capturedAt: string;
  validatedAt: string;
}
