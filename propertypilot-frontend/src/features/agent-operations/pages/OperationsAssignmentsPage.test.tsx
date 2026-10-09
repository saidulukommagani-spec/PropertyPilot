import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const mockData = vi.hoisted(() => ({ requests: [] as Array<Record<string, unknown>> }));

vi.mock('../hooks/useAgentOperations', () => ({
  useAgentAssignmentsData: () => ({
    agents: { data: [], isLoading: false, isError: false, refetch: vi.fn(), error: null },
    requests: { data: mockData.requests, isLoading: false, isError: false, refetch: vi.fn(), error: null },
  }),
  useAgentAssignment: () => ({ mutateAsync: vi.fn(), isPending: false, isError: false, error: null }),
}));

import OperationsAssignmentsPage from './OperationsAssignmentsPage';

function request(index: number, status: string, customerName = `Customer ${index}`) {
  return {
    serviceRequestId: `opaque-${index}`,
    requestedAt: `2026-10-${String(index).padStart(2, '0')}T10:00:00Z`,
    customerName,
    propertyTitle: `Property ${index}`,
    serviceName: 'Property inspection',
    requestType: 'Inspection',
    status,
    assignedAgentName: status === 'ASSIGNED' ? 'Ravi Kumar' : undefined,
  };
}

function renderPage(path = '/operations/assignments') {
  return render(<MemoryRouter initialEntries={[path]}><Routes><Route path="/operations/assignments" element={<OperationsAssignmentsPage />} /></Routes></MemoryRouter>);
}

afterEach(() => {
  cleanup();
  mockData.requests = [];
});

describe('OperationsAssignmentsPage request queue', () => {
  it('opens directly in the unassigned queue and shows readable request context', () => {
    mockData.requests = [request(1, 'NEW', 'Asha Rao'), request(2, 'PENDING_ASSIGNMENT'), request(3, 'ASSIGNED')];
    renderPage('/operations/assignments?assignment=unassigned');

    expect(screen.getByRole('combobox', { name: 'Assignment' }).textContent).toContain('Awaiting assignment');
    expect(screen.getByText('Asha Rao')).toBeTruthy();
    expect(screen.queryByText('Ravi Kumar')).toBeNull();
    expect(screen.queryByText('Customer 3')).toBeNull();
    expect(screen.getAllByRole('link', { name: 'Open request' }).map((link) => link.getAttribute('href'))).toContain('/service-requests/opaque-1');
  });

  it('searches requests by customer/property/service and paginates results', async () => {
    const user = userEvent.setup();
    mockData.requests = Array.from({ length: 12 }, (_, index) => request(index + 1, 'NEW'));
    renderPage();

    expect(screen.getByText('Customer 12')).toBeTruthy();
    expect(screen.queryByText('Customer 1')).toBeNull();
    await user.click(screen.getByRole('button', { name: 'Go to next page' }));
    expect(screen.getByText('Customer 1')).toBeTruthy();

    await user.type(screen.getByRole('textbox', { name: 'Search requests' }), 'Property 12');
    expect(screen.getByText('Customer 12')).toBeTruthy();
    expect(screen.queryByText('Customer 1')).toBeNull();
  });

  it('shows an empty filter result without presenting completed work as assignable', async () => {
    mockData.requests = [request(1, 'COMPLETED'), request(2, 'CANCELLED'), request(3, 'FAILED')];
    renderPage();

    expect(screen.getByText('No open service requests need assignment.')).toBeTruthy();
    expect(screen.queryByRole('button', { name: /Assign$/ })).toBeNull();
  });
});
