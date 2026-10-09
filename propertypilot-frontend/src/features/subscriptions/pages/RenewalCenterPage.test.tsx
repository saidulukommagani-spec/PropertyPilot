import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import RenewalCenterPage from './RenewalCenterPage';

const mocks = vi.hoisted(() => ({
  createInvoice: vi.fn(),
  sendInvoice: vi.fn(),
  renew: vi.fn(),
  renewalInvoices: vi.fn(),
  renewals: vi.fn(),
  payments: vi.fn(),
  operations: false,
  invoices: [] as Array<Record<string, unknown>>,
}));

vi.mock('../hooks/useSubscriptions', () => ({
  subscriptionKeys: { renewalInvoices: (customerId: string, operations: boolean) => ['subscriptions', 'renewal-invoices', customerId, operations], renewals: (id: string) => ['subscriptions', 'renewals', id] },
  useSubscriptions: () => ({
    role: { isOperations: mocks.operations, isAdmin: mocks.operations, isCustomer: !mocks.operations },
    profile: { data: { customerId: 'customer-1' }, isError: false, isLoading: false, refetch: vi.fn() },
    subscriptions: { data: [{ customerSubscriptionId: 'subscription-1', customerId: 'customer-1', planVersionId: 'version-1', planName: 'Annual plan', status: 'EXPIRED', startDate: '2025-10-08', endDate: '2026-10-07', autoRenew: false }], isError: false, isLoading: false, refetch: vi.fn() },
  }),
  useCreateRenewalInvoice: () => ({ mutateAsync: mocks.createInvoice, isPending: false }),
  useSendRenewalInvoice: () => ({ mutateAsync: mocks.sendInvoice, isPending: false }),
  useRenewSubscription: () => ({ mutateAsync: mocks.renew, isPending: false }),
}));

vi.mock('../services/SubscriptionApi', () => ({
  SubscriptionApi: {
    renewalInvoices: (...args: unknown[]) => mocks.renewalInvoices(...args),
    renewals: (...args: unknown[]) => mocks.renewals(...args),
    renewalPayments: (...args: unknown[]) => mocks.payments(...args),
  },
}));

function renderPage() {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false }, mutations: { retry: false } } });
  return render(<QueryClientProvider client={client}><MemoryRouter><RenewalCenterPage /></MemoryRouter></QueryClientProvider>);
}

describe('RenewalCenterPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mocks.operations = false;
    mocks.invoices = [];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    mocks.renewals.mockResolvedValue([]);
    mocks.payments.mockResolvedValue([]);
    mocks.createInvoice.mockResolvedValue({ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1' });
    mocks.sendInvoice.mockResolvedValue({});
    mocks.renew.mockResolvedValue({ periodStart: '2026-10-08', periodEnd: '2027-10-07' });
  });

  it('creates a dated renewal invoice for an expired subscription using the existing term duration', async () => {
    renderPage();

    fireEvent.click(await screen.findByRole('button', { name: 'Prepare renewal invoice' }));

    await waitFor(() => expect(mocks.createInvoice).toHaveBeenCalledWith({
      customerSubscriptionId: 'subscription-1',
      periodStart: '2026-10-08',
      periodEnd: '2027-10-07',
    }));
    expect(await screen.findByText(/Renewal invoice PP-R-1 is ready/)).toBeTruthy();
  });

  it('does not offer renewal activation for a paid invoice without a settled payment record', async () => {
    mocks.invoices = [{ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1', customerId: 'customer-1', entityType: 'SUBSCRIPTION', entityId: 'subscription-1', invoiceType: 'SUBSCRIPTION_RENEWAL', invoiceAmount: 1000, taxAmount: 0, currencyCode: 'INR', status: 'PAID', renewalPeriodStart: '2026-10-08', renewalPeriodEnd: '2027-10-07' }];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    renderPage();

    expect(await screen.findByText('Payment record unavailable')).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Apply verified renewal' })).toBeNull();
  });

  it('does not apply renewal from a legacy success record without settlement verification', async () => {
    mocks.invoices = [{ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1', customerId: 'customer-1', entityType: 'SUBSCRIPTION', entityId: 'subscription-1', invoiceType: 'SUBSCRIPTION_RENEWAL', invoiceAmount: 1000, taxAmount: 0, currencyCode: 'INR', status: 'PAID', renewalPeriodStart: '2026-10-08', renewalPeriodEnd: '2027-10-07' }];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    mocks.payments.mockResolvedValue([{ paymentId: 'payment-1', invoiceId: 'invoice-1', paymentStatus: 'SUCCESS', amount: 1000, currencyCode: 'INR' }]);
    renderPage();

    expect(await screen.findByText('Settlement requires reconciliation')).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Apply verified renewal' })).toBeNull();
    expect(mocks.renew).not.toHaveBeenCalled();
  });

  it('does not treat a verified payment in another currency as renewal settlement', async () => {
    mocks.invoices = [{ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1', customerId: 'customer-1', entityType: 'SUBSCRIPTION', entityId: 'subscription-1', invoiceType: 'SUBSCRIPTION_RENEWAL', invoiceAmount: 1000, taxAmount: 0, currencyCode: 'INR', status: 'PAID', renewalPeriodStart: '2026-10-08', renewalPeriodEnd: '2027-10-07' }];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    mocks.payments.mockResolvedValue([{ paymentId: 'payment-1', invoiceId: 'invoice-1', paymentStatus: 'SUCCESS', amount: 1000, currencyCode: 'USD', settledAt: '2026-10-08T11:00:00Z' }]);
    renderPage();

    expect(await screen.findByText('Settlement requires reconciliation')).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Apply verified renewal' })).toBeNull();
    expect(mocks.renew).not.toHaveBeenCalled();
  });

  it('does not treat a partial verified payment as full renewal settlement', async () => {
    mocks.invoices = [{ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1', customerId: 'customer-1', entityType: 'SUBSCRIPTION', entityId: 'subscription-1', invoiceType: 'SUBSCRIPTION_RENEWAL', invoiceAmount: 1000, taxAmount: 0, currencyCode: 'INR', status: 'PAID', renewalPeriodStart: '2026-10-08', renewalPeriodEnd: '2027-10-07' }];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    mocks.payments.mockResolvedValue([{ paymentId: 'payment-1', invoiceId: 'invoice-1', paymentStatus: 'SUCCESS', amount: 500, currencyCode: 'INR', settledAt: '2026-10-08T11:00:00Z' }]);
    renderPage();

    expect(await screen.findByText('Settlement requires reconciliation')).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Apply verified renewal' })).toBeNull();
    expect(mocks.renew).not.toHaveBeenCalled();
  });

  it('keeps renewal invoice sending restricted to the Admin role', async () => {
    mocks.invoices = [{ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1', customerId: 'customer-1', entityType: 'SUBSCRIPTION', entityId: 'subscription-1', invoiceType: 'SUBSCRIPTION_RENEWAL', invoiceAmount: 1000, taxAmount: 0, currencyCode: 'INR', status: 'DRAFT', renewalPeriodStart: '2026-10-08', renewalPeriodEnd: '2027-10-07' }];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    renderPage();
    expect(await screen.findByText('Awaiting admin to send')).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Send invoice' })).toBeNull();

    mocks.operations = true;
    renderPage();
    fireEvent.click(await screen.findByRole('button', { name: 'Send invoice' }));
    await waitFor(() => expect(mocks.sendInvoice).toHaveBeenCalledWith('invoice-1'));
  });

  it('applies renewal only when the matching invoice has a verified settlement record', async () => {
    mocks.invoices = [{ invoiceId: 'invoice-1', invoiceNumber: 'PP-R-1', customerId: 'customer-1', entityType: 'SUBSCRIPTION', entityId: 'subscription-1', invoiceType: 'SUBSCRIPTION_RENEWAL', invoiceAmount: 1000, taxAmount: 0, currencyCode: 'INR', status: 'PAID', renewalPeriodStart: '2026-10-08', renewalPeriodEnd: '2027-10-07' }];
    mocks.renewalInvoices.mockResolvedValue(mocks.invoices);
    mocks.payments.mockResolvedValue([{ paymentId: 'payment-1', invoiceId: 'invoice-1', paymentStatus: 'SUCCESS', amount: 1000, currencyCode: 'INR', settledAt: '2026-10-08T11:00:00Z' }]);
    renderPage();

    fireEvent.click(await screen.findByRole('button', { name: 'Apply verified renewal' }));
    await waitFor(() => expect(mocks.renew).toHaveBeenCalledWith({
      customerSubscriptionId: 'subscription-1',
      paymentId: 'payment-1',
      periodStart: '2026-10-08',
      periodEnd: '2027-10-07',
      renewalType: 'MANUAL',
      renewalAmount: 1000,
      remarks: 'Settled renewal invoice PP-R-1',
    }));
  });
});
