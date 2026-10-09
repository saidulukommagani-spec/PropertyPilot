import { useQuery } from '@tanstack/react-query';
import { adminPortalApi } from '../adminPortalApi';
import type { AdminResource } from '../types';

export const adminPortalKeys = {
  list: (resource: AdminResource) => ['admin-portal', resource] as const,
  detail: (resource: AdminResource, id: string) => ['admin-portal', resource, id] as const,
};

export function useAdminDirectory(resource: AdminResource) {
  return useQuery({ queryKey: adminPortalKeys.list(resource), queryFn: () => adminPortalApi.list(resource) });
}

export function useAdminRecord(resource: AdminResource, id: string) {
  return useQuery({ queryKey: adminPortalKeys.detail(resource, id), queryFn: () => adminPortalApi.get(resource, id), enabled: Boolean(id) });
}
