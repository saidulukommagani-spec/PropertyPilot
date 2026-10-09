import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const mocked = vi.hoisted(() => ({
  status: 'SCHEDULED',
  detailError: false,
  loading: false,
  refetch: vi.fn(),
  perform: vi.fn(),
  reschedule: vi.fn(),
  cancel: vi.fn(),
}));

vi.mock('../hooks/useVisits', () => ({
  useVisitDetails: () => {
    const visit = { visitId: 'visit-1', serviceRequestId: 'request-1', status: mocked.status, scheduledAt: '2026-10-12T10:00:00Z', visitNumber: 1, customerName: 'Asha Rao', propertyName: 'Lakeview House', serviceName: 'Inspection', agentName: 'Ravi Agent', evidence: [] };
    return {
      visit: { data: mocked.detailError || mocked.loading ? undefined : visit, isLoading: mocked.loading, isError: mocked.detailError, error: mocked.detailError ? new Error('Not assigned') : null, refetch: mocked.refetch },
      request: { data: undefined, isError: false, refetch: mocked.refetch },
      visits: { data: [visit] },
      assignmentHistory: { data: [], isError: false, refetch: mocked.refetch },
      evidence: { data: [], isLoading: false, isError: false, refetch: mocked.refetch },
    };
  },
  useVisitAction: () => ({ mutateAsync: mocked.perform, isPending: false }),
  useRescheduleVisit: () => ({ mutateAsync: mocked.reschedule, isPending: false, error: null }),
  useCancelVisit: () => ({ mutateAsync: mocked.cancel, isPending: false, isError: false, error: null }),
}));
vi.mock('../../evidence/hooks/useEvidence', () => ({ useEvidenceTypes: () => ({ data: [], isLoading: false, isError: false, refetch: vi.fn() }) }));
vi.mock('../../evidence/components/EvidenceUploader', () => ({ default: () => <div>Evidence uploader</div> }));
vi.mock('../components/VisitExecutionEvidenceCard', () => ({ default: () => <div>Required evidence checklist</div> }));
vi.mock('../components/VisitRescheduleDialog', () => ({ default: () => null }));
vi.mock('../../service-requests/components/EvidenceSummary', () => ({ default: () => <div>Evidence totals</div> }));

import VisitDetailsPage from './VisitDetailsPage';

function renderAgentVisit() {
  return render(<MemoryRouter initialEntries={['/agent/visit/visit-1']}><Routes>
    <Route path="/agent/visit/:visitId" element={<VisitDetailsPage agentMode />} />
    <Route path="/agent/visits" element={<div>Agent visits</div>} />
  </Routes></MemoryRouter>);
}

afterEach(() => {
  cleanup();
  mocked.status = 'SCHEDULED';
  mocked.detailError = false;
  mocked.loading = false;
  vi.clearAllMocks();
});

describe('Agent Visit Execution', () => {
  it('shows customer, property, service and scheduled context with the next scheduled action', () => {
    renderAgentVisit();
    expect(screen.getByText('Asha Rao · Lakeview House · Inspection')).toBeTruthy();
    expect(screen.getByText('Scheduled Visit')).toBeTruthy();
    expect(screen.getByRole('button', { name: 'Start Visit' })).toBeTruthy();
    expect(screen.queryByRole('button', { name: 'Complete Visit' })).toBeNull();
    expect(screen.getByText('Required evidence checklist')).toBeTruthy();
  });

  it('submits only the permitted start action for a scheduled visit', async () => {
    mocked.perform.mockResolvedValueOnce({});
    renderAgentVisit();
    fireEvent.click(screen.getByRole('button', { name: 'Start Visit' }));
    await waitFor(() => expect(mocked.perform).toHaveBeenCalledWith({ visitId: 'visit-1', action: 'start' }));
    expect(await screen.findByText('Visit started.')).toBeTruthy();
  });

  it('shows complete only while the visit is in progress and keeps parent completion distinct', () => {
    mocked.status = 'IN_PROGRESS';
    renderAgentVisit();
    expect(screen.getByRole('button', { name: 'Complete Visit' })).toBeTruthy();
    expect(screen.getByText('Capture required evidence and complete the visit when your work is done.')).toBeTruthy();
  });

  it('submits completion only for an in-progress visit and keeps the request-level result separate', async () => {
    mocked.status = 'IN_PROGRESS';
    mocked.perform.mockResolvedValueOnce({});
    renderAgentVisit();
    fireEvent.click(screen.getByRole('button', { name: 'Complete Visit' }));
    await waitFor(() => expect(mocked.perform).toHaveBeenCalledWith({ visitId: 'visit-1', action: 'complete' }));
    expect(await screen.findByText(/Visit marked complete\. Service request completion is evaluated separately/)).toBeTruthy();
  });

  it('surfaces completion errors and retries the same allowed action', async () => {
    mocked.status = 'IN_PROGRESS';
    mocked.perform.mockRejectedValueOnce(new Error('Evidence validation is incomplete')).mockResolvedValueOnce({});
    renderAgentVisit();
    fireEvent.click(screen.getByRole('button', { name: 'Complete Visit' }));
    expect(await screen.findByText('Evidence validation is incomplete')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Retry' }));
    await waitFor(() => expect(mocked.perform).toHaveBeenCalledTimes(2));
    expect(mocked.perform).toHaveBeenLastCalledWith({ visitId: 'visit-1', action: 'complete' });
  });

  it('shows loading state while assigned visit data is loading', () => {
    mocked.loading = true;
    renderAgentVisit();
    expect(screen.getByLabelText('Loading visit')).toBeTruthy();
  });

  it('shows a retryable error when an assigned-visit detail request fails', () => {
    mocked.detailError = true;
    renderAgentVisit();
    expect(screen.getByText('Not assigned')).toBeTruthy();
    fireEvent.click(screen.getByRole('button', { name: 'Retry' }));
    expect(mocked.refetch).toHaveBeenCalled();
  });

  it('offers no workflow action for a completed visit', () => {
    mocked.status = 'COMPLETED';
    renderAgentVisit();
    expect(screen.queryByRole('button', { name: 'Start Visit' })).toBeNull();
    expect(screen.queryByRole('button', { name: 'Complete Visit' })).toBeNull();
    expect(screen.getByText('This visit has no further workflow actions.')).toBeTruthy();
  });
});
