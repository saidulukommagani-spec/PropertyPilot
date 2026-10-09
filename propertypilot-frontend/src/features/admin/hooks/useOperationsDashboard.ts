import { useQuery } from '@tanstack/react-query';
import { adminOperationsApi } from '../services/adminOperationsApi';

export function useOperationsDashboard() {
  return useQuery({
    queryKey: ['admin-operations', 'dashboard'],
    queryFn: adminOperationsApi.dashboard,
  });
}
