import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { QuoteResponse } from '../types/quote';
import { QuoteCreatePage, QuoteDashboardPage, QuoteListPage } from './QuotePages';

const state = vi.hoisted(() => ({ data: [] as QuoteResponse[], calculate: vi.fn() }));

vi.mock('../../auth/AuthContext', () => ({ useAuth: () => ({ user: {} }) }));
vi.mock('../../../api/auth', () => ({ getUserRoles: () => ['ADMIN'] }));
vi.mock('../hooks/useQuotes', () => ({
  useQuotes: () => ({ data: state.data, isLoading: false, isError: false, refetch: vi.fn() }),
  useQuote: vi.fn(),
  useAcceptQuote: vi.fn(),
  useCalculateQuote: () => ({ mutateAsync: state.calculate, isPending: false, error: null }),
  useRejectQuote: vi.fn(),
}));
vi.mock('../services/quoteApi', () => ({
  quoteApi: {
    customers: async () => [{ customerId: 'customer-a', fullName: 'Asha Customer', email: 'asha@example.test' }, { customerId: 'customer-b', fullName: 'Ben Customer', email: 'ben@example.test' }],
    properties: async () => [{ propertyId: 'property-a', customerId: 'customer-a', title: 'Asha Home' }, { propertyId: 'property-b', customerId: 'customer-b', title: 'Ben Home' }],
    services: async () => [{ serviceId: 'service-a', serviceName: 'Inspection' }],
  },
}));

afterEach(() => {
  cleanup();
  state.data = [];
});

describe('QuoteListPage', () => {
  it('shows customer and property names without rendering their UUIDs', () => {
    state.data = [{
      quoteId: 'quote-internal-uuid',
      customerId: 'customer-internal-uuid',
      propertyId: 'property-internal-uuid',
      serviceId: 'service-internal-uuid',
      customerName: 'Asha Customer',
      customerEmail: 'asha@example.test',
      propertyName: 'Lakeview Residence',
      serviceName: 'Property inspection',
      status: 'ACTIVE',
      subtotal: 1000,
      discountAmount: 0,
      taxAmount: 0,
      totalAmount: 1000,
      currencyCode: 'INR',
      standardPrice: null,
      savings: null,
      expiresAt: '2026-10-20T00:00:00Z',
      acceptedAt: null,
      rejectedAt: null,
      rejectionReason: null,
      lineItems: [],
      createdAt: '2026-10-01T00:00:00Z',
    }];

    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter><QuoteListPage /></MemoryRouter></QueryClientProvider>);

    expect(screen.getByText('Asha Customer')).toBeTruthy();
    expect(screen.getByText('Lakeview Residence')).toBeTruthy();
    expect(screen.getByText('Property inspection')).toBeTruthy();
    expect(screen.queryByText('customer-internal-uuid')).toBeNull();
    expect(screen.queryByText('property-internal-uuid')).toBeNull();
    expect(screen.queryByText('quote-internal-uuid')).toBeNull();
  });
});

describe('QuoteDashboardPage', () => {
  it('keeps financial totals separate by currency', () => {
    const base: QuoteResponse = {
      quoteId: 'quote-one', customerId: 'customer-one', propertyId: null, serviceId: 'service-one', serviceName: 'Inspection',
      status: 'ACCEPTED', subtotal: 100, discountAmount: 0, taxAmount: 0, totalAmount: 100, currencyCode: 'INR',
      standardPrice: null, savings: null, expiresAt: '2026-10-20T00:00:00Z', acceptedAt: null, rejectedAt: null,
      rejectionReason: null, lineItems: [], createdAt: '2026-10-01T00:00:00Z',
    };
    state.data = [base, { ...base, quoteId: 'quote-two', customerId: 'customer-two', totalAmount: 200, currencyCode: 'USD' }];
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter><QuoteDashboardPage /></MemoryRouter></QueryClientProvider>);

    expect(screen.getByText('Total Quoted Value · INR')).toBeTruthy();
    expect(screen.getByText('Total Quoted Value · USD')).toBeTruthy();
    expect(screen.getByText('Approved Value · INR')).toBeTruthy();
    expect(screen.getByText('Approved Value · USD')).toBeTruthy();
    expect(screen.queryByText('Total Quoted Value')).toBeNull();
  });
});

describe('QuoteCreatePage', () => {
  it('clears the selected property when the customer changes', async () => {
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter><QuoteCreatePage /></MemoryRouter></QueryClientProvider>);

    state.calculate.mockClear();
    const customer = await screen.findByRole('combobox', { name: 'Customer' });
    fireEvent.mouseDown(customer);
    fireEvent.click(await screen.findByRole('option', { name: /Asha Customer/ }));
    const property = screen.getByRole('combobox', { name: 'Property' });
    fireEvent.mouseDown(property);
    fireEvent.click(await screen.findByRole('option', { name: 'Asha Home' }));
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Service' }));
    fireEvent.click(await screen.findByRole('option', { name: 'Inspection' }));

    fireEvent.mouseDown(customer);
    fireEvent.click(await screen.findByRole('option', { name: /Ben Customer/ }));
    fireEvent.click(screen.getByRole('button', { name: 'Calculate and Create Quote' }));

    await waitFor(() => expect(state.calculate).not.toHaveBeenCalled());
  });
});
