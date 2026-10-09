import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes, useLocation } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import VisitTimeline from './VisitTimeline';

function CurrentPath() {
  const { pathname } = useLocation();
  return <output>{pathname}</output>;
}

const visit = {
  visitId: 'visit-123',
  serviceRequestId: 'request-456',
  status: 'SCHEDULED',
  scheduledAt: '2026-10-08T10:00:00Z',
  agentName: 'Agent One',
};

describe('VisitTimeline navigation', () => {
  it('opens the visit workspace from the operations request route', () => {
    render(<MemoryRouter initialEntries={['/service-requests/request-456']}>
      <Routes>
        <Route path="/service-requests/:id" element={<VisitTimeline visits={[visit]} />} />
        <Route path="/visits/:visitId" element={<CurrentPath />} />
      </Routes>
    </MemoryRouter>);

    fireEvent.click(screen.getByRole('button', { name: 'Open visit 1' }));

    expect(screen.getByText('/visits/visit-123')).toBeTruthy();
  });

  it('opens the agent visit workspace from the agent request route', () => {
    render(<MemoryRouter initialEntries={['/agent/service-requests/request-456']}>
      <Routes>
        <Route path="/agent/service-requests/:id" element={<VisitTimeline visits={[visit]} />} />
        <Route path="/agent/visits/:visitId" element={<CurrentPath />} />
      </Routes>
    </MemoryRouter>);

    fireEvent.click(screen.getByRole('button', { name: 'Open visit 1' }));

    expect(screen.getByText('/agent/visits/visit-123')).toBeTruthy();
  });
});
