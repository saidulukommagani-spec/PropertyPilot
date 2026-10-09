import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { serviceRequestApi } from '../../../api/serviceRequestApi';
import type { ServiceRequestCreateRequest, ServiceRequestUpdateRequest } from '../types/serviceRequest';
import { serviceRequestDetailKeys } from './useServiceRequestDetails';

export const serviceRequestKeys = { all: ['service-requests'] as const };

export function useServiceRequests() {
  return useQuery({ queryKey: serviceRequestKeys.all, queryFn: serviceRequestApi.list });
}

export function useCreateServiceRequest() {
  const client = useQueryClient();
  return useMutation({ mutationFn: (request: ServiceRequestCreateRequest) => serviceRequestApi.createRequest(request),
    onSuccess: async () => { await client.invalidateQueries({ queryKey: serviceRequestKeys.all }); },
  });
}

export function useEditServiceRequest() {
  const client = useQueryClient();
  return useMutation({ mutationFn: ({ id, request }: { id: string; request: ServiceRequestUpdateRequest }) => serviceRequestApi.updateRequest(id, request),
    onSuccess: async (response) => {
      await client.invalidateQueries({ queryKey: serviceRequestKeys.all });
      await client.invalidateQueries({ queryKey: serviceRequestDetailKeys.detail(response.serviceRequestId) });
    },
  });
}
