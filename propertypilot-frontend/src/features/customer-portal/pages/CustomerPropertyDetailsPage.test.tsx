import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const state = vi.hoisted(() => ({
  property: { data: { propertyId: 'property-1', customerId: 'customer-1', title: 'Lakeview House', propertyType: 'House', listingStatus: 'OWNER_OCCUPIED', status: 'ACTIVE' }, isLoading: false, isError: false, error: null, refetch: vi.fn() },
  requests: { data: [{ serviceRequestId: 'request-private-id', propertyId: 'property-1', propertyTitle: 'Lakeview House', requestType: 'INSPECTION', serviceName: 'Property inspection', status: 'IN_PROGRESS', priority: 'MEDIUM', requestedAt: '2026-10-08T10:00:00Z' }], isLoading: false, isError: false, error: null, refetch: vi.fn() },
  visits: { data: [], isLoading: false, isError: false, error: null, refetch: vi.fn() },
  documents: vi.fn(async () => []),
  requestEvidence: vi.fn(async () => []),
  archive: vi.fn(),
}));

vi.mock('../hooks/useCustomerPortal', () => ({
  customerKeys: { properties: ['customer-portal', 'properties'], property: (id: string) => ['customer-portal', 'property', id], requests: ['customer-portal', 'requests'], visits: ['customer-portal', 'visits'], documents: ['customer-portal', 'documents'], evidenceForRequest: (id: string) => ['customer-portal', 'request-evidence', id] },
  useCustomerProperty: () => state.property,
  useCustomerRequests: () => state.requests,
  useCustomerVisits: () => state.visits,
}));
vi.mock('../services/CustomerPortalApi', () => ({ CustomerPortalApi: { documents: state.documents, requestEvidence: state.requestEvidence } }));
vi.mock('../../../api/properties', () => ({ propertiesApi: { archive: state.archive } }));

import CustomerPropertyDetailsPage from './CustomerPropertyDetailsPage';

function renderPage() {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/customer/properties/property-1']}><Routes><Route path="/customer/properties/:id" element={<CustomerPropertyDetailsPage />} /></Routes></MemoryRouter></QueryClientProvider>);
}

afterEach(cleanup);
beforeEach(() => {
  state.property.data = { propertyId: 'property-1', customerId: 'customer-1', title: 'Lakeview House', propertyType: 'House', listingStatus: 'OWNER_OCCUPIED', status: 'ACTIVE' };
  state.property.isLoading = false;
  state.property.isError = false;
  state.property.error = null;
  state.requests.data = [{ serviceRequestId: 'request-private-id', propertyId: 'property-1', propertyTitle: 'Lakeview House', requestType: 'INSPECTION', serviceName: 'Property inspection', status: 'IN_PROGRESS', priority: 'MEDIUM', requestedAt: '2026-10-08T10:00:00Z' }];
  state.requests.isLoading = false;
  state.requests.isError = false;
  state.visits.data = [];
  state.visits.isLoading = false;
  state.visits.isError = false;
  state.documents.mockResolvedValue([]);
  state.requestEvidence.mockResolvedValue([]);
});

describe('CustomerPropertyDetailsPage', () => {
  it('shows linked service activity, carries the property into request creation, and omits empty sections', async () => {
    renderPage();

    expect(await screen.findByRole('heading', { name: 'Lakeview House' })).toBeTruthy();
    expect(screen.getByText('Property inspection')).toBeTruthy();
    expect(screen.getByRole('link', { name: 'View request' }).getAttribute('href')).toBe('/customer/service-requests/request-private-id');
    expect(screen.getByRole('link', { name: 'Request a service for this property' }).getAttribute('href')).toBe('/customer/service-requests/new?propertyId=property-1');
    expect(screen.queryByRole('heading', { name: 'Property documents' })).toBeNull();
    expect(screen.queryByRole('heading', { name: 'Visit history' })).toBeNull();
    expect(screen.queryByRole('heading', { name: 'Service evidence' })).toBeNull();
    expect(screen.queryByText('request-private-id')).toBeNull();
  });

  it('provides a retry state when property access fails', () => {
    state.property.data = null as never;
    state.property.isError = true;
    state.property.error = new Error('Property unavailable');
    renderPage();

    expect(screen.getByText('Property unavailable')).toBeTruthy();
    expect(screen.getByRole('link', { name: 'Back to properties' })).toBeTruthy();
  });
});
