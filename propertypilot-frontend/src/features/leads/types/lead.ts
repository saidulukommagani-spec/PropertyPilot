export type LeadStatus = 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'READY_FOR_DEAL' | 'DEAL_CONVERTED' | 'CONVERTED' | 'LOST' | 'CLOSED';

export type OperationsLeadSortField = 'createdAt' | 'status' | 'source' | 'location' | 'propertyType';
export type LeadSortDirection = 'ASC' | 'DESC';

export interface OperationsLeadSearchParams {
  search?: string;
  status?: LeadStatus;
  page: number;
  size: number;
  sort: OperationsLeadSortField;
  direction: LeadSortDirection;
}

export interface OperationsLeadSummary {
  leadId: string;
  source: string | null;
  propertyType: string | null;
  location: string | null;
  status: LeadStatus | null;
  customerName: string | null;
  customerEmail: string | null;
  customerPhone: string | null;
  assignedAgentCode: string | null;
  createdAt: string | null;
}

export interface OperationsLeadPage {
  content: OperationsLeadSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface Lead {
  leadId: string;
  source: string;
  propertyType: string | null;
  location: string | null;
  status: LeadStatus;
  budgetMin: number | null;
  budgetMax: number | null;
  notes: string | null;
  contactedAt: string | null;
  convertedAt: string | null;
  assignedAgentId: string | null;
  assignedAgentCode: string | null;
  customerId: string | null;
}

export interface LeadDealConversionRequest {
  propertyId?: string;
  dealType: 'PROPERTY_SALE' | 'PROPERTY_PURCHASE' | 'PROPERTY_RENTAL' | 'PROPERTY_LEASE';
  expectedAmount: number;
  sellerCommissionPercent?: number;
  buyerCommissionPercent?: number;
  remarks?: string;
  duplicateOpportunityReason?: string;
}

export interface LeadLinkedDeal {
  dealId: string;
  propertyId: string | null;
  sellerCustomerId: string;
  leadId: string;
  dealType: string;
  dealStatus: string;
  expectedAmount: number;
  createdAt: string;
  remarks: string | null;
}

export interface CreateLeadRequest {
  source: string;
  budgetMin?: number;
  budgetMax?: number;
  notes?: string;
}

export interface LeadAssignmentRequest { agentId: string }
export interface LeadAssignmentResponse { leadId: string; agentId: string; agentCode: string; status: string }
export interface UpdateLeadStatusRequest { status: LeadStatus }

export interface LeadServiceRequest {
  serviceRequestId: string;
  leadId: string;
  propertyId: string | null;
  serviceId: string | null;
  quoteId: string | null;
  serviceName: string;
  status: string;
  priority: string;
  assignedAgent: string | null;
  requestedAt: string;
  scheduledAt: string | null;
  completedAt: string | null;
  slaDueAt: string | null;
  slaStatus: 'ON_TRACK' | 'BREACHED' | 'MET' | 'NO_POLICY' | string;
  visitCount: number;
  evidenceCount: number;
  requestValue: number | null;
  currencyCode: string | null;
  revenueByCurrency: LeadServiceRequestRevenueSummary[];
  statusHistory: LeadServiceRequestStatusHistory[];
  assignmentHistory: LeadServiceRequestAssignmentHistory[];
}

export interface LeadServiceRequestStatusHistory { previousStatus: string | null; newStatus: string; changeReason: string | null; changedAt: string }
export interface LeadServiceRequestAssignmentHistory { assignmentId: string; action: string; agentId: string | null; agentName: string | null; agentCode: string | null; assignedByName: string | null; assignedByUserId: string | null; assignmentStatus: string; notes: string | null; assignedAt: string; respondedAt: string | null }
export interface LeadServiceRequestRevenueSummary { currencyCode: string; invoicedRevenue: number; collectedRevenue: number }

export interface LeadActivity {
  leadActivityId: string;
  leadId: string;
  activityType: string;
  subject: string;
  details: string | null;
  outcome: string | null;
  performedBy: string | null;
  occurredAt: string;
  nextActionAt: string | null;
  visibility: 'INTERNAL' | 'CUSTOMER_VISIBLE';
}
