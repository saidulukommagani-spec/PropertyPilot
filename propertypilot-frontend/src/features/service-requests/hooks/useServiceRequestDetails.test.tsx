import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { renderHook, waitFor } from '@testing-library/react';
import type { ReactNode } from 'react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { useServiceRequestDetails } from './useServiceRequestDetails';

afterEach(() => vi.restoreAllMocks());

describe('useServiceRequestDetails role-specific API access', () => {
  it('uses the ownership-checked detail endpoint and skips operations-only queries for agents', async () => {
    const assignedDetails = vi.spyOn(serviceRequestApi, 'getAssignedDetails').mockRejectedValue(new Error('Not found'));
    const operationsDetails = vi.spyOn(serviceRequestApi, 'getDetails');
    const assignableAgents = vi.spyOn(serviceRequestApi, 'getAssignableAgents');
    const assignmentHistory = vi.spyOn(serviceRequestApi, 'getAssignmentHistory');
    vi.spyOn(serviceRequestApi, 'getVisits').mockResolvedValue([]);
    vi.spyOn(serviceRequestApi, 'getEvidence').mockResolvedValue([]);

    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
    const wrapper = ({ children }: { children: ReactNode }) => <QueryClientProvider client={client}>{children}</QueryClientProvider>;
    renderHook(() => useServiceRequestDetails('request-123', true), { wrapper });

    await waitFor(() => expect(assignedDetails).toHaveBeenCalledWith('request-123'));
    expect(operationsDetails).not.toHaveBeenCalled();
    expect(assignableAgents).not.toHaveBeenCalled();
    expect(assignmentHistory).not.toHaveBeenCalled();
    client.clear();
  });
});
