import { afterEach, describe, expect, it } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import AssignmentCard from './AssignmentCard';
import StatusActions from './StatusActions';
import type { ServiceRequestDetailsResponse } from '../types/serviceRequest';

afterEach(cleanup);

const request: ServiceRequestDetailsResponse = {
  serviceRequestId: 'request-id',
  propertyId: 'property-id',
  propertyTitle: 'Lakeview Residence',
  customerId: 'customer-id',
  customerName: 'Asha Customer',
  customerEmail: 'asha@example.com',
  customerMobile: '0000000000',
  requestType: 'INSPECTION',
  status: 'IN_PROGRESS',
  priority: 'MEDIUM',
  requestedAt: '2026-10-01T10:00:00Z',
  statusHistory: [],
  assignedToName: 'Ravi Agent',
};

describe('Service Request agent action permissions', () => {
  it('hides request assignment controls for the Agent workspace', () => {
    render(<AssignmentCard request={request} canManageAssignment={false} onAssignClick={() => undefined} />);

    expect(screen.queryByRole('button', { name: /reassign agent/i })).toBeNull();
  });

  it('keeps the assigned Agent workflow action while hiding cancellation', () => {
    render(<StatusActions
      status="IN_PROGRESS"
      canManageRequest={false}
      isPending={false}
      onAssign={() => undefined}
      onAction={async () => undefined}
      onCancel={async () => undefined}
    />);

    expect(screen.getByRole('button', { name: /complete request/i })).not.toBeNull();
    expect(screen.queryByRole('button', { name: /cancel request/i })).toBeNull();
  });

  it('hides assignment and cancellation actions for unassigned work in the Agent workspace', () => {
    render(<StatusActions
      status="NEW"
      canManageRequest={false}
      isPending={false}
      onAssign={() => undefined}
      onAction={async () => undefined}
      onCancel={async () => undefined}
    />);

    expect(screen.queryByRole('button', { name: /assign agent/i })).toBeNull();
    expect(screen.queryByRole('button', { name: /cancel request/i })).toBeNull();
  });
});
