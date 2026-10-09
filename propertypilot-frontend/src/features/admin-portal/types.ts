export type AdminResource = 'customers' | 'properties' | 'requests' | 'agents' | 'visits' | 'evidence' | 'quotes' | 'deals' | 'invoices' | 'payments' | 'subscriptions' | 'services' | 'pricing' | 'plans' | 'users';

export interface AdminDirectoryRow {
  id: string;
  title: string;
  primary: string;
  secondary: string;
  status: string;
  type: string;
  date: string;
  amount: string;
  detailPath: string;
  relationId: string;
  customerId: string;
  propertyId: string;
}

export type AdminRecord = Record<string, unknown>;
