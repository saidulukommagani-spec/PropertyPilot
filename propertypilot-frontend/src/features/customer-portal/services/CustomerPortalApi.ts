import { apiClient } from '../../../api/client';
import type { CustomerDashboard, CustomerDocument, CustomerEvidence, CustomerInvoice, CustomerProfile, CustomerProperty, CustomerQuote, CustomerRequest, CustomerRequestCreated, CustomerRequestDetails, CustomerService, EligibleServiceType, CustomerSubscription, CustomerVisit, CreateCustomerRequest, CustomerEstimate, VisitEvidenceSummary } from '../types/customerPortal';
import type { SubscriptionUsage } from '../types/customerUsage';

export const CustomerPortalApi = {
  async profile(): Promise<CustomerProfile> { return (await apiClient.get<CustomerProfile>('/api/customers/me')).data; },
  async dashboard(customerId: string): Promise<CustomerDashboard> { return (await apiClient.get<CustomerDashboard>(`/api/customer-dashboard/${customerId}`)).data; },
  async properties(): Promise<CustomerProperty[]> { return (await apiClient.get<CustomerProperty[]>('/api/v1/properties')).data; },
  async property(propertyId: string): Promise<CustomerProperty> { return (await apiClient.get<CustomerProperty>(`/api/v1/properties/${propertyId}`)).data; },
  async requests(): Promise<CustomerRequest[]> {
    const customer = await CustomerPortalApi.profile();
    return (await apiClient.get<CustomerRequest[]>(`/api/service-requests/customer/${customer.customerId}`)).data;
  },
  async request(requestId: string): Promise<CustomerRequestDetails> { return (await apiClient.get<CustomerRequestDetails>(`/api/service-requests/${requestId}`)).data; },
  async createRequest(request: CreateCustomerRequest): Promise<CustomerRequestCreated> { return (await apiClient.post<CustomerRequestCreated>('/api/service-requests', request)).data; },
  async customerEstimate(request: { propertyId: string; serviceId: string }): Promise<CustomerEstimate> { return (await apiClient.post<CustomerEstimate>('/api/v1/pricing/customer-estimates', request)).data; },
  async acceptEstimateAndCreateRequest(request: CreateCustomerRequest): Promise<CustomerRequestCreated> { return (await apiClient.post<CustomerRequestCreated>(`/api/v1/quotes/${request.quoteId}/accept-and-create-request`, request)).data; },
  async visits(): Promise<CustomerVisit[]> { return (await apiClient.get<CustomerVisit[]>('/api/v1/visits/customer')).data; },
  async visit(visitId: string): Promise<CustomerVisit> { return (await apiClient.get<CustomerVisit>(`/api/v1/visits/${visitId}`)).data; },
  async requestEvidence(requestId: string): Promise<CustomerEvidence[]> { return (await apiClient.get<CustomerEvidence[]>(`/api/evidence/service-request/${requestId}`)).data; },
  async visitEvidence(visitId: string): Promise<CustomerEvidence[]> { return (await apiClient.get<CustomerEvidence[]>(`/api/evidence/visit/${visitId}`)).data; },
  async visitEvidenceSummary(visitId: string): Promise<VisitEvidenceSummary> { return (await apiClient.get<VisitEvidenceSummary>(`/api/evidence/visit/${visitId}/summary`)).data; },
  async documents(propertyId: string): Promise<CustomerDocument[]> { return (await apiClient.get<CustomerDocument[]>(`/api/v1/properties/${propertyId}/documents`)).data; },
  async quotes(): Promise<CustomerQuote[]> { return (await apiClient.get<CustomerQuote[]>('/api/v1/quotes')).data; },
  async services(): Promise<CustomerService[]> { return (await apiClient.get<CustomerService[]>('/api/services')).data; },
  async eligibleServiceTypes(propertyId: string): Promise<EligibleServiceType[]> { return (await apiClient.get<EligibleServiceType[]>('/api/services/eligible-types', { params: { propertyId } })).data; },
  async eligibleServices(propertyId: string, serviceType: string): Promise<CustomerService[]> { return (await apiClient.get<CustomerService[]>('/api/services/eligible', { params: { propertyId, serviceType } })).data; },
  async subscriptions(customerId: string): Promise<CustomerSubscription[]> { return (await apiClient.get<CustomerSubscription[]>(`/api/customers/${customerId}/subscriptions`)).data; },
  async invoices(customerId: string): Promise<CustomerInvoice[]> { return (await apiClient.get<CustomerInvoice[]>(`/api/v1/billing/customers/${customerId}/invoices`)).data; },
  async subscriptionUsage(subscriptionId: string): Promise<SubscriptionUsage[]> { return (await apiClient.get<SubscriptionUsage[]>(`/api/subscription-consumptions/${subscriptionId}/usage`)).data; },
  async subscriptionRemaining(subscriptionId: string): Promise<SubscriptionUsage[]> { return (await apiClient.get<SubscriptionUsage[]>(`/api/subscription-consumptions/${subscriptionId}/remaining`)).data; },
};
