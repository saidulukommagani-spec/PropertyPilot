import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const state = vi.hoisted(() => ({
  propertyId: '00000000-0000-4000-8000-000000000001',
  quoteId: '00000000-0000-4000-8000-000000000002',
  serviceId: '00000000-0000-4000-8000-000000000003',
  properties: { data: [] as Array<{ propertyId: string; customerId: string; title: string; propertyType: string; status: string }>, isLoading: false, isError: false, error: null, isSuccess: true, refetch: vi.fn() },
  services: { data: [] as Array<{ serviceId: string; serviceCode: string; serviceName: string; category: string; requiresVisit: boolean; visitMandatory: boolean; remote: boolean }>, isLoading: false, isError: false, isSuccess: true, refetch: vi.fn() },
  serviceTypes: { data: [{ code: 'INSPECTION', label: 'Inspection' }], isLoading: false, isError: false, isSuccess: true, error: null, refetch: vi.fn() },
  mutateAsync: vi.fn(),
  estimateAsync: vi.fn(),
}));

const { propertyId, quoteId, serviceId } = state;
const createdRequest = {
  serviceRequestId: '00000000-0000-4000-8000-000000000004', propertyId, propertyTitle: 'Lakeview House', requestType: 'INSPECTION',
  serviceName: 'Document review', status: 'NEW', priority: 'MEDIUM', requestedAt: '2026-10-09T10:00:00Z', quoteId, serviceId,
};
state.properties.data = [{ propertyId, customerId: '00000000-0000-4000-8000-000000000005', title: 'Lakeview House', propertyType: 'House', status: 'ACTIVE' }];
state.services.data = [{ serviceId, serviceCode: 'DOC_SCAN', serviceName: 'Document review', category: 'DOCUMENTS', requiresVisit: false, visitMandatory: false, remote: true }];
const customerEstimate = { pricingEstimateId: quoteId, propertyId, serviceId, serviceName: 'Document review', subtotal: 1000, discountAmount: 100, taxAmount: 162, totalAmount: 1062, currencyCode: 'INR', status: 'ACTIVE', expiresAt: '2027-01-01T00:00:00Z', standardPrice: 1200, customerPrice: 1062, savings: 138, travelDistanceKm: 0, travelDistanceMode: 'ONE_WAY', lineItems: [{ lineType: 'FEE', description: 'Travel', amount: 0 }] };

vi.mock('../hooks/useCustomerPortal', async () => {
  const React = await vi.importActual<typeof import('react')>('react');
  return {
    useCustomerProperties: () => state.properties,
    useCreateCustomerEstimate: () => {
      const [mutation, setMutation] = React.useState<{ isPending: boolean; isError: boolean; error: unknown }>({ isPending: false, isError: false, error: null });
      return {
        ...mutation,
        reset: () => setMutation({ isPending: false, isError: false, error: null }),
        mutateAsync: async (...args: Parameters<typeof state.estimateAsync>) => {
          setMutation({ isPending: true, isError: false, error: null });
          try {
            const result = await state.estimateAsync(...args);
            setMutation({ isPending: false, isError: false, error: null });
            return result;
          } catch (error) {
            setMutation({ isPending: false, isError: true, error });
            throw error;
          }
        },
      };
    },
    useCustomerEligibleServiceTypes: () => state.serviceTypes,
    useCustomerEligibleServices: () => state.services,
    useCustomerEstimateRequirements: (_propertyId: string, serviceId: string) => ({
      data: serviceId ? { pricingModel: 'FIXED' as const } : undefined,
      isLoading: false,
      isError: false,
      isSuccess: Boolean(serviceId),
      error: null,
      refetch: vi.fn(),
    }),
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
  await user.click(screen.getByRole('combobox', { name: 'Service type' }));
  await user.click(await screen.findByRole('option', { name: 'Inspection' }));
  await user.click(screen.getByRole('combobox', { name: 'Service' }));
  await user.click(await screen.findByRole('option', { name: /Document review/ }));
  await screen.findByText('Your price estimate');
  await user.type(screen.getByRole('textbox', { name: 'Service details' }), 'Inspect the rooftop for possible leaks.');
}

afterEach(cleanup);
beforeEach(() => {
  state.properties.isLoading = false;
  state.properties.isError = false;
  state.properties.isSuccess = true;
  state.properties.error = null;
  state.services.isLoading = false;
  state.services.isError = false;
  state.services.isSuccess = true;
  state.services.data = [{ serviceId, serviceCode: 'DOC_SCAN', serviceName: 'Document review', category: 'DOCUMENTS', requiresVisit: false, visitMandatory: false, remote: true }];
  state.serviceTypes.isLoading = false;
  state.serviceTypes.isError = false;
  state.serviceTypes.isSuccess = true;
  state.serviceTypes.error = null;
  state.serviceTypes.data = [{ code: 'INSPECTION', label: 'Inspection' }];
  state.mutateAsync.mockReset();
  state.mutateAsync.mockResolvedValue(createdRequest);
  state.estimateAsync.mockReset();
  state.estimateAsync.mockResolvedValue(customerEstimate);
});

describe('CustomerRequestCreatePage', () => {
  it('preselects the property, calculates and accepts a server estimate, and shows success', async () => {
    const user = userEvent.setup();
    renderPage();
    expect(screen.getByRole('combobox', { name: 'Property' }).textContent).toContain('Lakeview House');
    expect(screen.getByText(/standard price, your price and savings/)).toBeTruthy();
    await fillRequiredFields(user);
    await user.click(screen.getByRole('button', { name: 'Accept estimate and submit request' }));

    await waitFor(() => expect(state.mutateAsync).toHaveBeenCalledWith(expect.objectContaining({ quoteId, propertyId, serviceId, serviceType: 'INSPECTION', priority: 'MEDIUM', description: 'Inspect the rooftop for possible leaks.' })));
    expect(state.estimateAsync).toHaveBeenCalledWith({ propertyId, serviceId });
    expect(await screen.findByRole('heading', { name: 'Service request submitted' })).toBeTruthy();
    expect(screen.getByRole('link', { name: 'View service request' }).getAttribute('href')).toBe(`/customer/service-requests/${createdRequest.serviceRequestId}`);
    expect(screen.queryByText(createdRequest.serviceRequestId)).toBeNull();
  }, 15000);

  it('validates required information and prevents duplicate submissions while pending', async () => {
    const user = userEvent.setup();
    renderPage();
    const submitButton = screen.getByRole('button', { name: 'Accept estimate and submit request' });
    expect(submitButton.hasAttribute('disabled')).toBe(true);
    await user.click(screen.getByRole('combobox', { name: 'Service type' }));
    await user.click(await screen.findByRole('option', { name: 'Inspection' }));
    await user.click(screen.getByRole('combobox', { name: 'Service' }));
    await user.click(await screen.findByRole('option', { name: /Document review/ }));
    await screen.findByText('Your price estimate');
    expect(screen.getByText(/Travel charge:.*no on-site visit required/)).toBeTruthy();
    await user.click(submitButton);
    expect(await screen.findByText('Add at least 10 characters describing the service needed.')).toBeTruthy();
    await user.type(screen.getByRole('textbox', { name: 'Service details' }), 'Inspect the rooftop for possible leaks.');

    let resolveCreate!: (value: typeof createdRequest) => void;
    state.mutateAsync.mockImplementation(() => new Promise((resolve) => { resolveCreate = resolve; }));
    await user.click(screen.getByRole('button', { name: 'Accept estimate and submit request' }));
    const pendingButton = await screen.findByRole('button', { name: 'Accepting estimate and submitting…' });
    expect(pendingButton.hasAttribute('disabled')).toBe(true);
    expect(state.mutateAsync).toHaveBeenCalledTimes(1);
    resolveCreate(createdRequest);
    expect(await screen.findByRole('heading', { name: 'Service request submitted' })).toBeTruthy();
  }, 15000);

  it('preserves entered form values after an API error', async () => {
    const user = userEvent.setup();
    state.mutateAsync.mockRejectedValue(new Error('Temporary submission failure'));
    renderPage();
    await fillRequiredFields(user);
    await user.click(screen.getByRole('button', { name: 'Accept estimate and submit request' }));

    expect(await screen.findByText('Temporary submission failure')).toBeTruthy();
    expect((screen.getByRole('textbox', { name: 'Service details' }) as HTMLInputElement).value).toBe('Inspect the rooftop for possible leaks.');
    expect(screen.getByRole('combobox', { name: 'Property' }).textContent).toContain('Lakeview House');
  }, 15000);

  it('shows the backend reason when estimate calculation is rejected', async () => {
    const user = userEvent.setup();
    state.estimateAsync.mockRejectedValue(Object.assign(new Error('Request failed with status code 400'), {
      response: { data: { status: 400, message: 'A positive service price must be configured before estimating' } },
    }));
    renderPage();
    await user.click(screen.getByRole('combobox', { name: 'Service type' }));
    await user.click(await screen.findByRole('option', { name: 'Inspection' }));
    await user.click(screen.getByRole('combobox', { name: 'Service' }));
    await user.click(await screen.findByRole('option', { name: /Document review/ }));

    expect(await screen.findByText(/This service does not have an active price yet/)).toBeTruthy();
    expect(screen.queryByText('Request failed with status code 400')).toBeNull();
  }, 15000);

  it('lets a customer retry a failed route estimate and then shows the distance and travel charge', async () => {
    const user = userEvent.setup();
    state.services.data[0].requiresVisit = true;
    const unavailableRoute = Object.assign(new Error('Request failed with status code 400'), {
      response: { data: { status: 400, message: 'Google Maps could not calculate the road distance right now. Operations will prepare the quote.' } },
    });
    state.estimateAsync.mockRejectedValueOnce(unavailableRoute).mockResolvedValueOnce({
      ...customerEstimate,
      travelDistanceKm: 12.345,
      travelDistanceMode: 'ROUND_TRIP',
      lineItems: [{ lineType: 'FEE', description: 'Travel', amount: 50 }],
    });
    renderPage();
    await user.click(screen.getByRole('combobox', { name: 'Service type' }));
    await user.click(await screen.findByRole('option', { name: 'Inspection' }));
    await user.click(screen.getByRole('combobox', { name: 'Service' }));
    await user.click(await screen.findByRole('option', { name: /Document review/ }));

    expect(await screen.findByText(/could not calculate the road distance right now/)).toBeTruthy();
    await user.click(screen.getByRole('button', { name: 'Retry estimate' }));

    expect(await screen.findByText('Your price estimate')).toBeTruthy();
    expect(screen.getByText(/Road distance from the LB Nagar office: 12.3 km one way\. Travel pricing: round trip\./)).toBeTruthy();
    expect(screen.getByText(/Travel charge:.*50\.00/)).toBeTruthy();
    expect(screen.getByText('Google Maps')).toBeTruthy();
    expect(state.estimateAsync).toHaveBeenCalledTimes(2);
  }, 15000);

  it('clears the selected type, service, and quote when the property changes', async () => {
    const user = userEvent.setup();
    const otherPropertyId = '00000000-0000-4000-8000-000000000006';
    state.properties.data.push({ propertyId: otherPropertyId, customerId: '00000000-0000-4000-8000-000000000005', title: 'Garden Flat', propertyType: 'Apartment', status: 'ACTIVE' });
    renderPage();
    await user.click(screen.getByRole('combobox', { name: 'Service type' }));
    await user.click(await screen.findByRole('option', { name: 'Inspection' }));
    await user.click(await screen.findByRole('combobox', { name: 'Service' }));
    await user.click(await screen.findByRole('option', { name: /Document review/ }));
    await screen.findByText('Your price estimate');

    await user.click(screen.getByRole('combobox', { name: 'Property' }));
    await user.click(await screen.findByRole('option', { name: /Garden Flat/ }));

    expect(screen.getByRole('combobox', { name: 'Service type' }).textContent).not.toContain('Inspection');
    expect(screen.getByRole('combobox', { name: 'Service' }).textContent).not.toContain('Document review');
    expect(screen.queryByText('Your price estimate')).toBeNull();
    state.properties.data.pop();
  }, 15000);

  it('shows an empty eligible catalogue and lets the customer retry a catalogue error', async () => {
    state.serviceTypes.data = [];
    renderPage();
    expect(await screen.findByText(/No services are currently configured for this property/)).toBeTruthy();
    expect(screen.queryByText('Choose a service type.')).toBeNull();
    cleanup();

    const retry = vi.fn();
    state.serviceTypes.isError = true;
    state.serviceTypes.error = new Error('Catalogue unavailable');
    state.serviceTypes.refetch = retry;
    const user = userEvent.setup();
    renderPage();
    expect(await screen.findByText('Catalogue unavailable')).toBeTruthy();
    await user.click(screen.getByRole('button', { name: 'Retry' }));
    expect(retry).toHaveBeenCalledOnce();
  });
});
