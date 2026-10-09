import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

const auth = vi.hoisted(() => ({ roles: ['OPERATIONS'] as string[] }));

vi.mock('../../features/auth/AuthContext', () => ({
  useAuth: () => ({ user: { fullName: 'Workspace User', email: 'user@example.test', roles: auth.roles }, signOut: vi.fn() }),
}));

import Header from './Header';

afterEach(() => {
  cleanup();
  auth.roles = ['OPERATIONS'];
});

describe('Header workspace identity', () => {
  it.each([
    { role: 'ADMIN', title: 'Admin console', description: 'Configuration, access, and business oversight' },
    { role: 'OPERATIONS', title: 'Operations', description: 'PropertyPilot operations workspace' },
    { role: 'AGENT', title: 'Field operations', description: 'Assigned work and visit execution' },
    { role: 'CUSTOMER', title: 'Customer portal', description: 'Your properties, services, and account' },
  ])('shows the $role workspace identity', ({ role, title, description }) => {
    auth.roles = [`ROLE_${role}`];
    render(<MemoryRouter initialEntries={['/staff']}><Header onMenuClick={() => undefined} /></MemoryRouter>);

    expect(screen.getByRole('group', { name: `${title} workspace` })).toBeTruthy();
    expect(screen.getByText(title)).toBeTruthy();
    expect(screen.getByRole('group', { name: `${title} workspace` }).textContent).toContain(description);
  });

  it('shows the current business section without exposing a record identifier', () => {
    auth.roles = ['ROLE_OPERATIONS'];
    render(<MemoryRouter initialEntries={['/staff/service-requests/req-123']}><Header onMenuClick={() => undefined} /></MemoryRouter>);

    expect(screen.getByText('Service request details')).toBeTruthy();
    expect(screen.queryByText('req-123')).toBeNull();
    expect(screen.getByText(/PropertyPilot operations workspace/)).toBeTruthy();
  });
});
