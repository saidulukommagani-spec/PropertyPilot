import { apiClient } from '../../../api/client';
import type { AgentProfile, AgentVisit, AgentRequest, AgentEvidence } from '../types/agentOperations';
import type { AssignableAgent } from '../../service-requests/types/serviceRequest';

export interface AgentSlaReport { evaluatedRequests: number; atRiskRequests: number; breachedRequests: number; requests: Array<{ serviceRequestId: string; slaState: string }> }

export const AgentOperationsApi = {
  async myProfile(): Promise<AgentProfile> { return (await apiClient.get<AgentProfile>('/api/v1/agents/profile')).data; },
  async myVisits(): Promise<AgentVisit[]> { return (await apiClient.get<AgentVisit[]>('/api/v1/visits/mine')).data; },
  async myRequests(): Promise<AgentRequest[]> { return (await apiClient.get<AgentRequest[]>('/api/service-requests/mine')).data; },
  async allVisits(): Promise<AgentVisit[]> { return (await apiClient.get<AgentVisit[]>('/api/v1/visits/admin')).data; },
  async agents(): Promise<AgentProfile[]> { return (await apiClient.get<AgentProfile[]>('/api/v1/admin/agents')).data; },
  async activeAgents(): Promise<AssignableAgent[]> { return (await apiClient.get<AssignableAgent[]>('/api/v1/agents')).data; },
  async requests(): Promise<AgentRequest[]> { return (await apiClient.get<AgentRequest[]>('/api/service-requests/admin')).data; },
  async evidence(): Promise<AgentEvidence[]> { return (await apiClient.get<AgentEvidence[]>('/api/evidence/admin')).data; },
  async slaReport(): Promise<AgentSlaReport> { return (await apiClient.get<AgentSlaReport>('/api/admin/operations/sla')).data; },
  async visitEvidenceSummary(visitId: string): Promise<{ completionPercentage: number; missingEvidenceTypes: string[] }> {
    return (await apiClient.get(`/api/evidence/visit/${visitId}/summary`)).data;
  },
  async assign(requestId: string, agentId: string, notes: string): Promise<void> {
    await apiClient.post(`/api/v1/service-requests/${requestId}/assign`, { agentId, notes });
  },
};
