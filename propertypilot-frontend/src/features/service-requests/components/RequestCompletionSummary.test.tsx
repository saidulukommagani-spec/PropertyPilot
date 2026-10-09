import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import RequestCompletionSummary from './RequestCompletionSummary';

afterEach(cleanup);

const visits = [
  { visitId: 'visit-1', serviceRequestId: 'request-1', status: 'COMPLETED', scheduledAt: '2026-10-08T10:00:00Z' },
  { visitId: 'visit-2', serviceRequestId: 'request-1', status: 'SCHEDULED', scheduledAt: '2026-10-09T10:00:00Z' },
  { visitId: 'visit-3', serviceRequestId: 'request-1', status: 'CANCELLED', scheduledAt: '2026-10-09T11:00:00Z' },
];
const evidence = [
  { evidenceId: 'evidence-1', serviceRequestId: 'request-1', evidenceTypeId: 'type-1', evidenceTypeCode: 'PHOTO', evidenceTypeName: 'After photo', fileUrl: '/file', validationStatus: 'APPROVED' },
  { evidenceId: 'evidence-2', serviceRequestId: 'request-1', evidenceTypeId: 'type-1', evidenceTypeCode: 'PHOTO', evidenceTypeName: 'After photo', fileUrl: '/file', validationStatus: 'PENDING' },
  { evidenceId: 'evidence-3', serviceRequestId: 'request-1', evidenceTypeId: 'type-1', evidenceTypeCode: 'PHOTO', evidenceTypeName: 'After photo', fileUrl: '/file', validationStatus: 'REJECTED' },
];

describe('RequestCompletionSummary', () => {
  it('shows observed visit and evidence facts without making a frontend completion decision', () => {
    render(<MemoryRouter><RequestCompletionSummary status="IN_PROGRESS" visits={visits} evidence={evidence} canReviewEvidence /></MemoryRouter>);
    expect(screen.getByText('Visits completed').nextElementSibling?.textContent).toBe('1');
    expect(screen.getByText('Visits cancelled').nextElementSibling?.textContent).toBe('1');
    expect(screen.getByText('Visits still open').nextElementSibling?.textContent).toBe('1');
    expect(screen.getByText('Evidence approved').nextElementSibling?.textContent).toBe('1');
    expect(screen.getByText('Evidence awaiting review').nextElementSibling?.textContent).toBe('1');
    expect(screen.getByText('Evidence returned').nextElementSibling?.textContent).toBe('1');
    expect(screen.getByText(/Completing a visit does not by itself complete this request/)).toBeTruthy();
    expect(screen.queryByText(/Eligible for completion|Ready to complete/)).toBeNull();
    expect(screen.getByRole('link', { name: 'Review submitted evidence' }).getAttribute('href')).toBe('/evidence/validation?evidenceId=evidence-2');
  });

  it('does not expose the Operations review link when review actions are not allowed', () => {
    render(<MemoryRouter><RequestCompletionSummary status="IN_PROGRESS" visits={visits} evidence={evidence} /></MemoryRouter>);
    expect(screen.queryByRole('link', { name: 'Review submitted evidence' })).toBeNull();
  });

  it('reports the actual completed request state without showing active completion guidance', () => {
    render(<MemoryRouter><RequestCompletionSummary status="COMPLETED" visits={visits} evidence={evidence} /></MemoryRouter>);
    expect(screen.getByText('COMPLETED')).toBeTruthy();
    expect(screen.queryByText(/backend checks/)).toBeNull();
  });
});
