import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import type { InvoiceResponse } from '../types/invoice';
import { InvoiceDashboardPage, InvoiceDetailPage, InvoiceListPage } from './InvoicePages';

const state = vi.hoisted(() => ({ invoices: [] as InvoiceResponse[], invoiceDetail: null as InvoiceResponse | null, payments: [] as { paymentId: string; amount: number; paymentStatus: string; paymentMethod: string; paymentDate: string | null; settledAt?: string | null }[], paymentMutate: vi.fn(), revenue: { totalInvoices: 1, paidInvoices: 1, pendingInvoices: 0, totalRevenue: 100, collectedRevenue: 118, outstandingRevenue: 0, serviceRevenue: 100, subscriptionRevenue: 0, dealRevenue: 0 } }));

vi.mock('../hooks/useInvoices', () => ({
  useInvoices: () => ({ data: state.invoices, isLoading: false, isError: false, refetch: vi.fn() }),
  useInvoice: () => ({ data: state.invoiceDetail, isLoading: false, isError: false, refetch: vi.fn() }),
  useInvoicePayments: () => ({ data: state.payments, isLoading: false, isError: false, refetch: vi.fn() }),
  useInvoiceRevenue: () => ({ data: state.revenue, isLoading: false, isError: false, refetch: vi.fn() }),
  useSendInvoice: () => ({ mutate: vi.fn(), isPending: false, isError: false, error: null }),
  useCancelInvoice: () => ({ mutate: vi.fn(), isPending: false, isError: false, error: null }),
  useCompleteDealForInvoice: vi.fn(),
  useRecordInvoicePayment: () => ({ mutateAsync: state.paymentMutate, isPending: false, error: null }),
}));

afterEach(() => {
  cleanup();
  state.invoices = [];
  state.invoiceDetail = null;
  state.payments = [];
  state.paymentMutate.mockReset();
});

describe('InvoiceListPage', () => {
  it('shows invoice business labels and hides customer/entity UUIDs', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid',
      customerId: 'customer-uuid',
      customerName: 'Asha Customer',
      invoiceNumber: 'INV-2026-00042',
      entityType: 'SERVICE_REQUEST',
      entityId: 'request-uuid',
      invoiceType: 'SERVICE',
      invoiceAmount: 1000,
      taxAmount: 180,
      status: 'SENT',
      issuedAt: '2026-10-01T00:00:00Z',
      dueAt: '2026-10-15T00:00:00Z',
      paidAt: null,
      description: 'Property inspection service',
    }];

    render(<MemoryRouter><InvoiceListPage /></MemoryRouter>);

    expect(screen.getByText('Asha Customer')).toBeTruthy();
    expect(screen.getByText('Property inspection service')).toBeTruthy();
    expect(screen.getByText('Service Request')).toBeTruthy();
    expect(screen.getByText(/₹1,180/)).toBeTruthy();
    expect(screen.getByRole('columnheader', { name: 'Billed item' })).toBeTruthy();
    expect(screen.queryByText('customer-uuid')).toBeNull();
    expect(screen.queryByText('request-uuid')).toBeNull();
    expect(screen.queryByText('invoice-uuid')).toBeNull();
  });

  it('sorts by the displayed gross amount including tax', async () => {
    state.invoices = [
      { invoiceId: 'net-high', customerId: 'customer-1', invoiceNumber: 'INV-NET-HIGH', entityType: 'SERVICE_REQUEST', entityId: 'request-1', invoiceType: 'SERVICE', invoiceAmount: 200, taxAmount: 0, status: 'SENT', issuedAt: '2026-10-02T00:00:00Z', dueAt: null, paidAt: null, description: null },
      { invoiceId: 'gross-high', customerId: 'customer-2', invoiceNumber: 'INV-GROSS-HIGH', entityType: 'SERVICE_REQUEST', entityId: 'request-2', invoiceType: 'SERVICE', invoiceAmount: 150, taxAmount: 100, status: 'SENT', issuedAt: '2026-10-01T00:00:00Z', dueAt: null, paidAt: null, description: null },
    ];
    render(<MemoryRouter><InvoiceListPage /></MemoryRouter>);
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Sort' }));
    fireEvent.click(await screen.findByRole('option', { name: 'Amount: high to low' }));
    await waitFor(() => expect(screen.getAllByRole('link').map(link => link.textContent)).toEqual(['INV-GROSS-HIGH', 'INV-NET-HIGH']));
  });
});

describe('InvoiceDashboardPage', () => {
  it('uses gross invoiced value when calculating the collection percentage', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid',
      customerId: 'customer-uuid',
      customerName: 'Asha Customer',
      invoiceNumber: 'INV-2026-00043',
      entityType: 'SERVICE_REQUEST',
      entityId: 'request-uuid',
      invoiceType: 'SERVICE',
      invoiceAmount: 100,
      taxAmount: 18,
      status: 'PAID',
      issuedAt: '2026-10-01T00:00:00Z',
      dueAt: '2026-10-15T00:00:00Z',
      paidAt: '2026-10-10T00:00:00Z',
      description: 'Property inspection service',
    }];

    render(<MemoryRouter><InvoiceDashboardPage /></MemoryRouter>);

    expect(screen.getByText('Total Invoice Value - INR').parentElement?.textContent).toContain('₹118');
    expect(screen.getByText('Collection % - INR').parentElement?.textContent).toContain('100%');
    expect(screen.getByText('Fully paid invoices by paid date')).toBeTruthy();
  });

  it('keeps financial totals separate for invoices in different currencies', () => {
    state.invoices = [
      { invoiceId: 'inr', customerId: 'customer-1', invoiceNumber: 'INV-INR', entityType: 'SERVICE_REQUEST', entityId: 'request-1', invoiceType: 'SERVICE', invoiceAmount: 100, taxAmount: 18, currencyCode: 'INR', status: 'PAID', issuedAt: '2026-10-01T00:00:00Z', dueAt: null, paidAt: '2026-10-02T00:00:00Z', description: null },
      { invoiceId: 'usd', customerId: 'customer-2', invoiceNumber: 'INV-USD', entityType: 'SERVICE_REQUEST', entityId: 'request-2', invoiceType: 'SERVICE', invoiceAmount: 50, taxAmount: 5, currencyCode: 'USD', status: 'SENT', issuedAt: '2026-10-01T00:00:00Z', dueAt: null, paidAt: null, description: null },
    ];
    state.revenue = { ...state.revenue, revenueByCurrency: [
      { currencyCode: 'INR', totalInvoiceValue: 118, collectedAmount: 118, outstandingAmount: 0 },
      { currencyCode: 'USD', totalInvoiceValue: 55, collectedAmount: 0, outstandingAmount: 55 },
    ] };
    render(<MemoryRouter><InvoiceDashboardPage /></MemoryRouter>);
    expect(screen.getByText('Total Invoice Value - INR').parentElement?.textContent).toContain('118');
    expect(screen.getByText('Total Invoice Value - USD').parentElement?.textContent).toContain('55');
    expect(screen.getByText(/Financial amounts are shown separately by currency/)).toBeTruthy();
    expect(screen.queryByText('Total Invoice Value')).toBeNull();
  });
});

describe('InvoiceDetailPage', () => {
  it('does not offer payment registration while the invoice is still a draft', () => {
    state.invoiceDetail = {
      invoiceId: 'invoice-uuid', customerId: 'customer-uuid', customerName: 'Asha Customer', invoiceNumber: 'INV-2026-00044',
      entityType: 'SERVICE_REQUEST', entityId: 'request-uuid', invoiceType: 'SERVICE', invoiceAmount: 100, taxAmount: 18,
      status: 'DRAFT', issuedAt: null, dueAt: null, paidAt: null, description: 'Inspection service',
    };
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/invoices/invoice-uuid']}><Routes><Route path="/invoices/:id" element={<InvoiceDetailPage />} /></Routes></MemoryRouter></QueryClientProvider>);

    expect(screen.queryByRole('button', { name: 'Register payment' })).toBeNull();
  });

  it('requires a provider and provider transaction reference before registering payment', async () => {
    state.invoiceDetail = {
      invoiceId: 'invoice-uuid', customerId: 'customer-uuid', customerName: 'Asha Customer', invoiceNumber: 'INV-2026-00045',
      entityType: 'SERVICE_REQUEST', entityId: 'request-uuid', invoiceType: 'SERVICE', invoiceAmount: 100, taxAmount: 18,
      status: 'SENT', issuedAt: '2026-10-01T00:00:00Z', dueAt: '2026-10-15T00:00:00Z', paidAt: null, description: 'Inspection service',
    };
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/invoices/invoice-uuid']}><Routes><Route path="/invoices/:id" element={<InvoiceDetailPage />} /></Routes></MemoryRouter></QueryClientProvider>);

    fireEvent.click(screen.getByRole('button', { name: 'Register payment' }));
    fireEvent.click(screen.getByRole('button', { name: 'Register pending payment' }));

    await waitFor(() => {
      expect(screen.getByText('Enter the payment provider.')).toBeTruthy();
      expect(screen.getByText('Enter the provider transaction reference.')).toBeTruthy();
    });
    expect(state.paymentMutate).not.toHaveBeenCalled();
  });

  it('does not count an unverified successful payment toward the invoice balance', () => {
    state.invoiceDetail = {
      invoiceId: 'invoice-uuid', customerId: 'customer-uuid', customerName: 'Asha Customer', invoiceNumber: 'INV-2026-00046',
      entityType: 'SERVICE_REQUEST', entityId: 'request-uuid', invoiceType: 'SERVICE', invoiceAmount: 100, taxAmount: 18,
      status: 'SENT', issuedAt: '2026-10-01T00:00:00Z', dueAt: '2026-10-15T00:00:00Z', paidAt: null, description: 'Inspection service',
    };
    state.payments = [{
      paymentId: 'legacy-payment', amount: 118, paymentStatus: 'SUCCESS', paymentMethod: 'UPI',
      paymentDate: '2026-10-02T00:00:00Z', settledAt: null,
    }];
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/invoices/invoice-uuid']}><Routes><Route path="/invoices/:id" element={<InvoiceDetailPage />} /></Routes></MemoryRouter></QueryClientProvider>);

    expect(screen.getByRole('button', { name: 'Register payment' })).toBeTruthy();
    expect(screen.getByText('Settled payments').parentElement?.textContent).toContain('0');
    expect(screen.getByText('Balance due').parentElement?.textContent).toContain('118');
    expect(screen.getByRole('alert').textContent).toContain('marked successful without verified settlement evidence');
    expect(screen.getAllByText(/SUCCESS.*UNVERIFIED/)).toHaveLength(2);
  });

  it('does not add a verified payment in another currency to the invoice balance', () => {
    state.invoiceDetail = {
      invoiceId: 'invoice-uuid', customerId: 'customer-uuid', customerName: 'Asha Customer', invoiceNumber: 'INV-USD-MISMATCH',
      entityType: 'SERVICE_REQUEST', entityId: 'request-uuid', invoiceType: 'SERVICE', invoiceAmount: 100, taxAmount: 18,
      currencyCode: 'INR', status: 'SENT', issuedAt: '2026-10-01T00:00:00Z', dueAt: '2026-10-15T00:00:00Z', paidAt: null, description: 'Inspection service',
    };
    state.payments = [{
      paymentId: 'usd-payment', amount: 118, currencyCode: 'USD', paymentStatus: 'SUCCESS', paymentMethod: 'CARD',
      paymentDate: '2026-10-02T00:00:00Z', settledAt: '2026-10-02T00:00:00Z',
    }];
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/invoices/invoice-uuid']}><Routes><Route path="/invoices/:id" element={<InvoiceDetailPage />} /></Routes></MemoryRouter></QueryClientProvider>);

    expect(screen.getByText('Settled payments').parentElement?.textContent).toContain('0');
    expect(screen.getByText('Balance due').parentElement?.textContent).toContain('118');
    expect(screen.getByRole('alert').textContent).toContain('different currency from this invoice');
    expect(screen.getByText(/USD/)).toBeTruthy();
  });
});
