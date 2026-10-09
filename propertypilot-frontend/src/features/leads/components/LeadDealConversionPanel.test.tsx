import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import LeadDealConversionPanel from './LeadDealConversionPanel';
import type { Lead } from '../types/lead';

const mocks = vi.hoisted(() => ({
  convert: vi.fn(async () => ({ dealId: 'new-deal', dealStatus: 'NEW' })),
  properties: vi.fn(async () => [{ propertyId: 'property-internal-uuid', customerId: 'customer-internal-uuid', title: 'Parkview Villa' }]),
  deals: [] as Array<{ dealId: string; dealType: string; dealStatus: string; expectedAmount: number; createdAt: string; remarks: string | null; propertyId: string | null; sellerCustomerId: string; leadId: string }>,
}));

vi.mock('../../deals/services/dealApi', () => ({ dealApi: { properties: mocks.properties } }));
vi.mock('../hooks/useLeads', () => ({
  useLeadDeals: () => ({ data: mocks.deals, isLoading: false, isError: false, refetch: vi.fn() }),
  useConvertLeadToDeal: () => ({ mutateAsync: mocks.convert, isPending: false, isError: false, error: null }),
}));

const lead: Lead = {
  leadId: 'lead-id', source: 'Customer', propertyType: null, location: null, status: 'READY_FOR_DEAL',
  budgetMin: null, budgetMax: null, notes: null, contactedAt: null, convertedAt: null,
  assignedAgentId: null, assignedAgentCode: null, customerId: 'customer-internal-uuid',
};

function renderPanel() {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(<QueryClientProvider client={client}><MemoryRouter><LeadDealConversionPanel lead={lead} /></MemoryRouter></QueryClientProvider>);
}

afterEach(() => { cleanup(); vi.clearAllMocks(); mocks.deals = []; });

describe('LeadDealConversionPanel', () => {
  it('converts a ready lead using a customer property and preserves readable labels', async () => {
    renderPanel();
    fireEvent.click(screen.getByRole('button', { name: 'Convert to deal' }));

    const propertySelect = await screen.findByRole('combobox', { name: 'Property' });
    fireEvent.mouseDown(propertySelect);
    fireEvent.click(await screen.findByRole('option', { name: 'Parkview Villa' }));
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Expected transaction amount (currency unavailable)' }), { target: { value: '250000' } });
    fireEvent.click(screen.getByRole('button', { name: 'Create deal' }));

    await waitFor(() => expect(mocks.convert).toHaveBeenCalledWith({
      dealType: 'PROPERTY_SALE', propertyId: 'property-internal-uuid', expectedAmount: 250000,
      sellerCommissionPercent: 0, buyerCommissionPercent: 0,
    }));
    expect(await screen.findByText(/Deal created and linked to this lead/)).toBeTruthy();
    expect(screen.queryByText(/property-internal-uuid|customer-internal-uuid/)).toBeNull();
  });

  it('requires an authorized reason before converting an already converted lead again', async () => {
    mocks.deals = [{ dealId: 'existing', dealType: 'PROPERTY_SALE', dealStatus: 'NEW', expectedAmount: 100000, createdAt: '2026-01-01T00:00:00Z', remarks: null, propertyId: null, sellerCustomerId: 'customer', leadId: 'lead-id' }];
    renderPanel();
    fireEvent.click(screen.getByRole('button', { name: 'Convert to deal' }));
    fireEvent.mouseDown(await screen.findByRole('combobox', { name: 'Property' }));
    fireEvent.click(await screen.findByRole('option', { name: 'Parkview Villa' }));
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Expected transaction amount (currency unavailable)' }), { target: { value: '250000' } });
    fireEvent.click(screen.getByRole('button', { name: 'Create deal' }));

    expect(await screen.findByText('The backend requires an authorized reason for another deal of this type.')).toBeTruthy();
    expect(mocks.convert).not.toHaveBeenCalled();
  });
});
