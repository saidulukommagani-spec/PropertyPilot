import type { PaymentResponse } from '../../invoices/types/invoice';

export function isVerifiedSettlement(payment: Pick<PaymentResponse, 'paymentStatus' | 'settledAt'>): boolean {
  return payment.paymentStatus === 'SUCCESS' && typeof payment.settledAt === 'string' && payment.settledAt.trim().length > 0;
}

export function paymentStatusLabel(payment: Pick<PaymentResponse, 'paymentStatus' | 'settledAt'>): string {
  return payment.paymentStatus === 'SUCCESS' && !isVerifiedSettlement(payment)
    ? 'SUCCESS · UNVERIFIED'
    : payment.paymentStatus.replaceAll('_', ' ');
}
