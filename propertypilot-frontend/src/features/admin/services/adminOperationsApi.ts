import { apiClient } from '../../../api/client';
import type { OperationsDashboardResponse } from '../types/operationsDashboard';

export const adminOperationsApi = {
  async dashboard(): Promise<OperationsDashboardResponse> {
    return (await apiClient.get<OperationsDashboardResponse>('/api/admin/operations/dashboard')).data;
  },
};
