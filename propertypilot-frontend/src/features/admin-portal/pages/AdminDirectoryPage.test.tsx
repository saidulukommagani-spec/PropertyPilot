import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, within, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import AdminDirectoryPage from './AdminDirectoryPage';
import { apiClient } from '../../../api/client';

const state = vi.hoisted(() => ({ rows: [] as Array<{ id: string; title: string; primary: string; secondary: string; status: string; type: string; date: string; amount: string; detailPath: string; relationId: string; customerId: string; propertyId: string }> }));

vi.mock('../hooks/useAdminPortal', () => ({
  adminPortalKeys: { list: (resource: string) => ['admin-portal', resource] },
  useAdminDirectory: () => ({ data: state.rows, isLoading: false, isError: false, isFetching: false, refetch: vi.fn() }),
}));
vi.mock('../../../api/client', () => ({ apiClient: { get: vi.fn().mockResolvedValue({ data: [] }), post: vi.fn().mockResolvedValue({ data: {} }), put: vi.fn().mockResolvedValue({ data: {} }), patch: vi.fn() } }));

afterEach(() => {
  cleanup();
  state.rows = [];
});

describe('AdminDirectoryPage', () => {
  it('shows property names and business details without rendering internal identifiers', () => {
    state.rows = [{ id: 'property-uuid', title: 'Lakeview Residence', primary: 'Lakeview Residence', secondary: 'Asha Owner', status: 'ACTIVE', type: 'VILLA', date: '2026-10-01', amount: '', detailPath: '/staff/properties/property-uuid', relationId: 'customer-uuid', customerId: 'customer-uuid', propertyId: 'property-uuid' }];
    const client = new QueryClient({ defaultOptions: { queries: { retry: false }, mutations: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/staff/properties']}><Routes><Route path="/staff/:resource" element={<AdminDirectoryPage />} /></Routes></MemoryRouter></QueryClientProvider>);

    expect(screen.getByText('Lakeview Residence')).toBeTruthy();
    expect(screen.getByText('Asha Owner')).toBeTruthy();
    expect(screen.getByRole('columnheader', { name: 'Record' })).toBeTruthy();
    expect(screen.queryByText('property-uuid')).toBeNull();
    expect(screen.queryByText('customer-uuid')).toBeNull();
  });

  it('lets an administrator add a service with catalogue eligibility through the admin API', async () => {
    const user = userEvent.setup();
    const client = new QueryClient({ defaultOptions: { queries: { retry: false }, mutations: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/staff/services']}><Routes><Route path="/staff/:resource" element={<AdminDirectoryPage />} /></Routes></MemoryRouter></QueryClientProvider>);

    await user.click(screen.getByRole('button', { name: 'Add service' }));
    const dialog = screen.getByRole('dialog', { name: 'Add catalog service' });
    fireEvent.change(within(dialog).getByRole('textbox', { name: 'Service code' }), { target: { value: 'PROPERTY_DOCUMENT_REVIEW' } });
    fireEvent.change(within(dialog).getByRole('textbox', { name: 'Service type code' }), { target: { value: 'DOCUMENTS' } });
    fireEvent.change(within(dialog).getByRole('textbox', { name: 'Catalogue category' }), { target: { value: 'Document Services' } });
    fireEvent.change(within(dialog).getByRole('textbox', { name: 'Customer-visible service name' }), { target: { value: 'Property document review' } });
    await user.click(within(dialog).getAllByText('Plot')[0]);
    await user.click(within(dialog).getByRole('button', { name: 'Add service' }));

    await waitFor(() => expect(apiClient.post).toHaveBeenCalledWith('/api/services/admin', expect.objectContaining({ serviceCode: 'PROPERTY_DOCUMENT_REVIEW', category: 'DOCUMENTS', catalogGroup: 'Document Services', eligiblePropertyTypes: ['PLOT'], activeFlag: false })));
    await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
  });
});
