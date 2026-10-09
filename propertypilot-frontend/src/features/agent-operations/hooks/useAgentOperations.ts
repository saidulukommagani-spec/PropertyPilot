import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { AgentOperationsApi } from '../services/AgentOperationsApi';

export const agentOperationsKeys = {
  profile: ['agent-operations', 'profile'] as const,
  myVisits: ['visits', 'mine'] as const,
  allVisits: ['visits', 'all'] as const,
  agents: ['agent-operations', 'agents'] as const,
  activeAgents: ['assignable-agents'] as const,
  requests: ['service-requests'] as const,
  evidence: ['evidence', 'all'] as const,
};

export function useAgentProfile() { return useQuery({ queryKey: agentOperationsKeys.profile, queryFn: AgentOperationsApi.myProfile }); }
export function useAgentVisits() { return useQuery({ queryKey: agentOperationsKeys.myVisits, queryFn: AgentOperationsApi.myVisits }); }
export function useAgentRequests() { return useQuery({ queryKey: ['agent-operations', 'my-requests'], queryFn: AgentOperationsApi.myRequests }); }
export function useOperationsVisits() { return useQuery({ queryKey: agentOperationsKeys.allVisits, queryFn: AgentOperationsApi.allVisits }); }
export function useOperationsAgents() { return useQuery({ queryKey: agentOperationsKeys.agents, queryFn: AgentOperationsApi.agents }); }
export function useAgentAssignmentsData() {
  return {
    agents: useQuery({ queryKey: agentOperationsKeys.activeAgents, queryFn: AgentOperationsApi.activeAgents }),
    requests: useQuery({ queryKey: agentOperationsKeys.requests, queryFn: AgentOperationsApi.requests }),
  };
}
export function useOperationsEvidence() { return useQuery({ queryKey: agentOperationsKeys.evidence, queryFn: AgentOperationsApi.evidence }); }
export function useOperationsSla() { return useQuery({ queryKey: ['agent-operations', 'sla-report'], queryFn: AgentOperationsApi.slaReport }); }
export function useAgentAssignment() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ requestId, agentId, notes }: { requestId: string; agentId: string; notes: string }) => AgentOperationsApi.assign(requestId, agentId, notes),
    onSuccess: async () => {
      await Promise.all([
        client.invalidateQueries({ queryKey: agentOperationsKeys.requests }),
        client.invalidateQueries({ queryKey: agentOperationsKeys.agents }),
        client.invalidateQueries({ queryKey: agentOperationsKeys.activeAgents }),
        client.invalidateQueries({ queryKey: ['service-request-detail'] }),
        client.invalidateQueries({ queryKey: ['service-request-assignment-history'] }),
      ]);
    },
  });
}
