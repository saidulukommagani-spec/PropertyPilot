import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter } from 'react-router-dom';

const state = vi.hoisted(() => ({
  roles: ['AGENT'] as string[],
  listMine: vi.fn(async () => [{ visitId: 'visit-internal-id', serviceRequestId: 'request-internal-id', status: 'IN_PROGRESS', scheduledAt: '2026-10-09T14:30:00Z', customerName: 'Mina Owner', propertyName: 'Willow House', serviceName: 'Window Cleaning' }]),
  listAll: vi.fn(async () => []),
  listServices: vi.fn(async () => [{ serviceId: 'remote-legal', serviceName: 'Legal documents', requiresVisit: false, visitMandatory: false, remote: true }]),
  myRequests: vi.fn(async () => [{ serviceRequestId: 'request-no-visit-id', serviceId: 'remote-legal', propertyId: 'property-id', propertyTitle: 'Willow House', customerId: 'customer-id', customerName: 'Mina Owner', requestType: 'LEGAL', serviceName: 'Legal documents', status: 'IN_PROGRESS', priority: 'MEDIUM', requestedAt: '2026-10-08T10:00:00Z' }]),
  adminRequests: vi.fn(async () => []),
}));

vi.mock('../../../api/VisitApi', () => ({ VisitApi: {
  listMine: state.listMine,
  listAll: state.listAll,
  listServices: state.listServices,
  getDetails: vi.fn(),
} }));
vi.mock('../../../api/serviceRequestApi', () => ({ serviceRequestApi: { list: state.adminRequests } }));
vi.mock('../../agent-operations/services/AgentOperationsApi', () => ({ AgentOperationsApi: { myRequests: state.myRequests } }));
vi.mock('../../../api/auth', () => ({ getUserRoles: (user: { roles: string[] }) => user.roles }));
vi.mock('../../auth/AuthContext', () => ({ useAuth: () => ({ user: { roles: state.roles } }) }));
vi.mock('../hooks/useEvidence', () => ({ useEvidenceTypes: () => ({ data: [], isLoading: false, isError: false, refetch: vi.fn() }) }));
vi.mock('../components/EvidenceUploader', () => ({ default: (props: { visitId?: string; serviceRequestId?: string }) => <div data-testid="evidence-upload-target" data-visit-id={props.visitId} data-service-request-id={props.serviceRequestId} /> }));

import EvidenceUploadPage from './EvidenceUploadPage';

afterEach(() => {
  cleanup();
  state.roles = ['AGENT'];
  state.listMine.mockClear();
  state.listAll.mockClear();
  state.myRequests.mockClear();
  state.listServices.mockClear();
  state.adminRequests.mockClear();
});

describe('EvidenceUploadPage', () => {
  it('loads only the signed-in agent visits for the agent visit picker', async () => {
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter><EvidenceUploadPage /></MemoryRouter></QueryClientProvider>);

    await waitFor(() => expect(state.listMine).toHaveBeenCalledOnce());
    expect(state.listAll).not.toHaveBeenCalled();
    expect(await screen.findByRole('combobox', { name: 'Active visit' })).toBeTruthy();
  });

  it('labels available visits with customer-facing details, not their IDs', async () => {
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter><EvidenceUploadPage /></MemoryRouter></QueryClientProvider>);
    await waitFor(() => expect(state.listMine).toHaveBeenCalledOnce());
    fireEvent.mouseDown(await screen.findByRole('combobox', { name: 'Active visit' }));

    expect(await screen.findByRole('option', { name: /Mina Owner.*Willow House.*Window Cleaning/ })).toBeTruthy();
    expect(screen.queryByText('visit-internal-id')).toBeNull();
    expect(screen.queryByText('request-internal-id')).toBeNull();
  });

  it('offers in-progress remote requests for direct evidence upload without loading the operations visit list', async () => {
    const client = new QueryClient();
    render(<QueryClientProvider client={client}><MemoryRouter initialEntries={['/evidence/upload?serviceRequestId=request-no-visit-id']}><EvidenceUploadPage /></MemoryRouter></QueryClientProvider>);

    expect(await screen.findByText('Upload evidence')).toBeTruthy();
    expect(state.myRequests).toHaveBeenCalledOnce();
    expect(state.listAll).not.toHaveBeenCalled();
    expect(screen.getByTestId('evidence-upload-target').getAttribute('data-service-request-id')).toBe('request-no-visit-id');
    expect(screen.queryByText('Evidence can be uploaded after a visit starts or when an in-progress no-visit service is ready for evidence.')).toBeNull();
  });
});
