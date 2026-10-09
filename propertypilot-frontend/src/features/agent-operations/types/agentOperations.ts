import type { AssignableAgent, ServiceRequestSummaryResponse, EvidenceResponse } from '../../service-requests/types/serviceRequest';
import type { VisitSummary } from '../../visits/types/visit';

export type AgentProfile = AssignableAgent & { region?: string };
export type AgentVisit = VisitSummary;
export type AgentRequest = ServiceRequestSummaryResponse & { slaStatus?: string };
export type AgentEvidence = EvidenceResponse;
