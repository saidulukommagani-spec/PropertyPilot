export interface QuoteLineItem { lineType: string; description: string; amount: number }
export interface QuoteResponse {
  quoteId: string; customerId: string; propertyId: string | null; serviceId: string; serviceName: string;
  customerName?: string | null; customerEmail?: string | null; propertyName?: string | null; createdAt?: string | null;
  status: 'ACTIVE' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED' | string; subtotal: number; discountAmount: number;
  taxAmount: number; totalAmount: number; currencyCode: string; standardPrice: number | null; savings: number | null;
  expiresAt: string; acceptedAt: string | null; rejectedAt: string | null; rejectionReason: string | null; lineItems: QuoteLineItem[];
}
export interface PricingEstimateRequest { customerId: string; propertyId: string; serviceId: string; oneWayDistanceKm: number; etaDays: number; couponCode?: string }
export interface PricingEstimateResponse extends QuoteResponse { pricingEstimateId: string }
export interface QuoteCustomer { customerId: string; fullName: string; email: string }
export interface QuoteProperty { propertyId: string; customerId: string; title: string }
export interface QuoteServiceOption { serviceId: string; serviceName: string }
