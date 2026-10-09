import { afterEach, describe, expect, it, vi } from 'vitest';
import { apiClient } from './client';
import { paymentsApi } from './payments';

afterEach(() => vi.restoreAllMocks());

describe('paymentsApi', () => {
  it('loads complete payment rows from the operations list endpoint in newest-first order', async () => {
    const get = vi.spyOn(apiClient, 'get').mockResolvedValue({ data: [
      { paymentId: 'payment-old', invoiceId: 'invoice-1', customerId: 'customer-1', invoiceNumber: 'PP-INV-1', customerName: 'Asha Rao', amount: 500, currencyCode: 'INR', paymentMethod: 'UPI', paymentStatus: 'SUCCESS', provider: 'Provider', providerPaymentId: 'txn-1', paymentDate: '2026-10-07T00:00:00Z', settledAt: '2026-10-07T00:00:01Z' },
      { paymentId: 'payment-new', invoiceId: 'invoice-2', customerId: 'customer-2', invoiceNumber: 'PP-INV-2', customerName: 'Ben Rao', amount: 50, currencyCode: 'USD', paymentMethod: 'CARD', paymentStatus: 'PENDING', provider: 'Provider', providerPaymentId: 'txn-2', paymentDate: '2026-10-08T00:00:00Z', settledAt: null },
    ] } as never);

    await expect(paymentsApi.list()).resolves.toMatchObject([
      { paymentId: 'payment-new', invoiceNumber: 'PP-INV-2', customerName: 'Ben Rao', currencyCode: 'USD' },
      { paymentId: 'payment-old', invoiceNumber: 'PP-INV-1', customerName: 'Asha Rao', currencyCode: 'INR' },
    ]);
    expect(get).toHaveBeenCalledTimes(1);
    expect(get).toHaveBeenCalledWith('/api/payments');
  });
});
