import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import AdminDirectoryPage from './AdminDirectoryPage';

const state = vi.hoisted(() => ({ rows: [] as Array<{ id: string; title: string; primary: string; secondary: string; status: string; type: string; date: string; amount: string; detailPath: string; relationId: string; customerId: string; propertyId: string }> }));

vi.mock('../hooks/useAdminPortal', () => ({
  adminPortalKeys: { list: (resource: string) => ['admin-portal', resource] },
  useAdminDirectory: () => ({ data: state.rows, isLoading: false, isError: false, isFetching: false, refetch: vi.fn() }),
}));
vi.mock('../../../api/client', () => ({ apiClient: { patch: vi.fn() } }));

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
});
