import { describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/react';
import VisitTimeline from './VisitTimeline';

describe('VisitTimeline', () => {
  it('renders visit events in chronological order when a reschedule is recorded', () => {
    render(<VisitTimeline visit={{
      visitId: 'visit-id',
      serviceRequestId: 'request-id',
      status: 'COMPLETED',
      scheduledAt: '2026-10-10T10:00:00Z',
      createdAt: '2026-10-01T10:00:00Z',
      updatedAt: '2026-10-05T10:00:00Z',
      startedAt: '2026-10-10T10:00:00Z',
      endedAt: '2026-10-10T11:00:00Z',
      rescheduleReason: 'Customer requested a new time',
      evidence: [],
      visitNumber: 1,
    }} />);

    const labels = screen.getAllByText(/^(Visit created|Rescheduled|Started|Completed)$/).map((element) => element.textContent);
    expect(labels).toEqual(['Visit created', 'Rescheduled', 'Started', 'Completed']);
    expect(screen.getByText('Customer requested a new time')).toBeTruthy();
  });
});
