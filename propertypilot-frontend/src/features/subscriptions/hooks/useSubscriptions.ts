import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getUserRoles } from '../../../api/auth';
import { useAuth } from '../../auth/AuthContext';
import { SubscriptionApi } from '../services/SubscriptionApi';

export const subscriptionKeys = {
  plans: ['subscriptions','plans'] as const,
  plan: (id:string) => ['subscriptions','plan',id] as const,
  versions: ['subscriptions','versions'] as const,
  benefits: ['subscriptions','benefits'] as const,
  entitlements: ['subscriptions','entitlements'] as const,
  services: ['subscriptions','services'] as const,
  profile: ['subscriptions','customer-profile'] as const,
  subscriptions: ['subscriptions','records'] as const,
  detail: (id:string) => ['subscriptions','detail',id] as const,
  usage: (id:string) => ['subscriptions','usage',id] as const,
  remaining: (id:string) => ['subscriptions','remaining',id] as const,
  renewals: (id:string) => ['subscriptions','renewals',id] as const,
  renewalInvoices: (customerId:string, operations:boolean) => ['subscriptions','renewal-invoices',customerId,operations] as const,
  addOns: ['subscriptions','add-on-catalog'] as const,
  purchasedAddOns: (id:string) => ['subscriptions','purchased-add-ons',id] as const,
};

export function useSubscriptionRole() {
  const {user}=useAuth();const roles=getUserRoles(user);return {isCustomer:roles.includes('CUSTOMER'),isAdmin:roles.includes('ADMIN'),isOperations:roles.includes('ADMIN')||roles.includes('OPERATIONS')};
}
export function useSubscriptionCatalog() {
  return {plans:useQuery({queryKey:subscriptionKeys.plans,queryFn:SubscriptionApi.plans}),versions:useQuery({queryKey:subscriptionKeys.versions,queryFn:SubscriptionApi.versions}),benefits:useQuery({queryKey:subscriptionKeys.benefits,queryFn:SubscriptionApi.benefits}),entitlements:useQuery({queryKey:subscriptionKeys.entitlements,queryFn:SubscriptionApi.entitlements}),services:useQuery({queryKey:subscriptionKeys.services,queryFn:SubscriptionApi.services})};
}
export function useSubscriptionPlan(id:string){return useQuery({queryKey:subscriptionKeys.plan(id),queryFn:()=>SubscriptionApi.plan(id),enabled:Boolean(id)});}
export function useCustomerId(){const role=useSubscriptionRole();return useQuery({queryKey:subscriptionKeys.profile,queryFn:SubscriptionApi.profile,enabled:role.isCustomer});}
export function useSubscriptions(){const role=useSubscriptionRole();const profile=useCustomerId();const query=useQuery({queryKey:[...subscriptionKeys.subscriptions,role.isOperations?'all':profile.data?.customerId],queryFn:()=>role.isOperations?SubscriptionApi.allSubscriptions():SubscriptionApi.customerSubscriptions(profile.data?.customerId??''),enabled:role.isOperations||Boolean(profile.data?.customerId)});return {role,profile,subscriptions:query};}
export function useSubscriptionDetail(id:string){return useQuery({queryKey:subscriptionKeys.detail(id),queryFn:()=>SubscriptionApi.subscription(id),enabled:Boolean(id)});}
export function useSubscriptionUsage(id:string){return {usage:useQuery({queryKey:subscriptionKeys.usage(id),queryFn:()=>SubscriptionApi.usage(id),enabled:Boolean(id)}),remaining:useQuery({queryKey:subscriptionKeys.remaining(id),queryFn:()=>SubscriptionApi.remaining(id),enabled:Boolean(id)})};}
export function useCreateSubscription(){const client=useQueryClient();return useMutation({mutationFn:({customerId,request}:{customerId:string;request:Parameters<typeof SubscriptionApi.createSubscription>[1]})=>SubscriptionApi.createSubscription(customerId,request),onSuccess:async()=>{await client.invalidateQueries({queryKey:subscriptionKeys.subscriptions});await client.invalidateQueries({queryKey:['customer-portal','dashboard']});}});}
export function useRenewSubscription(){const client=useQueryClient();return useMutation({mutationFn:SubscriptionApi.renew,onSuccess:async(result)=>{await client.invalidateQueries({queryKey:subscriptionKeys.subscriptions});await client.invalidateQueries({queryKey:subscriptionKeys.detail(result.customerSubscriptionId)});if(result.newCustomerSubscriptionId)await client.invalidateQueries({queryKey:subscriptionKeys.detail(result.newCustomerSubscriptionId)});await client.invalidateQueries({queryKey:subscriptionKeys.renewals(result.customerSubscriptionId)});await client.invalidateQueries({queryKey:['subscriptions','renewal-invoices']});}});}
export function useCreateRenewalInvoice(){const client=useQueryClient();return useMutation({mutationFn:SubscriptionApi.createRenewalInvoice,onSuccess:async()=>{await client.invalidateQueries({queryKey:['subscriptions','renewal-invoices']});await client.invalidateQueries({queryKey:['invoices']});}});}
export function useSendRenewalInvoice(){const client=useQueryClient();return useMutation({mutationFn:SubscriptionApi.sendRenewalInvoice,onSuccess:async()=>{await client.invalidateQueries({queryKey:['subscriptions','renewal-invoices']});await client.invalidateQueries({queryKey:['invoices']});}});}
export function useAddOnCatalog(){return useQuery({queryKey:subscriptionKeys.addOns,queryFn:SubscriptionApi.addOnCatalog});}
export function usePurchaseAddOn(){const client=useQueryClient();return useMutation({mutationFn:({subscriptionId,request}:{subscriptionId:string;request:Parameters<typeof SubscriptionApi.purchaseAddOn>[1]})=>SubscriptionApi.purchaseAddOn(subscriptionId,request),onSuccess:async(_result,variables)=>{await client.invalidateQueries({queryKey:subscriptionKeys.purchasedAddOns(variables.subscriptionId)});}});}
