import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { CustomerPortalApi } from '../services/CustomerPortalApi';

export const customerKeys = {
  profile: ['customer-portal','profile'] as const,
  dashboard: (id: string) => ['customer-portal','dashboard',id] as const,
  properties: ['customer-portal','properties'] as const,
  property: (id: string) => ['customer-portal','property',id] as const,
  requests: ['customer-portal','requests'] as const,
  request: (id: string) => ['customer-portal','request',id] as const,
  visits: ['customer-portal','visits'] as const,
  visit: (id: string) => ['customer-portal','visit',id] as const,
  evidence: ['customer-portal','evidence'] as const,
  evidenceForRequest: (id: string) => ['customer-portal','request-evidence',id] as const,
  evidenceForVisit: (id: string) => ['customer-portal','visit-evidence',id] as const,
  documents: ['customer-portal','documents'] as const,
  quotes: ['customer-portal','quotes'] as const,
  eligibleServiceTypes: (propertyId: string) => ['customer-portal','eligible-service-types',propertyId] as const,
  eligibleServices: (propertyId: string, serviceType: string) => ['customer-portal','eligible-services',propertyId,serviceType] as const,
  subscriptions: (id: string) => ['customer-portal','subscriptions',id] as const,
  invoices: (id: string) => ['customer-portal','invoices',id] as const,
};

export function useCustomerProfile() { return useQuery({ queryKey: customerKeys.profile, queryFn: CustomerPortalApi.profile }); }
export function useCustomerDashboard() { const profile = useCustomerProfile(); const customerId = profile.data?.customerId ?? ''; const dashboard = useQuery({ queryKey: customerKeys.dashboard(customerId), queryFn: () => CustomerPortalApi.dashboard(customerId), enabled: Boolean(customerId) }); return { profile, dashboard }; }
export function useCustomerProperties() { return useQuery({ queryKey: customerKeys.properties, queryFn: CustomerPortalApi.properties }); }
export function useCustomerProperty(id: string) { return useQuery({ queryKey: customerKeys.property(id), queryFn: () => CustomerPortalApi.property(id), enabled: Boolean(id) }); }
export function useCustomerRequests() { return useQuery({ queryKey: customerKeys.requests, queryFn: CustomerPortalApi.requests }); }
export function useCustomerRequest(id: string) { return useQuery({ queryKey: customerKeys.request(id), queryFn: () => CustomerPortalApi.request(id), enabled: Boolean(id) }); }
export function useCustomerVisits() { return useQuery({ queryKey: customerKeys.visits, queryFn: CustomerPortalApi.visits }); }
export function useCustomerVisit(id: string) { return useQuery({ queryKey: customerKeys.visit(id), queryFn: () => CustomerPortalApi.visit(id), enabled: Boolean(id) }); }
export function useCustomerEvidenceForRequest(id: string) { return useQuery({ queryKey: customerKeys.evidenceForRequest(id), queryFn: () => CustomerPortalApi.requestEvidence(id), enabled: Boolean(id) }); }
export function useCustomerEvidenceForVisit(id: string) { return useQuery({ queryKey: customerKeys.evidenceForVisit(id), queryFn: () => CustomerPortalApi.visitEvidence(id), enabled: Boolean(id) }); }
export function useCustomerQuotes() { return useQuery({ queryKey: customerKeys.quotes, queryFn: CustomerPortalApi.quotes }); }
export function useCreateCustomerEstimate() { return useMutation({ mutationFn: CustomerPortalApi.customerEstimate }); }
export function useCustomerEstimateRequirements(propertyId: string, serviceId: string) { return useQuery({ queryKey: ['customer-portal','estimate-requirements',propertyId,serviceId], queryFn: () => CustomerPortalApi.customerEstimateRequirements(propertyId, serviceId), enabled: Boolean(propertyId && serviceId) }); }
export function useCustomerServices() { return useQuery({ queryKey: ['customer-portal', 'services'], queryFn: CustomerPortalApi.services }); }
export function useCustomerEligibleServiceTypes(propertyId: string) { return useQuery({ queryKey: customerKeys.eligibleServiceTypes(propertyId), queryFn: () => CustomerPortalApi.eligibleServiceTypes(propertyId), enabled: Boolean(propertyId) }); }
export function useCustomerEligibleServices(propertyId: string, serviceType: string) { return useQuery({ queryKey: customerKeys.eligibleServices(propertyId, serviceType), queryFn: () => CustomerPortalApi.eligibleServices(propertyId, serviceType), enabled: Boolean(propertyId && serviceType) }); }
export function useCustomerDocuments() {
  const properties = useCustomerProperties();
  const query = useQuery({ queryKey: [...customerKeys.documents, properties.data?.map((item) => item.propertyId).join('|')], queryFn: async () => Promise.all((properties.data ?? []).map((property) => CustomerPortalApi.documents(property.propertyId))).then((items) => items.flat()), enabled: properties.isSuccess });
  return { properties, documents: query };
}
export function useCustomerBilling() {
  const profile = useCustomerProfile(); const id = profile.data?.customerId ?? '';
  return { profile, subscriptions: useQuery({ queryKey: customerKeys.subscriptions(id), queryFn: () => CustomerPortalApi.subscriptions(id), enabled: Boolean(id) }), invoices: useQuery({ queryKey: customerKeys.invoices(id), queryFn: () => CustomerPortalApi.invoices(id), enabled: Boolean(id) }) };
}
export function useCreateCustomerRequest() {
  const client = useQueryClient();
  return useMutation({ mutationFn: CustomerPortalApi.acceptEstimateAndCreateRequest, onSuccess: async () => { await client.invalidateQueries({ queryKey: customerKeys.requests }); await client.invalidateQueries({ queryKey: customerKeys.quotes }); await client.invalidateQueries({ queryKey: ['customer-portal','dashboard'] }); } });
}
