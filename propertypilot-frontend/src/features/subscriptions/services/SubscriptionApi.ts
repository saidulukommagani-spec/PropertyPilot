import { apiClient } from '../../../api/client';
import type { AddOnCatalogItem, CreateAddOnRequest, CreateRenewalInvoiceRequest, CreateRenewalRequest, CreateSubscriptionRequest, CustomerProfile, CustomerSubscription, PlanBenefit, PlanEntitlement, PlanVersion, PurchasedAddOn, RenewalInvoice, RenewalPayment, ServiceOption, SubscriptionPlan, SubscriptionRenewal, SubscriptionUsage } from '../types/subscription';

export const SubscriptionApi = {
  async plans(): Promise<SubscriptionPlan[]> { return (await apiClient.get<SubscriptionPlan[]>('/api/subscription-plans')).data; },
  async plan(id: string): Promise<SubscriptionPlan> { return (await apiClient.get<SubscriptionPlan>(`/api/subscription-plans/${id}`)).data; },
  async versions(): Promise<PlanVersion[]> { return (await apiClient.get<PlanVersion[]>('/api/subscription-plan-versions')).data; },
  async version(id: string): Promise<PlanVersion> { return (await apiClient.get<PlanVersion>(`/api/subscription-plan-versions/${id}`)).data; },
  async benefits(): Promise<PlanBenefit[]> { return (await apiClient.get<PlanBenefit[]>('/api/subscription-benefits')).data; },
  async entitlements(): Promise<PlanEntitlement[]> { return (await apiClient.get<PlanEntitlement[]>('/api/subscription-plan-entitlements')).data; },
  async services(): Promise<ServiceOption[]> { return (await apiClient.get<ServiceOption[]>('/api/services')).data; },
  async profile(): Promise<CustomerProfile> { return (await apiClient.get<CustomerProfile>('/api/customers/me')).data; },
  async customerSubscriptions(id: string): Promise<CustomerSubscription[]> { return (await apiClient.get<CustomerSubscription[]>(`/api/customers/${id}/subscriptions`)).data; },
  async allSubscriptions(): Promise<CustomerSubscription[]> { return (await apiClient.get<CustomerSubscription[]>('/api/customers/admin')).data; },
  async subscription(id: string): Promise<CustomerSubscription> { return (await apiClient.get<CustomerSubscription>(`/api/customers/subscriptions/${id}`)).data; },
  async createSubscription(customerId: string, request: CreateSubscriptionRequest): Promise<CustomerSubscription> { return (await apiClient.post<CustomerSubscription>(`/api/customers/${customerId}/subscriptions`, request)).data; },
  async usage(id: string): Promise<SubscriptionUsage[]> { return (await apiClient.get<SubscriptionUsage[]>(`/api/subscription-consumptions/${id}/usage`)).data; },
  async remaining(id: string): Promise<SubscriptionUsage[]> { return (await apiClient.get<SubscriptionUsage[]>(`/api/subscription-consumptions/${id}/remaining`)).data; },
  async renewals(id: string): Promise<SubscriptionRenewal[]> { return (await apiClient.get<SubscriptionRenewal[]>(`/api/subscription-renewals/subscription/${id}`)).data; },
  async createRenewalInvoice(request: CreateRenewalInvoiceRequest): Promise<RenewalInvoice> { return (await apiClient.post<RenewalInvoice>('/api/subscription-renewals/invoice', request)).data; },
  async renew(request: CreateRenewalRequest): Promise<SubscriptionRenewal> { return (await apiClient.post<SubscriptionRenewal>('/api/subscription-renewals', request)).data; },
  async renewalInvoices(customerId: string, operations: boolean): Promise<RenewalInvoice[]> {
    const path = operations ? '/api/v1/billing/invoices' : `/api/v1/billing/customers/${customerId}/invoices`;
    const response = await apiClient.get<RenewalInvoice[]>(path);
    return response.data.filter((invoice) => invoice.entityType === 'SUBSCRIPTION' && invoice.invoiceType === 'SUBSCRIPTION_RENEWAL');
  },
  async renewalPayments(invoiceId: string): Promise<RenewalPayment[]> { return (await apiClient.get<RenewalPayment[]>(`/api/payments/invoice/${invoiceId}`)).data; },
  async sendRenewalInvoice(invoiceId: string): Promise<RenewalInvoice> { return (await apiClient.post<RenewalInvoice>(`/api/v1/billing/invoices/${invoiceId}/send`)).data; },
  async addOnCatalog(): Promise<AddOnCatalogItem[]> { return (await apiClient.get<AddOnCatalogItem[]>('/api/customer-subscriptions/available')).data; },
  async purchasedAddOns(id: string): Promise<PurchasedAddOn[]> { return (await apiClient.get<PurchasedAddOn[]>(`/api/customer-subscriptions/${id}/add-ons`)).data; },
  async purchaseAddOn(id: string, request: CreateAddOnRequest): Promise<PurchasedAddOn> { return (await apiClient.post<PurchasedAddOn>(`/api/customer-subscriptions/${id}/add-ons`, request)).data; },
};
