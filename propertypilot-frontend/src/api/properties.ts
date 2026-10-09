import { apiClient } from './client';

export interface PropertyRecord {
  propertyId: string;
  customerId: string;
  title: string;
  propertyType: string;
  listingStatus: string | null;
  price: number | null;
  status: string;
}

export interface PropertyMutation {
  title: string;
  propertyType: string;
  listingStatus?: string;
  price?: number;
  status: string;
}

export const propertiesApi = {
  async get(propertyId: string): Promise<PropertyRecord> {
    return (await apiClient.get<PropertyRecord>(`/api/v1/properties/${propertyId}`)).data;
  },
  async create(request: PropertyMutation): Promise<PropertyRecord> {
    return (await apiClient.post<PropertyRecord>('/api/v1/properties', request)).data;
  },
  async update(propertyId: string, customerId: string, request: PropertyMutation): Promise<PropertyRecord> {
    return (await apiClient.put<PropertyRecord>(`/api/v1/properties/${propertyId}`, { ...request, customerId })).data;
  },
  async archive(propertyId: string): Promise<PropertyRecord> {
    return (await apiClient.patch<PropertyRecord>(`/api/v1/properties/${propertyId}/archive`)).data;
  },
};
