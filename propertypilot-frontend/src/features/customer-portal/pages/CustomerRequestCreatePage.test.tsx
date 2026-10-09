import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const state = vi.hoisted(() => ({
  propertyId: '00000000-0000-4000-8000-000000000001',
  quoteId: '00000000-0000-4000-8000-000000000002',
  serviceId: '00000000-0000-4000-8000-000000000003',
  properties: { data: [] as Array<{ propertyId: string; customerId: string; title: string; propertyType: string; status: string }>, isLoading: false, isError: false, error: null, isSuccess: true, refetch: vi.fn() },
  quotes: { data: [] as Array<{ quoteId: string; propertyId: string; propertyName: string; serviceId: string; serviceName: string; status: string; totalAmount: number; currencyCode: string; expiresAt: string; acceptedAt: string }>, isLoading: false, isError: false, error: null, refetch: vi.fn() },
  services: { data: [] as Array<{ serviceId: string; serviceCode: string; serviceName: string; category: string; requiresVisit: boolean; visitMandatory: boolean; remote: boolean }>, isLoading: false, isError: false, isSuccess: true, refetch: vi.fn() },
  mutateAsync: vi.fn(),
}));

const { propertyId, quoteId, serviceId } = state;
const createdRequest = {
  serviceRequestId: '00000000-0000-4000-8000-000000000004', propertyId, propertyTitle: 'Lakeview House', requestType: 'INSPECTION',
  serviceName: 'Property inspection', status: 'NEW', priority: 'MEDIUM', requestedAt: '2026-10-09T10:00:00Z', quoteId, serviceId,
};
state.properties.data = [{ propertyId, customerId: '00000000-0000-4000-8000-000000000005', title: 'Lakeview House', propertyType: 'House', status: 'ACTIVE' }];
state.quotes.data = [{ quoteId, propertyId, propertyName: 'Lakeview House', serviceId, serviceName: 'Property inspection', status: 'ACCEPTED', totalAmount: 1200, currencyCode: 'INR', expiresAt: '2027-01-01T00:00:00Z', acceptedAt: '2026-10-01T10:00:00Z' }];
state.services.data = [{ serviceId, serviceCode: 'INSPECT', serviceName: 'Property inspection', category: 'Inspection', requiresVisit: true, visitMandatory: true, remote: false }];

vi.mock('../hooks/useCustomerPortal', async () => {
  const React = await vi.importActual<typeof import('react')>('react');
  return {
    useCustomerProperties: () => state.properties,
    useCustomerQuotes: () => state.quotes,
    useCustomerServices: () => state.services,
    useCreateCustomerRequest: () => {
      const [mutation, setMutation] = React.useState<{ isPending: boolean; isError: boolean; error: Error | null }>({ isPending: false, isError: false, error: null });
      return {
        ...mutation,
        mutateAsync: async (...args: Parameters<typeof state.mutateAsync>) => {
          setMutation({ isPending: true, isError: false, error: null });
          try {
            const result = await state.mutateAsync(...args);
            setMutation({ isPending: false, isError: false, error: null });
            return result;
          } catch (error) {
            setMutation({ isPending: false, isError: true, error: error instanceof Error ? error : new Error('Request failed') });
            throw error;
          }
        },
      };
    },
  };
});

import CustomerRequestCreatePage from './CustomerRequestCreatePage';

function renderPage() {
  return render(<MemoryRouter initialEntries={[`/customer/service-requests/new?propertyId=${propertyId}`]}><Routes>
    <Route path="/customer/service-requests/new" element={<CustomerRequestCreatePage />} />
    <Route path="/customer/service-requests/:id" element={<div>Created request detail</div>} />
    <Route path="/customer/service-requests" element={<div>Request list</div>} />
  </Routes></MemoryRouter>);
}

async function fillRequiredFields(user: ReturnType<typeof userEvent.setup>) {
  await user.click(screen.getByRole('combobox', { name: 'Service' }));
  await user.click(await screen.findByRole('option', { name: /Property inspection/ }));
  await user.click(screen.getByRole('combobox', { name: 'Accepted quote' }));
  await user.click(await screen.findByRole('option', { name: /Property inspection/ }));
  await user.type(screen.getByRole('textbox', { name: 'Service details' }), 'Inspect the rooftop for possible leaks.');
}

afterEach(cleanup);
beforeEach(() => {
  state.properties.isLoading = false;
  state.properties.isError = false;
  state.properties.isSuccess = true;
  state.properties.error = null;
  state.quotes.isLoading = false;
  state.quotes.isError = false;
  state.quotes.error = null;
  state.services.isLoading = false;
  state.services.isError = false;
  state.services.isSuccess = true;
  state.mutateAsync.mockReset();
  state.mutateAsync.mockResolvedValue(createdRequest);
});

describe('CustomerRequestCreatePage', () => {
  it('preselects the property, matches service to its accepted quote, and shows server-confirmed success', async () => {
    const user = userEvent.setup();
    renderPage();
    expect(screen.getByRole('combobox', { name: 'Property' }).textContent).toContain('Lakeview House');
    expect(screen.getByText(/A subscription is not required/)).toBeTruthy();
    await fillRequiredFields(user);
    await user.click(screen.getByRole('button', { name: 'Submit service request' }));

    await waitFor(() => expect(state.mutateAsync).toHaveBeenCalledWith(expect.objectContaining({ quoteId, priority: 'MEDIUM', description: 'Inspect the rooftop for possible leaks.' })));
    expect(await screen.findByRole('heading', { name: 'Service request submitted' })).toBeTruthy();
    expect(screen.getByRole('link', { name: 'View service request' }).getAttribute('href')).toBe(`/customer/service-requests/${createdRequest.serviceRequestId}`);
    expect(screen.queryByText(createdRequest.serviceRequestId)).toBeNull();
  }, 15000);

  it('validates required information and prevents duplicate submissions while pending', async () => {
    const user = userEvent.setup();
    renderPage();
    const submitButton = screen.getByRole('button', { name: 'Submit service request' });
    expect(submitButton.hasAttribute('disabled')).toBe(true);
    await user.click(screen.getByRole('combobox', { name: 'Service' }));
    await user.click(await screen.findByRole('option', { name: /Property inspection/ }));
    await user.click(screen.getByRole('combobox', { name: 'Accepted quote' }));
    await user.click(await screen.findByRole('option', { name: /Property inspection/ }));
    await user.click(submitButton);
    expect(await screen.findByText('Add at least 10 characters describing the service needed.')).toBeTruthy();
    await user.type(screen.getByRole('textbox', { name: 'Service details' }), 'Inspect the rooftop for possible leaks.');

    let resolveCreate!: (value: typeof createdRequest) => void;
    state.mutateAsync.mockImplementation(() => new Promise((resolve) => { resolveCreate = resolve; }));
    await user.click(screen.getByRole('button', { name: 'Submit service request' }));
    const pendingButton = await screen.findByRole('button', { name: 'Submitting request…' });
    expect(pendingButton.hasAttribute('disabled')).toBe(true);
    expect(state.mutateAsync).toHaveBeenCalledTimes(1);
    resolveCreate(createdRequest);
    expect(await screen.findByRole('heading', { name: 'Service request submitted' })).toBeTruthy();
  });

  it('preserves entered form values after an API error', async () => {
    const user = userEvent.setup();
    state.mutateAsync.mockRejectedValue(new Error('Temporary submission failure'));
    renderPage();
    await fillRequiredFields(user);
    await user.click(screen.getByRole('button', { name: 'Submit service request' }));

    expect(await screen.findByText('Temporary submission failure')).toBeTruthy();
    expect((screen.getByRole('textbox', { name: 'Service details' }) as HTMLInputElement).value).toBe('Inspect the rooftop for possible leaks.');
    expect(screen.getByRole('combobox', { name: 'Property' }).textContent).toContain('Lakeview House');
  });
});
