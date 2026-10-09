import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import type { ServiceRequestDetailsResponse } from '../types/serviceRequest';
import RequestSummaryCard from './RequestSummaryCard';

afterEach(cleanup);

describe('RequestSummaryCard', () => {
  it('shows readable request context and SLA without exposing the internal request UUID', () => {
    const request: ServiceRequestDetailsResponse = {
      serviceRequestId: 'internal-request-uuid', propertyId: 'internal-property-uuid', propertyTitle: 'Lakeview Residence',
      customerId: 'internal-customer-uuid', customerName: 'Asha Customer', customerEmail: 'asha@example.test', customerMobile: '5550100',
      requestType: 'INSPECTION', serviceName: 'Property inspection', status: 'IN_PROGRESS', priority: 'HIGH',
      requestedAt: '2026-10-09T08:00:00Z', scheduledAt: '2026-10-12T08:00:00Z',
      slaDueAt: '2026-10-10T08:00:00Z', slaStatus: 'ON_TRACK', statusHistory: [],
    };

    const { container } = render(<RequestSummaryCard request={request} />);

    expect(screen.getByText('Asha Customer')).toBeTruthy();
    expect(screen.getByText('Lakeview Residence')).toBeTruthy();
    expect(screen.getByText('On track')).toBeTruthy();
    expect(container.textContent).not.toContain('internal-request-uuid');
    expect(container.textContent).not.toContain('internal-property-uuid');
    expect(container.textContent).not.toContain('internal-customer-uuid');
  });
});
