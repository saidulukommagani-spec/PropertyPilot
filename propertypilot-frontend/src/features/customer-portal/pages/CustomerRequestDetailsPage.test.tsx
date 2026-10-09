import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const state = vi.hoisted(() => ({
  request: { data: { serviceRequestId: 'request-private-id', propertyId: 'property-1', propertyTitle: 'Lakeview House', requestType: 'INSPECTION', serviceName: 'Property inspection', status: 'IN_PROGRESS', priority: 'MEDIUM', requestedAt: '2026-10-08T10:00:00Z', description: 'Please inspect the roof.', slaStatus: 'ON_TRACK', slaDueAt: '2026-10-16T10:00:00Z', statusHistory: [{ newStatus: 'NEW', changedAt: '2026-10-08T10:00:00Z' }, { newStatus: 'IN_PROGRESS', changedAt: '2026-10-09T10:00:00Z' }] }, isLoading: false, isError: false, error: null, refetch: vi.fn() },
  visits: { data: [{ visitId: 'visit-private-id', serviceRequestId: 'request-private-id', serviceName: 'Property inspection', agentName: 'Ravi Kumar', status: 'COMPLETED', scheduledAt: '2026-10-10T10:00:00Z' }], isLoading: false, isError: false, error: null, refetch: vi.fn() },
  evidence: { data: [{ evidenceId: 'evidence-private-id', serviceRequestId: 'request-private-id', evidenceTypeName: 'Before photo', fileName: 'roof-before.jpg', validationStatus: 'PENDING', capturedAt: '2026-10-10T10:30:00Z' }], isLoading: false, isError: false, error: null, refetch: vi.fn() },
}));

vi.mock('../hooks/useCustomerPortal', () => ({
  useCustomerRequest: () => state.request,
  useCustomerVisits: () => state.visits,
  useCustomerEvidenceForRequest: () => state.evidence,
}));

import CustomerRequestDetailsPage from './CustomerRequestDetailsPage';

function renderPage() {
  return render(<MemoryRouter initialEntries={['/customer/service-requests/request-private-id']}><Routes><Route path="/customer/service-requests/:id" element={<CustomerRequestDetailsPage />} /></Routes></MemoryRouter>);
}

afterEach(cleanup);
beforeEach(() => {
  state.request.isLoading = false;
  state.request.isError = false;
  state.request.error = null;
  state.visits.isLoading = false;
  state.visits.isError = false;
  state.evidence.isLoading = false;
  state.evidence.isError = false;
});

describe('CustomerRequestDetailsPage', () => {
  it('shows request progress separately from completed visit and distinguishes pending evidence review', () => {
    renderPage();

    expect(screen.getByRole('heading', { name: 'Property inspection' })).toBeTruthy();
    expect(screen.getByText('Service work is underway. A completed visit does not by itself complete this service request.')).toBeTruthy();
    expect(screen.getByText('Completed')).toBeTruthy();
    expect(screen.getByText('Pending')).toBeTruthy();
    expect(screen.getByText(/roof-before\.jpg/)).toBeTruthy();
    expect(screen.queryByText('request-private-id')).toBeNull();
    expect(screen.queryByRole('button', { name: /complete request/i })).toBeNull();
  });

  it('shows loading state and retry for visit/evidence errors', () => {
    state.request.isLoading = true;
    const { rerender } = renderPage();
    expect(screen.getByLabelText('Loading service request')).toBeTruthy();

    state.request.isLoading = false;
    state.visits.isError = true;
    state.visits.error = new Error('Visits unavailable');
    state.evidence.isError = true;
    state.evidence.error = new Error('Evidence unavailable');
    rerender(<MemoryRouter initialEntries={['/customer/service-requests/request-private-id']}><Routes><Route path="/customer/service-requests/:id" element={<CustomerRequestDetailsPage />} /></Routes></MemoryRouter>);
    expect(screen.getByText('Visits unavailable')).toBeTruthy();
    expect(screen.getByText('Evidence unavailable')).toBeTruthy();
    expect(screen.getAllByRole('button', { name: 'Retry' })).toHaveLength(2);
    fireEvent.click(screen.getAllByRole('button', { name: 'Retry' })[0]);
    expect(state.visits.refetch).toHaveBeenCalled();
  });
});
