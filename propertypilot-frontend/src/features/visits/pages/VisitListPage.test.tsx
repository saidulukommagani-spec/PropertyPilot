import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';
import type { VisitSummary } from '../types/visit';
import type { ServiceRequestSummaryResponse } from '../../service-requests/types/serviceRequest';
import VisitListPage from './VisitListPage';

const state = vi.hoisted(() => ({ visits: [] as VisitSummary[], requests: [] as ServiceRequestSummaryResponse[] }));

vi.mock('../../../api/VisitApi', () => ({ VisitApi: { listAll: async () => state.visits, listMine: async () => state.visits } }));
vi.mock('../../../api/serviceRequestApi', () => ({ serviceRequestApi: { list: async () => state.requests } }));

afterEach(() => {
  cleanup();
  state.visits = [];
  state.requests = [];
});

describe('VisitListPage', () => {
  it('uses customer/property labels for search and does not search or display raw identifiers', async () => {
    state.visits = [{
      visitId: 'visit-internal-id',
      serviceRequestId: 'request-internal-id',
      status: 'SCHEDULED',
      scheduledAt: '2026-10-10T10:00:00Z',
      agentName: 'Ravi Agent',
    }];
    state.requests = [{
      serviceRequestId: 'request-internal-id',
      propertyId: 'property-internal-id',
      customerId: 'customer-internal-id',
      propertyTitle: 'Lakeview Residence',
      customerName: 'Asha Customer',
      requestType: 'INSPECTION',
      serviceName: 'Property inspection',
      status: 'ACCEPTED',
      priority: 'NORMAL',
      requestedAt: '2026-10-01T10:00:00Z',
      assignedAgentId: 'agent-internal-id',
      assignedAgentName: 'Ravi Agent',
    }];
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    render(<QueryClientProvider client={client}><MemoryRouter><VisitListPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('Lakeview Residence')).toBeTruthy();
    expect(screen.getByText('Asha Customer')).toBeTruthy();
    expect(screen.queryByText('visit-internal-id')).toBeNull();
    expect(screen.queryByText('request-internal-id')).toBeNull();
    fireEvent.change(screen.getByPlaceholderText('Search by customer, property, service, or agent'), { target: { value: 'request-internal-id' } });
    expect(await screen.findByText('No matching visits')).toBeTruthy();
  });
});
