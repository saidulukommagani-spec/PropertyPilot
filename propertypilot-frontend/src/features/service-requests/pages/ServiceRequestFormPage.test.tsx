import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ServiceRequestFormPage from './ServiceRequestFormPage';

const mocks = vi.hoisted(() => ({
  create: vi.fn(async () => ({ serviceRequestId: 'request-created' })),
  listQuotes: vi.fn(async () => [{
    quoteId: 'quote-internal-uuid', customerId: 'customer-internal-uuid', customerName: 'Asha Rao', customerEmail: 'asha@example.com',
    propertyId: 'property-internal-uuid', propertyName: 'Lakeview Apartment', serviceId: 'service-internal-uuid', serviceName: 'Inspection',
    status: 'ACCEPTED', totalAmount: 2500, currencyCode: 'INR', expiresAt: '2030-01-01T00:00:00Z',
  }]),
}));

vi.mock('../../../api/serviceRequestApi', () => ({ serviceRequestApi: { listQuotes: mocks.listQuotes } }));
vi.mock('../hooks/useServiceRequests', () => ({
  useCreateServiceRequest: () => ({ mutateAsync: mocks.create, error: null, isPending: false }),
  useEditServiceRequest: () => ({ mutateAsync: vi.fn(), error: null, isPending: false }),
}));

function renderPage() {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/service-requests/new']}><Routes>
    <Route path="/service-requests/new" element={<ServiceRequestFormPage />} />
    <Route path="/service-requests/request-created" element={<div>Request created</div>} />
  </Routes></MemoryRouter></QueryClientProvider>);
}

afterEach(() => { cleanup(); vi.clearAllMocks(); });

describe('ServiceRequestFormPage', () => {
  it('creates a request from an accepted quote and displays linked business labels instead of IDs', async () => {
    renderPage();

    const quoteSelect = await screen.findByRole('combobox', { name: 'Accepted quote' });
    fireEvent.mouseDown(quoteSelect);
    fireEvent.click(await screen.findByRole('option', { name: /Inspection · Asha Rao · Lakeview Apartment/ }));
    fireEvent.change(screen.getByRole('textbox', { name: 'Description' }), { target: { value: 'Annual property inspection requested.' } });
    fireEvent.click(screen.getByRole('button', { name: 'Create request' }));

    await waitFor(() => expect(mocks.create).toHaveBeenCalledWith({
      quoteId: 'quote-internal-uuid', priority: 'MEDIUM', description: 'Annual property inspection requested.', preferredDate: undefined,
    }));
    expect(screen.getByText('Request created')).toBeTruthy();
    expect(screen.queryByText(/customer-internal-uuid|property-internal-uuid|service-internal-uuid/)).toBeNull();
  });
});
