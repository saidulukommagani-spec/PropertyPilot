export interface Deal {
  dealId: string; propertyId: string | null; sellerCustomerId: string; leadId: string | null; dealType: string; dealStatus: string;
  expectedAmount: number; finalAmount: number | null; sellerCommissionPercent: number | null; buyerCommissionPercent: number | null;
  totalCommissionAmount: number | null; createdAt: string; closedAt: string | null; remarks: string | null;
  selectedBuyerCustomerId: string | null; dealSource: string | null; exclusiveListing: boolean | null;
  buyerCommissionAmount: number | null; sellerCommissionAmount: number | null;
}
export interface DealCreateRequest {
  propertyId: string; sellerCustomerId: string; leadId?: string | null; dealType: string; expectedAmount: number;
  sellerCommissionPercent: number; buyerCommissionPercent: number; remarks: string; dealSource: string; exclusiveListing: boolean;
}
export interface DealStatusRequest { dealStatus: string; remarks?: string }
export interface CustomerOption { customerId: string; fullName: string; email?: string }
export interface PropertyOption { propertyId: string; customerId: string; title: string; price?: number }
export interface DealParticipant { participantId: string; dealId: string; customerId: string; customerName: string; participantRole: string; participantStatus: string; contactVisibility: string; offeredAmount: number | null; offerDate: string | null; selectedFlag: boolean | null; rejectedReason: string | null; remarks: string | null }
export interface AddDealParticipantRequest { customerId: string; participantRole: 'SELLER' | 'BUYER' | 'TENANT' | 'LANDLORD'; contactVisibility: 'HIDDEN' | 'PROPERTY_PILOT_ONLY' | 'SHARED'; offeredAmount?: number; remarks?: string }
