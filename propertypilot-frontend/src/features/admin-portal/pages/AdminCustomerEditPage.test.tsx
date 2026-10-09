import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import AdminCustomerEditPage from './AdminCustomerEditPage';

const api = vi.hoisted(() => ({ get: vi.fn(), put: vi.fn() }));
vi.mock('../../../api/client', () => ({ apiClient: api }));

afterEach(() => { cleanup(); api.get.mockReset(); api.put.mockReset(); });

describe('AdminCustomerEditPage', () => {
  it('updates the linked user contact through the existing admin user endpoint', async () => {
    api.get.mockResolvedValue({ data: { customerId: 'customer-1', userId: 'user-42', fullName: 'Alex Owner', email: 'alex@example.com', mobileNumber: '5551112222', status: 'ACTIVE', customerType: 'INDIVIDUAL' } });
    api.put.mockResolvedValue({ data: {} });
    const client = new QueryClient({ defaultOptions: { queries: { retry: false }, mutations: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/staff/customers/customer-1/edit']}><Routes><Route path="/staff/customers/:id/edit" element={<AdminCustomerEditPage />} /><Route path="/staff/customers/:id" element={<div>Customer details</div>} /></Routes></MemoryRouter></QueryClientProvider>);

    const name = await screen.findByLabelText(/Full name/);
    fireEvent.change(name, { target: { value: 'Alex Updated' } });
    fireEvent.change(screen.getByLabelText(/Email address/), { target: { value: 'ALEX2@example.com' } });
    fireEvent.change(screen.getByLabelText(/Phone number/), { target: { value: '5559990000' } });
    fireEvent.click(screen.getByRole('button', { name: 'Save changes' }));

    await waitFor(() => expect(api.put).toHaveBeenCalledWith('/api/v1/users/user-42', { fullName: 'Alex Updated', email: 'alex2@example.com', mobileNumber: '5559990000' }));
    expect(await screen.findByText('Customer details')).toBeTruthy();
  });
});
