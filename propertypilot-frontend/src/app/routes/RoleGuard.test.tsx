import { afterEach, describe, expect, it, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';

const auth = vi.hoisted(() => ({ roles: [] as string[] }));

vi.mock('../../features/auth/AuthContext', () => ({
  useAuth: () => ({ user: { roles: auth.roles } }),
}));

import RoleGuard from './RoleGuard';

afterEach(() => {
  cleanup();
  auth.roles = [];
});

function renderProtectedPage() {
  return render(
    <MemoryRouter initialEntries={['/protected']}>
      <Routes>
        <Route element={<RoleGuard allowedRoles={['ADMIN', 'OPERATIONS']} />}>
          <Route path="/protected" element={<h1>Authorized workspace</h1>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );
}

describe('RoleGuard', () => {
  it('normalizes backend ROLE_ADMIN authorities and grants authorized routes', () => {
    auth.roles = ['ROLE_ADMIN'];
    renderProtectedPage();

    expect(screen.getByRole('heading', { name: 'Authorized workspace' })).toBeTruthy();
  });

  it('denies roles outside the route policy and explains missing roles', () => {
    auth.roles = ['ROLE_CUSTOMER'];
    renderProtectedPage();

    expect(screen.getByRole('alert').textContent).toContain('does not have an authorized role');
    expect(screen.queryByRole('heading', { name: 'Authorized workspace' })).toBeNull();
  });

  it('explains when the authenticated account has no assigned roles', () => {
    renderProtectedPage();

    expect(screen.getByRole('alert').textContent).toContain('No roles are assigned');
  });
});
