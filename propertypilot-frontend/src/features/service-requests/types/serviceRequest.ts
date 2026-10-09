export interface ServiceRequestDetailsResponse {
  serviceRequestId: string;
  propertyId: string;
  propertyTitle: string;
  customerId: string;
  customerName: string;
  customerEmail: string;
  customerMobile: string;
  requestType: string;
  serviceName?: string;
  status: string;
  priority: string;
  amount?: number;
  description?: string;
  requestedAt: string;
  scheduledAt?: string;
  completedAt?: string;
  assignedToUserId?: string;
  assignedToName?: string;
  assignedAt?: string;
  assignedByUserId?: string;
  assignedByName?: string;
  assignedAgentPhone?: string;
  assignedAgentCode?: string;
  assignedAgentId?: string;
  assignmentNotes?: string;
  activeAssignmentId?: string;
  slaDueAt?: string | null;
  slaStatus?: 'ON_TRACK' | 'BREACHED' | 'MET' | 'NO_POLICY' | string;
  statusHistory: StatusHistoryResponse[];
}

export interface ServiceRequestSummaryResponse {
  serviceRequestId: string;
  serviceId?: string;
  propertyId: string;
  propertyTitle: string;
  customerId: string;
  customerName: string;
  requestType: string;
  serviceName?: string;
  status: string;
  priority: string;
  requestedAt: string;
  assignedAgentId?: string;
  assignedAgentName?: string;
}

export interface ServiceRequestListLocationState {
  search: string;
  status: string;
  priority: string;
  service: string;
  customer: string;
  agent: string;
  fromDate: string;
  toDate: string;
  page: number;
  rowsPerPage: number;
  sortDesc: boolean;
}

export interface ServiceRequestCreateRequest {
  quoteId: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  description: string;
  preferredDate?: string;
}

export interface ServiceRequestUpdateRequest {
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  description: string;
  scheduledAt?: string;
}

export interface RequestQuoteOption {
  quoteId: string;
  customerId: string;
  customerName?: string | null;
  customerEmail?: string | null;
  propertyId: string | null;
  propertyName?: string | null;
  serviceId: string;
  serviceName: string;
  status: string;
  totalAmount: number;
  currencyCode: string;
  expiresAt: string;
}

export interface RequestCustomerOption {
  customerId: string;
  fullName: string;
  email: string;
}

export interface RequestPropertyOption {
  propertyId: string;
  customerId: string;
  title: string;
}

export interface StatusHistoryResponse {
  previousStatus?: string;
  newStatus: string;
  changeReason?: string;
  changedAt: string;
}

export interface VisitResponse {
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
}

export interface EvidenceResponse {
  evidenceId: string;
  visitId?: string;
  serviceRequestId: string;
  evidenceTypeId: string;
  evidenceTypeCode: string;
  evidenceTypeName: string;
  fileUrl: string;
  mediaType?: string;
  checksumSha256?: string;
  capturedAt?: string;
  createdAt?: string;
  latitude?: number;
  longitude?: number;
  fileName?: string;
  remarks?: string;
  deviceInfo?: string;
  validationStatus?: string;
  validationComment?: string;
  uploadedByName?: string;
  validatedByName?: string;
  validatedAt?: string;
  propertyName?: string;
  customerName?: string;
  serviceName?: string;
  agentName?: string;
  visitStatus?: string;
  visitScheduledAt?: string;
  validationHistory?: EvidenceValidationHistoryResponse[];
}

export interface EvidenceValidationHistoryResponse {
  historyId: string;
  status: string;
  comment: string;
  userName?: string;
  occurredAt: string;
}

export interface ActivityResponse {
  id: string;
  label: string;
  detail?: string;
  occurredAt: string;
  kind: 'request' | 'status' | 'visit' | 'evidence';
}

export interface AssignableAgent {
  agentId: string;
  agentCode: string;
  fullName: string;
  phone?: string;
  status: string;
  skills: string[];
  availability: 'AVAILABLE' | 'BUSY' | string;
  activeAssignmentCount: number;
  region?: string;
}

export interface AssignmentHistoryResponse {
  assignmentId: string;
  action: string;
  agentId: string;
  agentName?: string;
  agentCode?: string;
  assignedByName?: string;
  assignedByUserId?: string;
  assignmentStatus: string;
  notes?: string;
  assignedAt: string;
  respondedAt?: string;
}
