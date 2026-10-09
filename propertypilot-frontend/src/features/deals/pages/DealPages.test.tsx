import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import type { Deal } from '../types/deal';
import { DealDashboardPage, DealListPage, DealRevenueMetric } from './DealPages';

const state = vi.hoisted(() => ({ deals: [] as Deal[], revenue: { isLoading: false, isError: false, data: { dealRevenue: 999, revenueByCurrency: [{ currencyCode: 'INR', totalInvoiceValue: 118, collectedAmount: 118, outstandingAmount: 0, dealRevenue: 30 }, { currencyCode: 'USD', totalInvoiceValue: 20, collectedAmount: 20, outstandingAmount: 0, dealRevenue: 20 }] }, refetch: vi.fn() } }));

vi.mock('../hooks/useDeals', () => ({
  useDeals: () => ({ data: state.deals, isLoading: false, isError: false, refetch: vi.fn() }),
  useDeal: vi.fn(),
  useCancelDeal: vi.fn(),
  useCreateDeal: vi.fn(),
  useUpdateDealStatus: vi.fn(),
}));
vi.mock('../services/dealApi', () => ({
  dealApi: {
    customers: async () => [{ customerId: 'customer-uuid', fullName: 'Asha Customer' }],
    properties: async () => [{ propertyId: 'property-uuid', customerId: 'customer-uuid', title: 'Lakeview Residence' }],
  },
}));
vi.mock('../../invoices/hooks/useInvoices', () => ({ useCompleteDealForInvoice: vi.fn(), useInvoiceRevenue: () => state.revenue }));
vi.mock('../../leads/hooks/useLeads', () => ({ useLeadServiceRequests: vi.fn() }));

afterEach(() => {
  cleanup();
  state.deals = [];
});

describe('DealListPage', () => {
  it('shows readable account labels and table headings instead of internal identifiers', async () => {
    state.deals = [{
      dealId: 'deal-uuid',
      propertyId: 'property-uuid',
      sellerCustomerId: 'customer-uuid',
      leadId: 'lead-uuid',
      dealType: 'PROPERTY_SALE',
      dealStatus: 'NEW',
      expectedAmount: 125000,
      finalAmount: null,
      sellerCommissionPercent: 0,
      buyerCommissionPercent: 0,
      totalCommissionAmount: null,
      createdAt: '2026-10-01T00:00:00Z',
      closedAt: null,
      remarks: null,
      selectedBuyerCustomerId: null,
      dealSource: 'ADMIN_CREATED',
      exclusiveListing: false,
      buyerCommissionAmount: null,
      sellerCommissionAmount: null,
    }];

    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter><DealListPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('Asha Customer')).toBeTruthy();
    expect(screen.getByRole('heading', { name: 'Property transaction deals' })).toBeTruthy();
    expect(screen.getByText(/current Deal API supports property transactions/i)).toBeTruthy();
    expect(screen.getByText(/Service revenue is invoiced from completed billable Service Requests/i)).toBeTruthy();
    expect(screen.getByText('Lakeview Residence')).toBeTruthy();
    expect(screen.getByRole('columnheader', { name: 'Expected / final amount (currency unavailable)' })).toBeTruthy();
    expect(screen.getAllByText(/currency unavailable/i).length).toBeGreaterThan(0);
    expect(screen.getByRole('columnheader', { name: 'Lead' })).toBeTruthy();
    expect(screen.getByRole('link', { name: 'Property Sale' }).getAttribute('href')).toBe('/deals/deal-uuid');
    expect(screen.getByRole('link', { name: 'View lead' }).getAttribute('href')).toBe('/staff/leads/lead-uuid');
    expect(screen.queryByText('deal-uuid')).toBeNull();
    expect(screen.queryByText('customer-uuid')).toBeNull();
    expect(screen.queryByText('property-uuid')).toBeNull();
  });
});

describe('DealRevenueMetric', () => {
  it('displays invoice-backed deal revenue separated by currency', () => {
    render(<DealRevenueMetric />);

    expect(screen.getByText(/\$20\.00 USD/)).toBeTruthy();
    expect(screen.getByText(/30\.00/)).toBeTruthy();
    expect(screen.queryByText(/999/)).toBeNull();
  });
});

describe('DealDashboardPage', () => {
  it('keeps transaction outcome labels factual and does not aggregate currencies the API does not provide', async () => {
    state.deals = [{
      dealId: 'dashboard-deal', propertyId: 'property-uuid', sellerCustomerId: 'customer-uuid', leadId: null,
      dealType: 'PROPERTY_SALE', dealStatus: 'COMPLETED', expectedAmount: 125000, finalAmount: 150000,
      sellerCommissionPercent: 0, buyerCommissionPercent: 0, totalCommissionAmount: null,
      createdAt: '2026-10-01T00:00:00Z', closedAt: '2026-10-02T00:00:00Z', remarks: null,
      selectedBuyerCustomerId: null, dealSource: 'ADMIN_CREATED', exclusiveListing: false,
      buyerCommissionAmount: null, sellerCommissionAmount: null,
    }];
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter><DealDashboardPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('Completed Deals')).toBeTruthy();
    expect(screen.getByText('Cancelled Deals')).toBeTruthy();
    expect(screen.queryByText('Won Deals')).toBeNull();
    expect(screen.queryByText('Closed Transaction Value')).toBeNull();
    expect(screen.getByText(/Transaction currency is not provided by the current API/)).toBeTruthy();
  });
});
