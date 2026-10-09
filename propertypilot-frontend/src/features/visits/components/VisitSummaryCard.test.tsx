import { describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/react';
import VisitSummaryCard from './VisitSummaryCard';

describe('VisitSummaryCard', () => {
  it('shows visit and related business labels without internal identifiers or duplicate service rows', () => {
    render(<VisitSummaryCard visit={{
      visitId: 'visit-internal-id',
      serviceRequestId: 'request-internal-id',
      status: 'SCHEDULED',
      scheduledAt: '2026-10-10T10:00:00Z',
      visitNumber: 2,
      serviceName: 'Property inspection',
      propertyName: 'Lakeview Residence',
      customerName: 'Asha Customer',
      agentName: 'Ravi Agent',
      evidence: [],
    }} />);

    expect(screen.getByText('Visit Number')).toBeTruthy();
    expect(screen.getByText('Visit Number').nextElementSibling?.textContent).toBe('2');
    expect(screen.getByText('Property inspection')).toBeTruthy();
    expect(screen.getByText('Lakeview Residence')).toBeTruthy();
    expect(screen.getByText('Asha Customer')).toBeTruthy();
    expect(screen.getByText('Ravi Agent')).toBeTruthy();
    expect(screen.getAllByText('Service')).toHaveLength(1);
    expect(screen.queryByText('visit-internal-id')).toBeNull();
    expect(screen.queryByText('request-internal-id')).toBeNull();
  });
});
