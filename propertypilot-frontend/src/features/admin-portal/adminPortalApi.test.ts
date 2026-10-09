import { afterEach, describe, expect, it, vi } from 'vitest';
import { adminPortalApi } from './adminPortalApi';

const mocks = vi.hoisted(() => ({ get: vi.fn() }));

vi.mock('../../api/client', () => ({ apiClient: { get: mocks.get } }));
vi.mock('../../api/serviceRequestApi', () => ({ serviceRequestApi: {} }));
vi.mock('../../api/VisitApi', () => ({ VisitApi: {} }));

afterEach(() => vi.clearAllMocks());

describe('adminPortalApi service details', () => {
  it('loads the selected service from the existing admin catalog endpoint', async () => {
    const service = { serviceId: 'service-1', serviceName: 'Property inspection', active: true };
    mocks.get.mockResolvedValue({ data: [service] });

    await expect(adminPortalApi.get('services', 'service-1')).resolves.toEqual(service);

    expect(mocks.get).toHaveBeenCalledOnce();
    expect(mocks.get).toHaveBeenCalledWith('/api/services/admin');
  });

  it('reports when the selected service is not present in the admin catalog', async () => {
    mocks.get.mockResolvedValue({ data: [] });

    await expect(adminPortalApi.get('services', 'missing')).rejects.toThrow('Service not found.');
  });
});

describe('adminPortalApi payment directory', () => {
  it('loads payments in one request and formats each row using its returned currency', async () => {
    mocks.get.mockResolvedValue({ data: [{
      paymentId: 'payment-1', invoiceId: 'invoice-1', customerId: 'customer-1', customerName: 'Mira Owner',
      invoiceNumber: 'PP-INV-USD-1', amount: 50, currencyCode: 'USD', paymentMethod: 'CARD', paymentStatus: 'PENDING',
      paymentDate: '2026-10-08T00:00:00Z',
    }] });

    const rows = await adminPortalApi.list('payments');

    expect(rows).toHaveLength(1);
    expect(rows[0]).toMatchObject({ id: 'payment-1', title: 'Mira Owner', primary: 'Mira Owner', secondary: 'PP-INV-USD-1 / CARD', type: 'CARD', status: 'PENDING', detailPath: '/staff/payments/payment-1' });
    expect(rows[0].amount).toMatch(/\$50\.00/);
    expect(mocks.get).toHaveBeenCalledTimes(1);
    expect(mocks.get).toHaveBeenCalledWith('/api/payments');
  });
});
