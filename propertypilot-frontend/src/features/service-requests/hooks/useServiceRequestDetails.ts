import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';

export const serviceRequestDetailKeys = {
  detail: (id: string, audience?: 'operations' | 'agent') => audience ? ['service-request-detail', id, audience] as const : ['service-request-detail', id] as const,
  visits: (id: string) => ['service-request-visits', id] as const,
  evidence: (id: string) => ['service-request-evidence', id] as const,
  agents: ['assignable-agents'] as const,
  assignmentHistory: (id: string) => ['service-request-assignment-history', id] as const,
};

export function useServiceRequestDetails(id: string, isAgent = false) {
  const enabled = Boolean(id);
  const audience = isAgent ? 'agent' : 'operations';
  return {
    request: useQuery({ queryKey: serviceRequestDetailKeys.detail(id, audience), queryFn: () => isAgent ? serviceRequestApi.getAssignedDetails(id) : serviceRequestApi.getDetails(id), enabled }),
    visits: useQuery({ queryKey: serviceRequestDetailKeys.visits(id), queryFn: () => serviceRequestApi.getVisits(id), enabled }),
    evidence: useQuery({ queryKey: serviceRequestDetailKeys.evidence(id), queryFn: () => serviceRequestApi.getEvidence(id), enabled }),
    agents: useQuery({ queryKey: serviceRequestDetailKeys.agents, queryFn: serviceRequestApi.getAssignableAgents, staleTime: 60_000, enabled: enabled && !isAgent }),
    assignmentHistory: useQuery({ queryKey: serviceRequestDetailKeys.assignmentHistory(id), queryFn: () => serviceRequestApi.getAssignmentHistory(id), enabled: enabled && !isAgent }),
  };
}

export function useAssignRequestAgent() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ id, agentId, notes }: { id: string; agentId: string; notes: string }) => serviceRequestApi.assignAgent(id, agentId, notes),
    onSuccess: (request) => {
      void client.invalidateQueries({ queryKey: serviceRequestDetailKeys.detail(request.serviceRequestId) });
      void client.invalidateQueries({ queryKey: serviceRequestDetailKeys.assignmentHistory(request.serviceRequestId) });
      void client.invalidateQueries({ queryKey: ['service-requests'] });
    },
  });
}

export function useRequestWorkflowAction() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ id, action }: { id: string; action: 'accept' | 'start' | 'complete' }) => {
      if (action === 'accept') return serviceRequestApi.acceptRequest(id);
      if (action === 'start') return serviceRequestApi.startRequest(id);
      return serviceRequestApi.completeRequest(id);
    },
    onSuccess: (request) => {
      void client.invalidateQueries({ queryKey: serviceRequestDetailKeys.detail(request.serviceRequestId) });
      void client.invalidateQueries({ queryKey: ['service-requests'] });
    },
  });
}

export function useCancelServiceRequest() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ id, reason }: { id: string; reason: string }) => serviceRequestApi.cancelRequest(id, reason),
    onSuccess: async (request) => {
      await Promise.all([
        client.invalidateQueries({ queryKey: serviceRequestDetailKeys.detail(request.serviceRequestId) }),
        client.invalidateQueries({ queryKey: ['service-requests'] }),
      ]);
    },
  });
}
