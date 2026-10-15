import { apiClient } from './client';

export interface PropertyRecord {
  propertyId: string;
  customerId: string;
  title: string;
  propertyType: string;
  surveyNumber?: string | null;
  areaValue?: number | null;
  areaUnit?: 'SQ_FT' | 'SQ_YD' | 'ACRES_GUNTAS' | null;
  areaGuntas?: number | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  localityName?: string | null;
  city?: string | null;
  district?: string | null;
  state?: string | null;
  postalCode?: string | null;
  countryCode?: string | null;
  listingStatus: string | null;
  price: number | null;
  status: string;
}

export interface PropertyMutation {
  title: string;
  propertyType: string;
  surveyNumber?: string;
  areaValue?: number;
  areaUnit?: 'SQ_FT' | 'SQ_YD' | 'ACRES_GUNTAS';
  areaGuntas?: number;
  addressLine1?: string;
  addressLine2?: string;
  localityName?: string;
  city?: string;
  district?: string;
  state?: string;
  postalCode?: string;
  countryCode?: string;
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
