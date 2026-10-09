import { apiClient } from './client';
import type { PaymentResponse } from '../features/invoices/types/invoice';

export const paymentsApi = {
  async get(paymentId: string): Promise<PaymentResponse> {
    return (await apiClient.get<PaymentResponse>(`/api/payments/${paymentId}`)).data;
  },

  async list(): Promise<PaymentResponse[]> {
    return (await apiClient.get<PaymentResponse[]>('/api/payments')).data.sort((left, right) =>
      (right.paymentDate ?? '').localeCompare(left.paymentDate ?? ''),
    );
  },
};
