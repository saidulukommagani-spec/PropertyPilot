import { cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import AppRoutes from './AppRoutes';
import { AuthProvider } from '../../features/auth/AuthContext';
import { authApi } from '../../api/auth';

vi.mock('../../features/service-requests/pages/ServiceRequestListPage', () => ({
  default: () => <h1>Operations request list route</h1>,
}));
vi.mock('../../features/agent-operations/pages/AgentWorkQueuePage', () => ({
  default: () => <h1>Agent work queue route</h1>,
}));
vi.mock('../../features/customer-portal/pages/CustomerRequestListPage', () => ({
  default: () => <h1>Customer service request route</h1>,
}));
vi.mock('../../features/service-requests/pages/ServiceRequestDetailPage', () => ({
  default: () => <h1>Service request detail workspace</h1>,
}));
vi.mock('../../features/leads/pages/OperationsLeadQueuePage', () => ({
  default: () => <h1>Operations Lead queue route</h1>,
}));
vi.mock('../../features/leads/pages/LeadDetailPage', () => ({
  default: () => <h1>Lead details route</h1>,
}));

afterEach(() => {
  cleanup();
  window.sessionStorage.clear();
  vi.restoreAllMocks();
});

describe('AppRoutes', () => {
  it('loads the public sign-in page through the route-level lazy boundary', async () => {
    render(<MemoryRouter initialEntries={['/login']}><AuthProvider><AppRoutes /></AuthProvider></MemoryRouter>);

    expect(await screen.findByRole('heading', { name: 'Sign in' })).toBeTruthy();
  });

  it.each([
    { role: 'ROLE_OPERATIONS', path: '/service-requests', heading: 'Operations request list route' },
    { role: 'ROLE_OPERATIONS', path: '/operations/leads', heading: 'Operations Lead queue route' },
    { role: 'ROLE_ADMIN', path: '/operations/leads', heading: 'Operations Lead queue route' },
    { role: 'ROLE_AGENT', path: '/agent/work-queue', heading: 'Agent work queue route' },
    { role: 'ROLE_CUSTOMER', path: '/customer/service-requests', heading: 'Customer service request route' },
  ])('allows $role to reach its registered service route', async ({ role, path, heading }) => {
    window.sessionStorage.setItem('propertypilot.session.v1', JSON.stringify({
      accessToken: 'operations-session-token',
      expiresAt: Date.now() + 60_000,
    }));
    vi.spyOn(authApi, 'currentUser').mockResolvedValue({
      userId: 'route-user', fullName: 'Portal User', email: 'route-user@propertypilot.test',
      mobileNumber: '9000000000', status: 'ACTIVE', roles: [role],
    });

    render(<MemoryRouter initialEntries={[path]}><AuthProvider><AppRoutes /></AuthProvider></MemoryRouter>);

    expect(await screen.findByRole('heading', { name: heading })).toBeTruthy();
  });

  it('allows Operations to open a Lead detail from the queue route', async () => {
    window.sessionStorage.setItem('propertypilot.session.v1', JSON.stringify({
      accessToken: 'operations-session-token',
      expiresAt: Date.now() + 60_000,
    }));
    vi.spyOn(authApi, 'currentUser').mockResolvedValue({
      userId: 'operations-user', fullName: 'Operations User', email: 'operations@example.test',
      mobileNumber: '9000000000', status: 'ACTIVE', roles: ['ROLE_OPERATIONS'],
    });

    render(<MemoryRouter initialEntries={['/operations/leads/lead-reference']}><AuthProvider><AppRoutes /></AuthProvider></MemoryRouter>);

    expect(await screen.findByRole('heading', { name: 'Lead details route' })).toBeTruthy();
  });

  it.each(['ROLE_AGENT', 'ROLE_CUSTOMER'])('does not grant the Operations Lead queue to %s', async (role) => {
    window.sessionStorage.setItem('propertypilot.session.v1', JSON.stringify({
      accessToken: 'restricted-session-token',
      expiresAt: Date.now() + 60_000,
    }));
    vi.spyOn(authApi, 'currentUser').mockResolvedValue({
      userId: 'restricted-user', fullName: 'Restricted User', email: 'restricted@example.test',
      mobileNumber: '9000000000', status: 'ACTIVE', roles: [role],
    });

    render(<MemoryRouter initialEntries={['/operations/leads']}><AuthProvider><AppRoutes /></AuthProvider></MemoryRouter>);

    expect(await screen.findByText(/not have an authorized role for this page/i)).toBeTruthy();
    expect(screen.queryByRole('heading', { name: 'Operations Lead queue route' })).toBeNull();
  });

  it.each([
    { role: 'ROLE_OPERATIONS', path: '/staff/service-requests/request-reference' },
    { role: 'ROLE_AGENT', path: '/agent/service-requests/request-reference' },
  ])('allows $role to reach the detail workspace on its registered route', async ({ role, path }) => {
    window.sessionStorage.setItem('propertypilot.session.v1', JSON.stringify({
      accessToken: 'detail-session-token',
      expiresAt: Date.now() + 60_000,
    }));
    vi.spyOn(authApi, 'currentUser').mockResolvedValue({
      userId: 'detail-user', fullName: 'Portal User', email: 'detail-user@propertypilot.test',
      mobileNumber: '9000000000', status: 'ACTIVE', roles: [role],
    });

    render(<MemoryRouter initialEntries={[path]}><AuthProvider><AppRoutes /></AuthProvider></MemoryRouter>);

    expect(await screen.findByRole('heading', { name: 'Service request detail workspace' })).toBeTruthy();
  });

  it('does not grant the Operations staff detail route to a Customer role', async () => {
    window.sessionStorage.setItem('propertypilot.session.v1', JSON.stringify({
      accessToken: 'customer-session-token',
      expiresAt: Date.now() + 60_000,
    }));
    vi.spyOn(authApi, 'currentUser').mockResolvedValue({
      userId: 'customer-user', fullName: 'Portal User', email: 'customer@propertypilot.test',
      mobileNumber: '9000000000', status: 'ACTIVE', roles: ['ROLE_CUSTOMER'],
    });

    render(<MemoryRouter initialEntries={['/staff/service-requests/request-reference']}><AuthProvider><AppRoutes /></AuthProvider></MemoryRouter>);

    expect(await screen.findByText(/not have an authorized role for this page/i)).toBeTruthy();
    expect(screen.queryByRole('heading', { name: 'Service request detail workspace' })).toBeNull();
  });
});
