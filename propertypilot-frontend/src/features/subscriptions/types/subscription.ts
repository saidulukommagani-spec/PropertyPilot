export interface SubscriptionPlan { subscriptionPlanId: string; planCode: string; planName: string; description?: string; status: string }
export interface PlanVersion { planVersionId: string; subscriptionPlanId: string; versionNumber: number; price: number; currencyCode: string; effectiveFrom: string; effectiveTo?: string; status: string; benefitsJson?: Record<string, unknown> }
export interface CustomerSubscription { customerSubscriptionId: string; customerId: string; customerName?: string; planVersionId: string; planName?: string; status: string; startDate: string; endDate?: string; autoRenew: boolean }
export interface PlanEntitlement { entitlementId: string; planVersionId: string; serviceId: string; quantity: number; periodType: string; carryForwardAllowed: boolean; status: string }
export interface PlanBenefit { benefitId: string; planId: string; benefitType: string; benefitValue: string; status: string }
export interface SubscriptionUsage { serviceId: string; serviceName?: string | null; entitledQuantity: number; consumedQuantity: number; remainingQuantity: number }
export interface SubscriptionRenewal { subscriptionRenewalId: string; customerSubscriptionId: string; newCustomerSubscriptionId?: string; renewalDate: string; periodStart: string; periodEnd: string; renewalType: string; renewalAmount?: number; remarks?: string; status: string }
export interface AddOnCatalogItem { subscriptionAddOnId: string; serviceId: string; serviceName: string; name: string; price: number; status: string }
export interface PurchasedAddOn { customerSubscriptionAddOnId: string; customerSubscriptionId: string; subscriptionAddOnId: string; addOnName: string; quantity: number; status: string; startsAt?: string; endsAt?: string }
export interface ServiceOption { serviceId: string; serviceName: string; serviceCode: string; category?: string }
export interface CustomerProfile { customerId: string; fullName: string; email: string; mobileNumber: string }
export interface CreateSubscriptionRequest { planVersionId: string; startDate: string; endDate: string; autoRenew: boolean }
export interface CreateRenewalRequest { customerSubscriptionId: string; paymentId: string; periodStart: string; periodEnd: string; renewalType: string; renewalAmount: number; remarks?: string }
export interface RenewalInvoice { invoiceId: string; invoiceNumber: string; customerId: string; entityType: string; entityId: string; invoiceType: string; invoiceAmount: number; taxAmount: number; currencyCode: string; status: string; renewalPeriodStart?: string; renewalPeriodEnd?: string; description?: string }
export interface CreateRenewalInvoiceRequest { customerSubscriptionId: string; periodStart: string; periodEnd: string }
export interface RenewalPayment { paymentId: string; invoiceId: string; paymentStatus: string; amount: number; currencyCode: string; settledAt?: string | null }
export interface CreateAddOnRequest { subscriptionAddOnId: string; quantity: number; startsAt?: string; endsAt?: string }
