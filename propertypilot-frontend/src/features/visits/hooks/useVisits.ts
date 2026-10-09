import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { VisitApi } from '../../../api/VisitApi';
import type { CreateVisitRequest, GpsVerificationRequest, RescheduleVisitRequest } from '../types/visit';

export const visitKeys = {
  all: ['visits', 'all'] as const,
  forRequest: (id: string) => ['visits', 'request', id] as const,
  detail: (id: string) => ['visits', 'detail', id] as const,
  requestDetails: (id: string) => ['service-request-detail', id] as const,
  evidence: (id: string) => ['evidence','visit', id] as const,
};

export function useAllVisits() {
  return useQuery({ queryKey: visitKeys.all, queryFn: VisitApi.listAll });
}

export function useVisitsForRequest(requestId: string) {
  return useQuery({ queryKey: visitKeys.forRequest(requestId), queryFn: () => VisitApi.listForRequest(requestId), enabled: Boolean(requestId) });
}

export function useVisitDetails(visitId: string, agentMode = false) {
  const enabled = Boolean(visitId);
  const visit = useQuery({ queryKey: visitKeys.detail(visitId), queryFn: () => VisitApi.getDetails(visitId), enabled });
  const requestId = visit.data?.serviceRequestId ?? '';
  const request = useQuery({ queryKey: visitKeys.requestDetails(requestId), queryFn: () => VisitApi.getRequest(requestId), enabled: Boolean(requestId) && !agentMode });
  const evidence = useQuery({ queryKey: visitKeys.evidence(visitId), queryFn: () => VisitApi.getEvidenceForVisit(visitId), enabled: Boolean(visitId) });
  const visits = useQuery({ queryKey: agentMode ? ['visits','mine'] : visitKeys.forRequest(requestId), queryFn: agentMode ? VisitApi.listMine : () => VisitApi.listForRequest(requestId), enabled: agentMode || Boolean(requestId) });
  const assignmentHistory = useQuery({ queryKey: ['service-request-assignment-history', requestId], queryFn: () => VisitApi.getAssignmentHistory(requestId), enabled: Boolean(requestId) && !agentMode });
  return { visit, request, evidence, visits, assignmentHistory };
}

export function useCreateVisit(requestId: string) {
  const client = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateVisitRequest) => VisitApi.create(request),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: visitKeys.forRequest(requestId) });
      await client.invalidateQueries({ queryKey: visitKeys.all });
      await client.invalidateQueries({ queryKey: ['service-request-detail', requestId] });
    },
  });
}

export function useVisitAction() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ visitId, action }: { visitId: string; action: 'start' | 'complete' }) => action === 'start' ? VisitApi.start(visitId) : VisitApi.complete(visitId),
    onSuccess: async (visit) => {
      await client.invalidateQueries({ queryKey: visitKeys.detail(visit.visitId) });
      await client.invalidateQueries({ queryKey: visitKeys.forRequest(visit.serviceRequestId) });
      await client.invalidateQueries({ queryKey: visitKeys.requestDetails(visit.serviceRequestId) });
      await client.invalidateQueries({ queryKey: visitKeys.evidence(visit.visitId) });
      await client.invalidateQueries({ queryKey: ['visits','mine'] });
      await client.invalidateQueries({ queryKey: visitKeys.all });
      await client.invalidateQueries({ queryKey: ['customer-portal','visits'] });
      await client.invalidateQueries({ queryKey: ['customer-portal','requests'] });
      await client.invalidateQueries({ queryKey: ['customer-portal','request', visit.serviceRequestId] });
      await client.invalidateQueries({ queryKey: ['customer-portal','dashboard'] });
    },
  });
}

export function useGpsVerification() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: (request: GpsVerificationRequest) => VisitApi.verifyGps(request),
    onSuccess: async (verification) => {
      await Promise.all([
        client.invalidateQueries({ queryKey: visitKeys.detail(verification.visitId) }),
        client.invalidateQueries({ queryKey: ['visits', 'mine'] }),
        client.invalidateQueries({ queryKey: visitKeys.all }),
      ]);
    },
  });
}

export function useRescheduleVisit() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ visitId, request }: { visitId: string; request: RescheduleVisitRequest }) => VisitApi.reschedule(visitId, request),
    onSuccess: async (visit) => {
      await client.invalidateQueries({ queryKey: visitKeys.detail(visit.visitId) });
      await client.invalidateQueries({ queryKey: visitKeys.forRequest(visit.serviceRequestId) });
      await client.invalidateQueries({ queryKey: visitKeys.all });
    },
  });
}

export function useCancelVisit() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ visitId, reason }: { visitId: string; reason: string }) => VisitApi.cancel(visitId, reason),
    onSuccess: async (visit) => {
      await Promise.all([
        client.invalidateQueries({ queryKey: visitKeys.detail(visit.visitId) }),
        client.invalidateQueries({ queryKey: visitKeys.forRequest(visit.serviceRequestId) }),
        client.invalidateQueries({ queryKey: visitKeys.all }),
        client.invalidateQueries({ queryKey: ['visits', 'mine'] }),
        client.invalidateQueries({ queryKey: ['customer-portal', 'visits'] }),
        client.invalidateQueries({ queryKey: ['customer-portal', 'requests'] }),
        client.invalidateQueries({ queryKey: ['customer-portal', 'request', visit.serviceRequestId] }),
        client.invalidateQueries({ queryKey: ['customer-portal', 'dashboard'] }),
      ]);
    },
  });
}
