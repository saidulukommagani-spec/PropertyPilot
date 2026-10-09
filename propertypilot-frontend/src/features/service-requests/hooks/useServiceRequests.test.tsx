import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import { useServiceRequests } from './useServiceRequests';

const result = [{ serviceRequestId:'one', propertyId:'property-1', customerId:'customer-1', customerName:'Asha Rao', propertyTitle:'Lakeview', requestType:'Inspection', serviceName:'Inspection', status:'NEW', priority:'NORMAL', requestedAt:'2026-01-01T00:00:00Z' }];
function wrapper({children}:{children:React.ReactNode}) { const client=new QueryClient({defaultOptions:{queries:{retry:false}}}); return <QueryClientProvider client={client}>{children}</QueryClientProvider>; }
afterEach(cleanup);

describe('useServiceRequests', () => {
  it('loads and exposes service request data through React Query', async () => {
    const fetch=vi.spyOn(serviceRequestApi,'list').mockResolvedValue(result);
    const {result:hook}=renderHook(()=>useServiceRequests(),{wrapper});
    await waitFor(()=>expect(hook.current.isSuccess).toBe(true));
    expect(hook.current.data).toEqual(result); expect(fetch).toHaveBeenCalledOnce();
    fetch.mockRestore();
  });
});
