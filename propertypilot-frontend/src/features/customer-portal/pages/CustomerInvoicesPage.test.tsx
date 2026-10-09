import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import type { CustomerInvoice } from '../types/customerPortal';
import CustomerInvoicesPage from './CustomerInvoicesPage';

const state = vi.hoisted(() => ({ invoices: [] as CustomerInvoice[], payments: [] as Array<{ amount: number; currencyCode?: string | null; paymentStatus: string; settledAt?: string | null }> }));

vi.mock('../hooks/useCustomerPortal', () => ({
  useCustomerBilling: () => ({
    profile: { isError: false, refetch: vi.fn() },
    invoices: { data: state.invoices, isLoading: false, isError: false, refetch: vi.fn() },
  }),
}));
vi.mock('../../invoices/hooks/useInvoices', () => ({
  useInvoicePayments: () => ({ data: state.payments, isLoading: false, isError: false, refetch: vi.fn() }),
  useRecordInvoicePayment: () => ({ mutateAsync: vi.fn(), error: null, isPending: false }),
}));

afterEach(() => {
  cleanup();
  state.invoices = [];
  state.payments = [];
});

describe('CustomerInvoicesPage', () => {
  it('shows tax-inclusive invoice total with the subtotal and tax breakdown', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid',
      invoiceNumber: 'PP-INV-2026-0017',
      invoiceAmount: 100,
      taxAmount: 18,
      status: 'SENT',
      issuedAt: '2026-10-01T00:00:00Z',
      dueAt: '2026-10-31T00:00:00Z',
      description: 'Property inspection service',
    }];

    render(<CustomerInvoicesPage />);

    expect(screen.getByText('PP-INV-2026-0017')).toBeTruthy();
    expect(screen.getByText('Subtotal ₹100.00')).toBeTruthy();
    expect(screen.getByText('Tax ₹18.00')).toBeTruthy();
    expect(screen.getByText('Total ₹118.00')).toBeTruthy();
    expect(screen.queryByText('invoice-uuid')).toBeNull();
  });

  it('lets a customer report an external payment for an open invoice', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid',
      invoiceNumber: 'PP-INV-2026-0018',
      invoiceAmount: 100,
      taxAmount: 18,
      status: 'SENT',
    }];

    render(<CustomerInvoicesPage />);

    expect(screen.getByRole('button', { name: 'Report external payment' })).toBeTruthy();
  });

  it('does not allow another report while the full invoice payment is awaiting verification', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid',
      invoiceNumber: 'PP-INV-2026-0019',
      invoiceAmount: 100,
      taxAmount: 18,
      status: 'SENT',
    }];
    state.payments = [{ amount: 118, currencyCode: 'INR', paymentStatus: 'PENDING' }];

    render(<CustomerInvoicesPage />);

    expect(screen.getByText(/awaiting provider verification/)).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Report external payment' })).toBeNull();
  });

  it('does not count legacy successful payment without settlement timestamp for a paid invoice', () => {
    state.invoices = [{ invoiceId: 'invoice-uuid', invoiceNumber: 'PP-INV-LEGACY', invoiceAmount: 100, taxAmount: 18, status: 'PAID' }];
    state.payments = [{ amount: 118, paymentStatus: 'SUCCESS' }];

    render(<CustomerInvoicesPage />);

    expect(screen.getByText('PAID · RECONCILIATION REQUIRED')).toBeTruthy();
    expect(screen.getByText(/legacy payment is marked successful without verified settlement evidence/i)).toBeTruthy();
    expect(screen.getByText(/verified settlement records do not cover the invoice total/i)).toBeTruthy();
  });

  it('formats invoice amounts using the currency supplied by billing', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid',
      invoiceNumber: 'PP-INV-2026-0020',
      invoiceAmount: 100,
      taxAmount: 0,
      currencyCode: 'USD',
      status: 'PAID',
    }];

    render(<CustomerInvoicesPage />);

    const subtotal = screen.getByText(/Subtotal/).textContent ?? '';
    expect(subtotal).toContain('100.00');
    expect(subtotal).not.toContain('₹');
  });
  it('does not count a verified payment in another currency toward an invoice balance', () => {
    state.invoices = [{
      invoiceId: 'invoice-uuid', invoiceNumber: 'PP-INV-CURRENCY-MISMATCH', invoiceAmount: 100, taxAmount: 18,
      currencyCode: 'INR', status: 'PAID',
    }];
    state.payments = [{ amount: 118, currencyCode: 'USD', paymentStatus: 'SUCCESS', settledAt: '2026-10-02T00:00:00Z' }];

    render(<CustomerInvoicesPage />);

    expect(screen.getByText(/RECONCILIATION REQUIRED/)).toBeTruthy();
    expect(screen.getByText(/payment uses a different currency from this invoice/i)).toBeTruthy();
    expect(screen.getByText(/verified settlement records do not cover the invoice total/i)).toBeTruthy();
  });
});
