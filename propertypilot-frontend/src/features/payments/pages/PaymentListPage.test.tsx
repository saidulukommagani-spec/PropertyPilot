import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import PaymentListPage from './PaymentListPage';

const state = vi.hoisted(() => ({ payments: [] as Array<{ paymentId: string; invoiceId: string; customerId: string; invoiceNumber: string; customerName: string; amount: number; currencyCode?: string | null; paymentMethod: string; paymentStatus: string; provider: null; providerPaymentId: null; paymentDate: string; settledAt: string | null }> }));

vi.mock('../../../api/payments', () => ({ paymentsApi: { list: async () => state.payments } }));

afterEach(() => {
  cleanup();
  state.payments = [];
});

describe('PaymentListPage', () => {
  it('filters by settlement status and provides sortable amount/status/date columns', async () => {
    state.payments = [
      { paymentId: 'payment-one', invoiceId: 'invoice-one', customerId: 'customer-one', invoiceNumber: 'INV-001', customerName: 'Asha Owner', amount: 100, paymentMethod: 'UPI', paymentStatus: 'SUCCESS', provider: null, providerPaymentId: null, paymentDate: '2026-10-01T00:00:00Z', settledAt: '2026-10-01T00:01:00Z' },
      { paymentId: 'payment-two', invoiceId: 'invoice-two', customerId: 'customer-two', invoiceNumber: 'INV-002', customerName: 'Ben Owner', amount: 200, paymentMethod: 'CARD', paymentStatus: 'PENDING', provider: null, providerPaymentId: null, paymentDate: '2026-10-02T00:00:00Z', settledAt: null },
    ];
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter><PaymentListPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('Asha Owner')).toBeTruthy();
    expect(screen.getByText('Ben Owner')).toBeTruthy();
    expect(screen.getByRole('columnheader', { name: /Amount/ })).toBeTruthy();
    expect(screen.getByRole('columnheader', { name: /Status/ })).toBeTruthy();
    expect(screen.getByRole('columnheader', { name: /Payment \/ settlement date/ })).toBeTruthy();
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Payment status' }));
    fireEvent.click(await screen.findByRole('option', { name: 'Verified settlement' }));
    expect(screen.getByText('Asha Owner')).toBeTruthy();
    expect(screen.queryByText('Ben Owner')).toBeNull();
    expect(screen.queryByText('payment-one')).toBeNull();
  });

  it('formats each payment using its invoice currency and offers every backend status filter', async () => {
    state.payments = [
      { paymentId: 'payment-usd', invoiceId: 'invoice-usd', customerId: 'customer-usd', invoiceNumber: 'INV-USD', customerName: 'Mira Owner', amount: 50, currencyCode: 'USD', paymentMethod: 'CARD', paymentStatus: 'PENDING', provider: null, providerPaymentId: null, paymentDate: '2026-10-03T00:00:00Z', settledAt: null },
      { paymentId: 'payment-refunded', invoiceId: 'invoice-inr', customerId: 'customer-inr', invoiceNumber: 'INV-INR', customerName: 'Nikhil Owner', amount: 100, currencyCode: 'INR', paymentMethod: 'UPI', paymentStatus: 'REFUNDED', provider: null, providerPaymentId: null, paymentDate: '2026-10-02T00:00:00Z', settledAt: null },
    ];
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter><PaymentListPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('Mira Owner')).toBeTruthy();
    expect(screen.getByText(/\$50\.00/)).toBeTruthy();
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Payment status' }));
    expect(await screen.findByRole('option', { name: 'Initiated' })).toBeTruthy();
    expect(screen.getByRole('option', { name: 'Refunded' })).toBeTruthy();
    expect(screen.getByRole('option', { name: 'Cancelled' })).toBeTruthy();
    fireEvent.click(screen.getByRole('option', { name: 'Refunded' }));
    expect(screen.getByText('Nikhil Owner')).toBeTruthy();
    expect(screen.queryByText('Mira Owner')).toBeNull();
  });

  it('separates unverified success records from verified settlements', async () => {
    state.payments = [
      { paymentId: 'payment-verified', invoiceId: 'invoice-one', customerId: 'customer-one', invoiceNumber: 'INV-001', customerName: 'Asha Owner', amount: 100, paymentMethod: 'UPI', paymentStatus: 'SUCCESS', provider: null, providerPaymentId: null, paymentDate: '2026-10-01T00:00:00Z', settledAt: '2026-10-01T00:01:00Z' },
      { paymentId: 'payment-legacy', invoiceId: 'invoice-two', customerId: 'customer-two', invoiceNumber: 'INV-002', customerName: 'Ben Owner', amount: 200, paymentMethod: 'CARD', paymentStatus: 'SUCCESS', provider: null, providerPaymentId: null, paymentDate: '2026-10-02T00:00:00Z', settledAt: null },
    ];
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter><PaymentListPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('SUCCESS · UNVERIFIED')).toBeTruthy();
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Payment status' }));
    fireEvent.click(await screen.findByRole('option', { name: 'Verified settlement' }));
    expect(screen.getByText('Asha Owner')).toBeTruthy();
    expect(screen.queryByText('Ben Owner')).toBeNull();
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Payment status' }));
    fireEvent.click(await screen.findByRole('option', { name: 'Unverified success' }));
    expect(screen.getByText('Ben Owner')).toBeTruthy();
    expect(screen.queryByText('Asha Owner')).toBeNull();
  });
});
