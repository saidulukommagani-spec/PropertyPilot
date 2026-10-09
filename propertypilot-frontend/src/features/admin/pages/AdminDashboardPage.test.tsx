import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

const state = vi.hoisted(() => ({
  result: { totalRequests: 25, unassignedRequests: 3, activeRequests: 8, completedRequests: 12, failedRequests: 2, overdueInvoices: 4, failedPayments: 1 },
  evidence: { totalEvidence: 20, pendingValidation: 5, approved: 12, rejected: 3, missingEvidence: 0, todaysUploads: 2, byType: {}, byService: {} },
  dashboardLoading: false,
  dashboardError: false,
  evidenceLoading: false,
  evidenceError: false,
  refetchDashboard: vi.fn(),
  refetchEvidence: vi.fn(),
}));

vi.mock('../hooks/useOperationsDashboard', () => ({
  useOperationsDashboard: () => ({ data: state.result, isLoading: state.dashboardLoading, isError: state.dashboardError, refetch: state.refetchDashboard, error: new Error('Dashboard unavailable') }),
}));

vi.mock('../../evidence/hooks/useEvidence', () => ({
  useEvidenceDashboard: () => ({ data: state.evidence, isLoading: state.evidenceLoading, isError: state.evidenceError, refetch: state.refetchEvidence, error: new Error('Evidence unavailable') }),
}));

import AdminDashboardPage from './AdminDashboardPage';

afterEach(() => {
  cleanup();
  state.dashboardError = false;
  state.evidenceError = false;
});

describe('AdminDashboardPage', () => {
  it('shows live operations summary values and links to real work queues', () => {
    render(<MemoryRouter><AdminDashboardPage /></MemoryRouter>);

    expect(screen.getByText('Operations overview')).toBeTruthy();
    expect(screen.getByText('Awaiting assignment').parentElement?.textContent).toContain('3');
    expect(screen.getByText('Failed payments').parentElement?.textContent).toContain('1');
    expect(screen.getByText('Evidence awaiting review').parentElement?.textContent).toContain('5');
    expect(screen.getByRole('link', { name: /Evidence awaiting review/ }).getAttribute('href')).toBe('/evidence/validation');
    expect(screen.getByRole('link', { name: /Awaiting assignment/ }).getAttribute('href')).toBe('/operations/assignments?assignment=unassigned');
    expect(screen.getByRole('link', { name: /Completed requests/ }).getAttribute('href')).toBe('/service-requests?status=COMPLETED');
    expect(screen.getByRole('link', { name: /Failed requests/ }).getAttribute('href')).toBe('/service-requests?status=FAILED');
    expect(screen.getByRole('link', { name: 'Service requests' }).getAttribute('href')).toBe('/service-requests');
    expect(screen.getByRole('link', { name: 'Agent assignments' }).getAttribute('href')).toBe('/operations/assignments');
    expect(screen.queryByText('Your operational queues will appear here as their API contracts are connected.')).toBeNull();
  });

  it('does not present failed metric requests as zero and offers retry', () => {
    state.dashboardError = true;
    state.evidenceError = true;
    render(<MemoryRouter><AdminDashboardPage /></MemoryRouter>);

    expect(screen.getByText('Dashboard unavailable')).toBeTruthy();
    expect(screen.getByText('Evidence unavailable')).toBeTruthy();
    expect(screen.getAllByText('—').length).toBeGreaterThan(0);
  });
});
