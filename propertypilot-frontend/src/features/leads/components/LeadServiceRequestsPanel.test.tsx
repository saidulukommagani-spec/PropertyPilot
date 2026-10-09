import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import type { LeadServiceRequest } from '../types/lead';
import LeadServiceRequestsPanel from './LeadServiceRequestsPanel';

const state = vi.hoisted(() => ({ requests: [] as LeadServiceRequest[] }));

vi.mock('@tanstack/react-query', () => ({
  useQueries: () => [],
  useQuery: () => ({ data: [], isLoading: false, isError: false, refetch: vi.fn() }),
}));

vi.mock('../hooks/useLeads', () => ({
  useLeadServiceRequests: () => ({ data: state.requests, isLoading: false, isError: false, refetch: vi.fn() }),
  useCreateLeadServiceRequest: () => ({ mutateAsync: vi.fn(), isPending: false, isError: false, error: null }),
}));

afterEach(() => {
  cleanup();
  state.requests = [];
});

describe('LeadServiceRequestsPanel', () => {
  it('distinguishes invoiced value excluding tax from tax-inclusive collections', () => {
    state.requests = [{
      serviceRequestId: 'request-1', leadId: 'lead-1', propertyId: null, serviceId: 'service-1', quoteId: 'quote-1',
      serviceName: 'Property inspection', status: 'COMPLETED', priority: 'MEDIUM', assignedAgent: 'Asha Agent',
      requestedAt: '2026-10-01T09:00:00Z', scheduledAt: null, completedAt: '2026-10-02T09:00:00Z',
      slaDueAt: null, slaStatus: 'MET', visitCount: 0, evidenceCount: 1, requestValue: 100, currencyCode: 'INR',
      revenueByCurrency: [{ currencyCode: 'INR', invoicedRevenue: 100, collectedRevenue: 118 }],
      statusHistory: [], assignmentHistory: [],
    }];

    render(<MemoryRouter><LeadServiceRequestsPanel leadId="lead-1" operations /></MemoryRouter>);

        expect(screen.getByText('Invoiced service value (excl. tax) - INR')).toBeTruthy();
        expect(screen.getByText('Payments received (incl. tax) - INR')).toBeTruthy();
        expect(screen.getByText('Invoiced (excl. tax) - INR')).toBeTruthy();
        expect(screen.getByText('Received (incl. tax) - INR')).toBeTruthy();
  });
});
