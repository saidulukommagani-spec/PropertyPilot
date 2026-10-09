import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import type { InvoiceResponse, PaymentResponse } from '../../invoices/types/invoice';
import DealFinancialsPanel from './DealFinancialsPanel';

const state = vi.hoisted(() => ({
  invoices: [] as InvoiceResponse[],
  paymentQueries: [] as Array<{ data: PaymentResponse[]; isLoading: boolean; isError: boolean; refetch: () => void }>,
}));

vi.mock('@tanstack/react-query', () => ({
  useQuery: () => ({ data: state.invoices, isLoading: false, isError: false, refetch: vi.fn() }),
  useQueries: () => state.paymentQueries,
}));
vi.mock('../../invoices/services/invoiceApi', () => ({ invoiceApi: { listForEntity: vi.fn(), payments: vi.fn() } }));

afterEach(() => {
  cleanup();
  state.invoices = [];
  state.paymentQueries = [];
});

describe('DealFinancialsPanel', () => {
  it('keeps invoice and settlement totals separate by currency', () => {
    state.invoices = [
      { invoiceId: 'inr-invoice', customerId: 'customer-1', invoiceNumber: 'INV-INR', entityType: 'DEAL', entityId: 'deal-1', invoiceType: 'COMMISSION', invoiceAmount: 100, taxAmount: 18, currencyCode: 'INR', status: 'PARTIALLY_PAID', issuedAt: null, dueAt: null, paidAt: null, description: null },
      { invoiceId: 'usd-invoice', customerId: 'customer-1', invoiceNumber: 'INV-USD', entityType: 'DEAL', entityId: 'deal-1', invoiceType: 'COMMISSION', invoiceAmount: 100, taxAmount: 10, currencyCode: 'USD', status: 'PARTIALLY_PAID', issuedAt: null, dueAt: null, paidAt: null, description: null },
    ];
    state.paymentQueries = [
      { data: [{ paymentId: 'inr-payment', invoiceId: 'inr-invoice', customerId: 'customer-1', amount: 50, currencyCode: 'INR', paymentMethod: 'UPI', paymentStatus: 'SUCCESS', provider: null, providerPaymentId: null, paymentDate: null, settledAt: '2026-10-01T00:00:00Z' }], isLoading: false, isError: false, refetch: vi.fn() },
      { data: [{ paymentId: 'usd-payment', invoiceId: 'usd-invoice', customerId: 'customer-1', amount: 40, currencyCode: 'USD', paymentMethod: 'CARD', paymentStatus: 'SUCCESS', provider: null, providerPaymentId: null, paymentDate: null, settledAt: '2026-10-01T00:00:00Z' }], isLoading: false, isError: false, refetch: vi.fn() },
    ];

    render(<MemoryRouter><DealFinancialsPanel dealId="deal-1" completed /></MemoryRouter>);

    expect(screen.getByText('Invoiced for deal - INR').parentElement?.textContent).toContain('₹118.00');
    expect(screen.getByText('Invoiced for deal - USD').parentElement?.textContent).toContain('$110.00');
    expect(screen.getByText('Settled payments - INR').parentElement?.textContent).toContain('₹50.00');
    expect(screen.getByText('Settled payments - USD').parentElement?.textContent).toContain('$40.00');
    expect(screen.getByText('Outstanding balance - INR').parentElement?.textContent).toContain('₹68.00');
    expect(screen.getByText('Outstanding balance - USD').parentElement?.textContent).toContain('$70.00');
  });
});
