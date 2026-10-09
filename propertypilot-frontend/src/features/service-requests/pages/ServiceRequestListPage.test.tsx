import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ServiceRequestListPage from './ServiceRequestListPage';

const state = vi.hoisted(() => ({ data: [] as Array<Record<string, string>>, loading: false }));
vi.mock('../hooks/useServiceRequests', () => ({ useServiceRequests: () => ({ data: state.data, isLoading: state.loading, isError: false, refetch: vi.fn(), isFetching: false }) }));
const request = (id: string, customer: string, status: string) => ({ serviceRequestId:id, customerName:customer, propertyTitle:'Lakeview Apartment', requestType:'Inspection', status, priority:'HIGH', requestedAt:'2026-01-02T10:00:00Z' });
function renderPage(path = '/service-requests') { const client = new QueryClient(); return render(<QueryClientProvider client={client}><MemoryRouter initialEntries={[path]}><ServiceRequestListPage /></MemoryRouter></QueryClientProvider>); }
afterEach(() => { cleanup(); state.data=[]; state.loading=false; });

describe('ServiceRequestListPage', () => {
  it('shows request rows and filters them by search text', () => {
    state.data=[request('a12345678','Asha Rao','NEW'),request('b12345678','Mina Das','ASSIGNED')];
    renderPage();
    expect(screen.getByText('Asha Rao')).toBeTruthy(); expect(screen.getByText('Mina Das')).toBeTruthy();
    fireEvent.change(screen.getByPlaceholderText(/Search request/),{target:{value:'Mina'}});
    expect(screen.queryByText('Asha Rao')).toBeNull(); expect(screen.getByText('Mina Das')).toBeTruthy();
  });
  it('shows a useful empty state when no requests exist', () => {
    renderPage(); expect(screen.getByText('No service requests yet')).toBeTruthy();
  });
  it('opens directly on a supported status filter from dashboard queue links', () => {
    state.data=[request('failed123','Asha Rao','FAILED'),request('new1234567','Mina Das','NEW')];
    renderPage('/service-requests?status=FAILED');
    expect(screen.getByText('Asha Rao')).toBeTruthy();
    expect(screen.queryByText('Mina Das')).toBeNull();
  });
  it('keeps staff list actions inside the staff service-request routes', () => {
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/staff/service-requests']}><Routes>
      <Route path="/staff/service-requests" element={<ServiceRequestListPage />} />
      <Route path="/staff/service-requests/new" element={<div>Staff request form</div>} />
      <Route path="/service-requests/new" element={<div>Customer request form</div>} />
    </Routes></MemoryRouter></QueryClientProvider>);
    fireEvent.click(screen.getByRole('button', { name: 'Create request' }));
    expect(screen.getByText('Staff request form')).toBeTruthy();
  });
});
