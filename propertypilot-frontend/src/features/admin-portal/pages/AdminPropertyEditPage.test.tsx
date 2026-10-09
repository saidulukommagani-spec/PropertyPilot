import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import AdminPropertyEditPage from './AdminPropertyEditPage';

const api = vi.hoisted(() => ({ get: vi.fn(), put: vi.fn() }));
vi.mock('../../../api/client', () => ({ apiClient: api }));

afterEach(() => { cleanup(); api.get.mockReset(); api.put.mockReset(); });

describe('AdminPropertyEditPage', () => {
  it('updates property details through the existing API while preserving the owner', async () => {
    api.get.mockResolvedValue({ data: {
      propertyId: 'property-1', customerId: 'customer-42', title: 'Lakeview Home', propertyType: 'VILLA',
      listingStatus: 'OWNER_OCCUPIED', price: 125000, status: 'ACTIVE',
    } });
    api.put.mockResolvedValue({ data: {} });
    const client = new QueryClient({ defaultOptions: { queries: { retry: false }, mutations: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/staff/properties/property-1/edit']}><Routes>
      <Route path="/staff/properties/:id/edit" element={<AdminPropertyEditPage />} />
      <Route path="/staff/properties/:id" element={<div>Property details</div>} />
    </Routes></MemoryRouter></QueryClientProvider>);

    const title = await screen.findByRole('textbox', { name: /Property name/ });
    fireEvent.change(title, { target: { value: 'Lakeview Residence' } });
    fireEvent.mouseDown(screen.getByRole('combobox', { name: 'Property status' }));
    fireEvent.click(await screen.findByRole('option', { name: 'INACTIVE' }));
    fireEvent.click(screen.getByRole('button', { name: 'Save changes' }));

    await waitFor(() => expect(api.put).toHaveBeenCalledWith('/api/v1/properties/property-1', {
      title: 'Lakeview Residence', propertyType: 'VILLA', listingStatus: 'OWNER_OCCUPIED', price: 125000,
      status: 'INACTIVE', customerId: 'customer-42',
    }));
    expect(await screen.findByText('Property details')).toBeTruthy();
  });
});
