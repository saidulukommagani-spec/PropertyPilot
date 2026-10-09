import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import type { CustomerVisit } from '../types/customerPortal';
import CustomerVisitsPage from './CustomerVisitsPage';

const state = vi.hoisted(() => ({ visits: [] as CustomerVisit[] }));
vi.mock('../hooks/useCustomerPortal', () => ({ useCustomerVisits: () => ({ data: state.visits, isLoading: false, isError: false, refetch: vi.fn() }) }));

afterEach(() => { cleanup(); state.visits = []; });

describe('CustomerVisitsPage', () => {
  it('searches readable visit details without IDs and paginates the results', () => {
    state.visits = Array.from({ length: 11 }, (_, index) => ({
      visitId: `visit-uuid-${index}`,
      serviceRequestId: `request-uuid-${index}`,
      status: 'SCHEDULED',
      scheduledAt: `2026-10-${String(index + 1).padStart(2, '0')}T10:00:00Z`,
      propertyName: `Residence ${index + 1}`,
      serviceName: 'Inspection',
    }));
    render(<MemoryRouter><CustomerVisitsPage /></MemoryRouter>);

    expect(screen.getByText(/^Residence 1 ·/)).toBeTruthy();
    expect(screen.queryByText(/^Residence 11 ·/)).toBeNull();
    expect(screen.queryByText('request-uuid-0')).toBeNull();
    fireEvent.click(screen.getByRole('button', { name: 'Go to page 2' }));
    expect(screen.getByText(/^Residence 11 ·/)).toBeTruthy();
    fireEvent.change(screen.getByLabelText('Search visits'), { target: { value: 'request-uuid-10' } });
    expect(screen.getByText('No visits match your search or status filter.')).toBeTruthy();
  });
});
