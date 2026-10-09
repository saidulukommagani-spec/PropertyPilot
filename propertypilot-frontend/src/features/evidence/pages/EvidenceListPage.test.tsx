import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import EvidenceListPage from './EvidenceListPage';

const fixture = vi.hoisted(() => ({ data: [] as Array<Record<string, unknown>> }));
vi.mock('../hooks/useEvidence', () => ({ useEvidenceList: () => ({ data: fixture.data, isLoading: false, isError: false, refetch: vi.fn() }) }));

afterEach(() => { cleanup(); fixture.data = []; });

describe('EvidenceListPage', () => {
  it('uses business labels and linked records instead of rendering UUIDs', () => {
    fixture.data = [{ evidenceId: 'evidence-uuid-12345678', serviceRequestId: 'request-uuid-12345678', visitId: 'visit-uuid-12345678', propertyName: 'Lakeview Apartment', serviceName: 'Inspection', evidenceTypeName: 'After photo', fileName: 'kitchen-after.jpg', uploadedByName: 'Asha Rao', capturedAt: '2026-10-08T10:00:00Z', validationStatus: 'PENDING' }];
    render(<MemoryRouter><EvidenceListPage /></MemoryRouter>);
    expect(screen.getByText('kitchen-after.jpg')).toBeTruthy();
    expect(screen.getByText('Lakeview Apartment')).toBeTruthy();
    expect(screen.getByRole('link', { name: 'Open request' }).getAttribute('href')).toBe('/service-requests/request-uuid-12345678');
    expect(screen.getByRole('link', { name: 'Open visit' }).getAttribute('href')).toBe('/visits/visit-uuid-12345678');
    expect(screen.queryByText(/uuid/i)).toBeNull();
  });
});
