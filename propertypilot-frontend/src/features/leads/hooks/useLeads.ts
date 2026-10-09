import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { leadApi } from '../services/leadApi';
import type { CreateLeadRequest, LeadDealConversionRequest, LeadStatus, OperationsLeadSearchParams } from '../types/lead';
import type { CreateCustomerRequest } from '../../customer-portal/types/customerPortal';
import { customerKeys } from '../../customer-portal/hooks/useCustomerPortal';

export const leadKeys = {
  all: ['leads'] as const,
  operations: ['leads', 'operations'] as const,
  operationsPage: (params: OperationsLeadSearchParams) => [...leadKeys.operations, params] as const,
  mine: ['leads', 'mine'] as const,
  detail: (leadId: string) => ['leads', 'detail', leadId] as const,
  serviceRequests: (leadId: string) => ['leads', leadId, 'service-requests'] as const,
  activities: (leadId: string) => ['leads', leadId, 'activities'] as const,
  deals: (leadId: string) => ['leads', leadId, 'deals'] as const,
};

export function useMyLeads() {
  return useQuery({ queryKey: leadKeys.mine, queryFn: leadApi.mine });
}

export function useOperationsLeads(params: OperationsLeadSearchParams) {
  return useQuery({
    queryKey: leadKeys.operationsPage(params),
    queryFn: () => leadApi.searchOperationsLeads(params),
  });
}

export function useLead(leadId: string) {
  return useQuery({ queryKey: leadKeys.detail(leadId), queryFn: () => leadApi.get(leadId), enabled: Boolean(leadId) });
}

export function useLeadServiceRequests(leadId: string) {
  return useQuery({ queryKey: leadKeys.serviceRequests(leadId), queryFn: () => leadApi.serviceRequests(leadId), enabled: Boolean(leadId) });
}

export function useLeadActivities(leadId: string) {
  return useQuery({ queryKey: leadKeys.activities(leadId), queryFn: () => leadApi.activities(leadId), enabled: Boolean(leadId) });
}

export function useLeadDeals(leadId: string) {
  return useQuery({ queryKey: leadKeys.deals(leadId), queryFn: () => leadApi.deals(leadId), enabled: Boolean(leadId) });
}

export function useConvertLeadToDeal(leadId: string) {
  const client = useQueryClient();
  return useMutation({
    mutationFn: (request: LeadDealConversionRequest) => leadApi.convertToDeal(leadId, request),
    onSuccess: async (deal) => Promise.all([
      client.invalidateQueries({ queryKey: leadKeys.detail(leadId) }),
      client.invalidateQueries({ queryKey: leadKeys.activities(leadId) }),
      client.invalidateQueries({ queryKey: leadKeys.deals(leadId) }),
      client.invalidateQueries({ queryKey: leadKeys.mine }),
      client.invalidateQueries({ queryKey: leadKeys.operations }),
      client.invalidateQueries({ queryKey: ['deals'] }),
      client.setQueryData(['deals', 'detail', deal.dealId], deal),
    ]),
  });
}

export function useCreateLead() {
  const queryClient = useQueryClient();
  return useMutation({ mutationFn: (request: CreateLeadRequest) => leadApi.create(request), onSuccess: async () => {
    await queryClient.invalidateQueries({ queryKey: leadKeys.mine });
  } });
}

export function useUpdateLeadStatus(leadId: string) {
  const queryClient = useQueryClient();
  return useMutation({ mutationFn: (status: LeadStatus) => leadApi.updateStatus(leadId, status), onSuccess: async (lead) => {
    queryClient.setQueryData(leadKeys.detail(leadId), lead);
    await Promise.all([
      queryClient.invalidateQueries({ queryKey: leadKeys.mine }),
      queryClient.invalidateQueries({ queryKey: leadKeys.operations }),
    ]);
  } });
}

export function useCreateLeadServiceRequest(leadId: string) {
  const client = useQueryClient();
  const create = (input: { request: Omit<CreateCustomerRequest, 'leadId'>; operations: boolean }) => leadApi.createServiceRequest({ ...input.request, leadId }, input.operations);
  return useMutation({
    mutationFn: create,
    onSuccess: async () => Promise.all([
      client.invalidateQueries({ queryKey: leadKeys.serviceRequests(leadId) }),
      client.invalidateQueries({ queryKey: leadKeys.activities(leadId) }),
      client.invalidateQueries({ queryKey: customerKeys.requests }),
      client.invalidateQueries({ queryKey: customerKeys.quotes }),
    ]),
  });
}
