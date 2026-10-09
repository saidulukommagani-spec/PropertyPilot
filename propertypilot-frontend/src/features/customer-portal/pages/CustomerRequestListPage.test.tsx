import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

const state = vi.hoisted(() => ({ query: { data: [
  { serviceRequestId: 'opaque-request-1', propertyId: 'property-1', propertyTitle: 'Lakeview House', requestType: 'INSPECTION', serviceName: 'Property inspection', status: 'IN_PROGRESS', priority: 'MEDIUM', requestedAt: '2026-10-08T10:00:00Z' },
  { serviceRequestId: 'opaque-request-2', propertyId: 'property-2', propertyTitle: 'Garden Flat', requestType: 'LEGAL', serviceName: 'Legal document review', status: 'COMPLETED', priority: 'HIGH', requestedAt: '2026-10-07T10:00:00Z' },
], isLoading: false, isError: false, error: null, refetch: vi.fn() } }));

vi.mock('../hooks/useCustomerPortal', () => ({ useCustomerRequests: () => state.query }));

import CustomerRequestListPage from './CustomerRequestListPage';

afterEach(cleanup);
beforeEach(() => {
  state.query.isLoading = false;
  state.query.isError = false;
  state.query.error = null;
  state.query.data = [
    { serviceRequestId: 'opaque-request-1', propertyId: 'property-1', propertyTitle: 'Lakeview House', requestType: 'INSPECTION', serviceName: 'Property inspection', status: 'IN_PROGRESS', priority: 'MEDIUM', requestedAt: '2026-10-08T10:00:00Z' },
    { serviceRequestId: 'opaque-request-2', propertyId: 'property-2', propertyTitle: 'Garden Flat', requestType: 'LEGAL', serviceName: 'Legal document review', status: 'COMPLETED', priority: 'HIGH', requestedAt: '2026-10-07T10:00:00Z' },
  ];
});

describe('CustomerRequestListPage', () => {
  it('shows service/property context and readable status without printing opaque identifiers', () => {
    render(<MemoryRouter><CustomerRequestListPage /></MemoryRouter>);

    expect(screen.getByText('Property inspection')).toBeTruthy();
    expect(screen.getByText('Lakeview House')).toBeTruthy();
    expect(screen.getByText('In Progress')).toBeTruthy();
    expect(screen.getByRole('link', { name: /Property inspection for Lakeview House/ }).getAttribute('href')).toBe('/customer/service-requests/opaque-request-1');
    expect(screen.queryByText('opaque-request-1')).toBeNull();
  });

  it('filters by service, property, and status', () => {
    render(<MemoryRouter><CustomerRequestListPage /></MemoryRouter>);
    fireEvent.change(screen.getByRole('textbox', { name: 'Search requests' }), { target: { value: 'Garden' } });
    expect(screen.getByText('Legal document review')).toBeTruthy();
    expect(screen.queryByText('Property inspection')).toBeNull();
  });

  it('shows loading and retryable error states', () => {
    state.query.isLoading = true;
    const { rerender } = render(<MemoryRouter><CustomerRequestListPage /></MemoryRouter>);
    expect(screen.getByLabelText('Loading service requests')).toBeTruthy();

    state.query.isLoading = false;
    state.query.isError = true;
    state.query.error = new Error('Requests unavailable');
    rerender(<MemoryRouter><CustomerRequestListPage /></MemoryRouter>);
    expect(screen.getByText('Requests unavailable')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Retry' }));
    expect(state.query.refetch).toHaveBeenCalled();
  });
});
